#!/usr/bin/env python3
"""
Test script to verify the backend is working properly
"""

import asyncio
import json
import logging
import requests
from datetime import datetime

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

BASE_URL = "http://localhost:8000"


def test_api_endpoints():
    """Test various API endpoints"""
    logger.info("Testing Coffee Shop API endpoints...")

    # Test root endpoint
    try:
        response = requests.get(f"{BASE_URL}/")
        logger.info(f"Root endpoint: {response.status_code} - {response.json()}")
    except Exception as e:
        logger.error(f"Root endpoint failed: {e}")

    # Test health check
    try:
        response = requests.get(f"{BASE_URL}/health")
        logger.info(f"Health check: {response.status_code} - {response.json()}")
    except Exception as e:
        logger.error(f"Health check failed: {e}")

    # Test product categories
    try:
        response = requests.get(f"{BASE_URL}/products/categories")
        logger.info(f"Categories: {response.status_code} - {response.json()}")
    except Exception as e:
        logger.error(f"Categories failed: {e}")

    # Test all products
    try:
        response = requests.get(f"{BASE_URL}/products")
        data = response.json()
        logger.info(f"All products: {response.status_code}")
        if data.get("success"):
            products = data.get("data", [])
            logger.info(f"Found {len(products)} products")
            for product in products[:3]:  # Show first 3 products
                logger.info(f"  - {product.get('name')} (${product.get('price')})")
        else:
            logger.warning(f"Products request failed: {data}")
    except Exception as e:
        logger.error(f"Products failed: {e}")

    # Test search
    try:
        response = requests.get(f"{BASE_URL}/products/search?q=coffee")
        data = response.json()
        logger.info(f"Search coffee: {response.status_code}")
        if data.get("success"):
            results = data.get("data", [])
            logger.info(f"Search returned {len(results)} results")
        else:
            logger.warning(f"Search failed: {data}")
    except Exception as e:
        logger.error(f"Search failed: {e}")

    # Test category filter
    try:
        response = requests.get(f"{BASE_URL}/products/category/coffee")
        data = response.json()
        logger.info(f"Coffee category: {response.status_code}")
        if data.get("success"):
            products = data.get("data", [])
            logger.info(f"Coffee category has {len(products)} products")
        else:
            logger.warning(f"Category request failed: {data}")
    except Exception as e:
        logger.error(f"Category failed: {e}")


def test_auth_endpoints():
    """Test authentication endpoints"""
    logger.info("Testing authentication endpoints...")

    # Test registration (this will fail without proper data, but we can see the endpoint structure)
    try:
        response = requests.post(
            f"{BASE_URL}/auth/register",
            data={
                "name": "Test User",
                "email": "test@example.com",
                "password": "testpassword",
            },
        )
        logger.info(f"Registration test: {response.status_code}")
        if response.status_code != 200:
            logger.info(f"Registration response: {response.text[:200]}")
    except Exception as e:
        logger.error(f"Registration test failed: {e}")

    # Test login
    try:
        response = requests.post(
            f"{BASE_URL}/auth/login",
            data={"email": "test@example.com", "password": "testpassword"},
        )
        logger.info(f"Login test: {response.status_code}")
        if response.status_code != 200:
            logger.info(f"Login response: {response.text[:200]}")
    except Exception as e:
        logger.error(f"Login test failed: {e}")


def main():
    """Main function"""
    logger.info("Starting API tests...")
    logger.info(f"Base URL: {BASE_URL}")
    logger.info(f"Test started at: {datetime.now()}")

    test_api_endpoints()
    test_auth_endpoints()

    logger.info("API tests completed!")


if __name__ == "__main__":
    main()
