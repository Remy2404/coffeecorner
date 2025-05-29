#!/usr/bin/env python3
"""
Direct database query test
"""

import os
import logging
from dotenv import load_dotenv
from supabase import create_client, Client

# Load environment variables
load_dotenv()

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

def test_direct_query():
    """Test direct Supabase queries"""
    try:
        supabase_url = os.getenv("SUPABASE_URL")
        supabase_key = os.getenv("SUPABASE_ANON_KEY")
        
        logger.info(f"Connecting to: {supabase_url}")
        
        # Create Supabase client
        supabase: Client = create_client(supabase_url, supabase_key)
        
        # Test products query
        logger.info("Querying products table...")
        response = supabase.table("products").select("*").execute()
        
        logger.info(f"Products query status: Success")
        logger.info(f"Number of products found: {len(response.data)}")
        
        if response.data:
            for i, product in enumerate(response.data[:3]):
                logger.info(f"Product {i+1}: {product.get('name')} - ${product.get('price')}")
        else:
            logger.warning("No products found in database")
            
        # Test categories
        logger.info("Getting unique categories...")
        response = supabase.table("products").select("category").execute()
        categories = list(set([p.get('category') for p in response.data if p.get('category')]))
        logger.info(f"Categories: {categories}")
        
        return True
        
    except Exception as e:
        logger.error(f"Database query failed: {e}")
        import traceback
        traceback.print_exc()
        return False

def main():
    """Main function"""
    logger.info("Testing direct database queries...")
    success = test_direct_query()
    if success:
        logger.info("Database queries successful!")
    else:
        logger.error("Database queries failed!")

if __name__ == "__main__":
    main()
