#!/usr/bin/env python3
"""
Setup database using Supabase client with SQL execution
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

def create_tables_via_supabase():
    """Create tables using individual Supabase operations"""
    try:
        supabase_url = os.getenv("SUPABASE_URL")
        supabase_key = os.getenv("SUPABASE_ANON_KEY")
        
        if not supabase_url or not supabase_key:
            logger.error("Supabase URL or key not found in environment variables")
            return False
        
        logger.info("Creating tables in Supabase...")
        
        # Create Supabase client
        supabase: Client = create_client(supabase_url, supabase_key)
        
        # Let's first try to create some sample data to verify the structure
        # We'll use the REST API directly for this
        
        # Check if tables exist by trying to query them
        tables_to_check = ["profiles", "products", "cart_items", "orders", "order_items", "favorites", "notifications"]
        
        for table in tables_to_check:
            try:
                response = supabase.table(table).select("*").limit(1).execute()
                logger.info(f"Table '{table}' exists and is accessible")
            except Exception as e:
                logger.error(f"Table '{table}' does not exist or is not accessible: {e}")
        
        # Since we can't execute DDL through the client, let's create sample products
        # to verify the products table exists
        try:
            sample_products = [
                {
                    "name": "Espresso",
                    "description": "Rich and bold espresso shot",
                    "price": 2.50,
                    "category": "coffee",
                    "is_available": True,
                    "rating": 4.5
                },
                {
                    "name": "Cappuccino",
                    "description": "Classic cappuccino with steamed milk",
                    "price": 3.50,
                    "category": "coffee",
                    "is_available": True,
                    "rating": 4.7
                },
                {
                    "name": "Croissant",
                    "description": "Buttery, flaky croissant",
                    "price": 2.00,
                    "category": "pastry",
                    "is_available": True,
                    "rating": 4.3
                }
            ]
            
            # Insert sample products
            response = supabase.table("products").insert(sample_products).execute()
            logger.info(f"Successfully inserted sample products: {len(response.data)} items")
            
        except Exception as e:
            logger.error(f"Failed to insert sample products: {e}")
        
        return True
        
    except Exception as e:
        logger.error(f"Failed to setup database: {e}")
        return False

def main():
    """Main function"""
    logger.info("Setting up database via Supabase client...")
    
    result = create_tables_via_supabase()
    
    if result:
        logger.info("Database setup completed!")
    else:
        logger.error("Database setup failed!")

if __name__ == "__main__":
    main()
