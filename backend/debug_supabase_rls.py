#!/usr/bin/env python3
"""
Script to debug and verify Supabase RLS policies and authentication.
This helps identify why cart operations might be failing.
"""

import os
import asyncio
import logging
from supabase import create_client
from app.core.config import settings

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

async def test_supabase_auth():
    """Test Supabase authentication and RLS policies"""
    
    logger.info("Testing Supabase authentication and RLS policies...")
    
    try:
        # Create Supabase clients
        logger.info("Creating Supabase clients...")
        
        anon_client = create_client(settings.supabase_url, settings.supabase_anon_key)
        logger.info("✅ Anonymous client created")
        
        # Try to create an admin client if service key is available
        admin_client = None
        if hasattr(settings, 'supabase_service_role_key') and settings.supabase_service_role_key:
            admin_client = create_client(settings.supabase_url, settings.supabase_service_role_key)
            logger.info("✅ Admin client created")
        else:
            logger.warning("⚠️  No service role key found, using anon client for admin operations")
            admin_client = anon_client
        
        # Test 1: Check profiles table structure
        logger.info("\n🔍 Testing profiles table structure...")
        try:
            result = admin_client.table("profiles").select("*").limit(1).execute()
            logger.info(f"✅ Profiles table accessible, sample count: {len(result.data)}")
            if result.data:
                logger.info(f"Sample profile structure: {list(result.data[0].keys())}")
        except Exception as e:
            logger.error(f"❌ Profiles table error: {e}")
        
        # Test 2: Check cart_items table structure
        logger.info("\n🔍 Testing cart_items table structure...")
        try:
            result = admin_client.table("cart_items").select("*").limit(1).execute()
            logger.info(f"✅ Cart_items table accessible, sample count: {len(result.data)}")
            if result.data:
                logger.info(f"Sample cart_item structure: {list(result.data[0].keys())}")
        except Exception as e:
            logger.error(f"❌ Cart_items table error: {e}")
        
        # Test 3: Test user authentication
        logger.info("\n🔍 Testing user authentication...")
        test_email = "testuser@example.com"
        test_password = "password123"
        
        try:
            # Try to sign in
            auth_result = anon_client.auth.sign_in_with_password({
                "email": test_email, 
                "password": test_password
            })
            
            if auth_result.user:
                logger.info(f"✅ User authentication successful: {auth_result.user.id}")
                access_token = auth_result.session.access_token
                logger.info(f"✅ Access token obtained: {access_token[:20]}...")
                
                # Test 4: Test authenticated operations
                logger.info("\n🔍 Testing authenticated operations...")
                
                # Create authenticated client
                auth_client = create_client(settings.supabase_url, settings.supabase_anon_key)
                auth_client.auth.set_auth(access_token)
                
                # Test profile read with authentication
                try:
                    profile_result = auth_client.table("profiles").select("*").eq("id", auth_result.user.id).execute()
                    logger.info(f"✅ Authenticated profile read successful: {len(profile_result.data)} records")
                except Exception as e:
                    logger.error(f"❌ Authenticated profile read failed: {e}")
                
                # Test cart read with authentication
                try:
                    cart_result = auth_client.table("cart_items").select("*").eq("user_id", auth_result.user.id).execute()
                    logger.info(f"✅ Authenticated cart read successful: {len(cart_result.data)} items")
                except Exception as e:
                    logger.error(f"❌ Authenticated cart read failed: {e}")
                
                # Test cart write with authentication (this is where RLS usually fails)
                logger.info("\n🔍 Testing cart write operations (RLS critical test)...")
                try:
                    # Try to add a cart item
                    test_cart_item = {
                        "user_id": auth_result.user.id,
                        "product_id": "test-product-123",
                        "quantity": 1
                    }
                    
                    insert_result = auth_client.table("cart_items").insert(test_cart_item).execute()
                    
                    if insert_result.data:
                        logger.info("✅ Cart write operation successful!")
                        logger.info(f"Inserted cart item: {insert_result.data[0]}")
                        
                        # Clean up - delete the test item
                        auth_client.table("cart_items").delete().eq("id", insert_result.data[0]["id"]).execute()
                        logger.info("✅ Test cart item cleaned up")
                    else:
                        logger.error("❌ Cart write operation failed: No data returned")
                        
                except Exception as e:
                    logger.error(f"❌ Cart write operation failed: {e}")
                    logger.error("This is likely an RLS policy issue!")
                    
                    # Provide RLS debugging information
                    logger.info("\n🔧 RLS DEBUGGING SUGGESTIONS:")
                    logger.info("1. Check if RLS is enabled on cart_items table")
                    logger.info("2. Verify RLS policy allows INSERT for authenticated users")
                    logger.info("3. Check if the policy uses auth.uid() = user_id")
                    logger.info("4. Ensure the JWT token contains the correct user ID")
                
            else:
                logger.error("❌ User authentication failed: No user returned")
                
        except Exception as e:
            logger.error(f"❌ Authentication error: {e}")
            logger.info("Creating test user might be needed...")
            
            # Try to create test user
            try:
                signup_result = anon_client.auth.sign_up({
                    "email": test_email,
                    "password": test_password
                })
                if signup_result.user:
                    logger.info(f"✅ Test user created: {signup_result.user.id}")
                else:
                    logger.error("❌ Failed to create test user")
            except Exception as signup_error:
                logger.error(f"❌ Test user creation failed: {signup_error}")
        
        # Test 5: Check RLS policies (if admin client available)
        if admin_client and hasattr(settings, 'supabase_service_role_key'):
            logger.info("\n🔍 Checking RLS policies...")
            try:
                # This would require SQL access, so we'll just log the recommendation
                logger.info("🔧 To check RLS policies, run these SQL queries in Supabase dashboard:")
                logger.info("SELECT * FROM pg_policies WHERE tablename = 'cart_items';")
                logger.info("SELECT * FROM pg_policies WHERE tablename = 'profiles';")
            except Exception as e:
                logger.error(f"❌ RLS policy check error: {e}")
        
        logger.info("\n" + "="*60)
        logger.info("SUPABASE AUTHENTICATION TEST COMPLETE")
        logger.info("="*60)
        
    except Exception as e:
        logger.error(f"❌ Major error during Supabase testing: {e}")

def main():
    """Main execution"""
    print("🔍 SUPABASE AUTHENTICATION & RLS DEBUGGER")
    print("="*60)
    
    asyncio.run(test_supabase_auth())

if __name__ == "__main__":
    main()