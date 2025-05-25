#!/usr/bin/env python3
"""
Debug script to test ProductService directly
"""

import asyncio
import sys
import os
import logging

# Add the app directory to Python path
sys.path.append(os.path.join(os.path.dirname(__file__), 'app'))

from dotenv import load_dotenv
load_dotenv()

from app.services.product_service import ProductService

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

async def test_product_service():
    """Test ProductService methods directly"""
    logger.info("Testing ProductService...")
    
    try:
        # Test get_categories
        logger.info("Testing get_categories...")
        categories = await ProductService.get_categories()
        logger.info(f"Categories: {categories}")
        
        # Test get_all_products
        logger.info("Testing get_all_products...")
        products = await ProductService.get_all_products()
        logger.info(f"Found {len(products)} products")
        
        if products:
            logger.info(f"First product: {products[0]}")
        
        # Test search
        logger.info("Testing search_products...")
        search_results = await ProductService.search_products("coffee")
        logger.info(f"Search results for 'coffee': {len(search_results)} products")
        
        # Test category filter
        logger.info("Testing get_products_by_category...")
        coffee_products = await ProductService.get_products_by_category("coffee")
        logger.info(f"Coffee products: {len(coffee_products)} products")
        
    except Exception as e:
        logger.error(f"Error testing ProductService: {e}")
        import traceback
        traceback.print_exc()

def main():
    """Main function"""
    logger.info("Starting ProductService tests...")
    asyncio.run(test_product_service())
    logger.info("ProductService tests completed!")

if __name__ == "__main__":
    main()
