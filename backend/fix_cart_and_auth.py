#!/usr/bin/env python
# Fix cart and authentication issue in the Coffee Corner app

import os
import logging
import sys
from dotenv import load_dotenv
import json

# Setup logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

# Add the parent directory to sys.path to allow importing app modules
parent_dir = os.path.abspath(os.path.join(os.path.dirname(__file__)))
sys.path.append(parent_dir)

# Load environment variables from .env file if present
load_dotenv()

try:
    from app.database.supabase import supabase, create_client
    from app.core.config import settings
    
    logger.info("Successfully imported backend modules")
except Exception as e:
    logger.error(f"Error importing backend modules: {e}")
    sys.exit(1)

def diagnose_supabase_client():
    """Diagnose the Supabase client configuration"""
    logger.info("Diagnosing Supabase client configuration...")
    
    # Check URL and key availability
    url = getattr(settings, "supabase_url", None)
    anon_key = getattr(settings, "supabase_anon_key", None)
    service_role_key = getattr(settings, "supabase_service_role_key", None)
    
    logger.info(f"SUPABASE_URL available: {bool(url)}")
    logger.info(f"SUPABASE_ANON_KEY available: {bool(anon_key)}")
    logger.info(f"SUPABASE_SERVICE_ROLE_KEY available: {bool(service_role_key)}")
    
    # Test basic client functionality
    try:
        result = supabase.table("profiles").select("count(*)").execute()
        logger.info(f"Supabase client test: {result.data}")
    except Exception as e:
        logger.error(f"Supabase client test failed: {e}")
    
    return {
        "url_available": bool(url),
        "anon_key_available": bool(anon_key),
        "service_role_key_available": bool(service_role_key)
    }

def test_cart_operations():
    """Test cart operations"""
    logger.info("Testing cart operations with admin client...")
    
    try:
        # Using service role key to bypass RLS
        admin_client = create_client(settings.supabase_url, settings.supabase_service_role_key)
        
        # Test fetching cart items
        result = admin_client.table("cart_items").select("*").limit(5).execute()
        logger.info(f"Cart items test: Found {len(result.data)} items")
        
        # Test user profile
        result = admin_client.table("profiles").select("*").limit(5).execute()
        logger.info(f"Profiles test: Found {len(result.data)} profiles")
        if result.data:
            logger.info(f"Sample profile: {json.dumps(result.data[0], default=str)}")
        
    except Exception as e:
        logger.error(f"Cart operations test failed: {e}")

def diagnose_auth_flow():
    """Diagnose authentication flow"""
    logger.info("Diagnosing authentication flow...")
    
    try:
        from app.services.auth_service import AuthService
        
        # Check if auth service is properly imported
        logger.info(f"AuthService available: {AuthService is not None}")
        
    except Exception as e:
        logger.error(f"Error accessing AuthService: {e}")

def fix_cart_service():
    """Try to fix issues with the cart service"""
    logger.info("Attempting to fix cart service...")
    
    try:
        # Examine cart_service.py
        from app.services.cart_service import CartService
        
        # Check if we can properly instantiate a cart service
        logger.info(f"CartService available: {CartService is not None}")
        
        # Check if get_user_cart method is available
        logger.info(f"get_user_cart method available: {'get_user_cart' in dir(CartService)}")
        
        # Check if add_to_cart method is available
        logger.info(f"add_to_cart method available: {'add_to_cart' in dir(CartService)}")
        
    except Exception as e:
        logger.error(f"Error accessing CartService: {e}")

def main():
    """Main function to diagnose and fix issues"""
    logger.info("Starting Coffee Corner App diagnostic tool...")
    
    # Run diagnostics
    client_status = diagnose_supabase_client()
    test_cart_operations()
    diagnose_auth_flow()
    fix_cart_service()
    
    logger.info("Diagnosis complete. Check logs for details.")
    
    # Summary
    if client_status["service_role_key_available"]:
        logger.info(
            "KEY FINDING: Service role key is available. The backend might be using "
            "it even for user operations, which would bypass Row Level Security "
            "but NOT provide user context for RLS policies."
        )
        logger.info(
            "SOLUTION: Ensure cart operations use a client authenticated as the user, "
            "not the service role key. The cart_service.py should be updated to use "
            "the user's JWT token for Supabase operations."
        )
    
    logger.info(
        "Run the SQL script fix_cart_and_profile.sql in the Supabase SQL Editor "
        "to ensure Row Level Security policies are correctly configured."
    )

if __name__ == "__main__":
    main()
