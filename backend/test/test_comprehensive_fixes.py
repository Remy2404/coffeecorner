#!/usr/bin/env python3
"""
Comprehensive test script to validate both profile update and cart authentication fixes.
Tests both /auth/profile and /users/profile endpoints, plus cart operations.
"""

import asyncio
import requests
import json
import logging
from datetime import datetime

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

# Configuration
BASE_URL = "http://localhost:8000"
TEST_EMAIL = "testuser@example.com"
TEST_PASSWORD = "password123"
TEST_NAME = "Test User Updated"

class CoffeeShopTester:
    def __init__(self):
        self.session = requests.Session()
        self.access_token = None
        self.user_id = None
        self.product_id = None
        
    def test_login(self):
        """Test user login and get access token"""
        logger.info("Testing user login...")
        
        try:
            response = self.session.post(
                f"{BASE_URL}/auth/login",
                data={
                    "email": TEST_EMAIL,
                    "password": TEST_PASSWORD
                }
            )
            
            if response.status_code == 200:
                data = response.json()
                self.access_token = data.get("access_token")
                if data.get("user"):
                    self.user_id = data["user"]["id"]
                logger.info("✅ Login successful")
                logger.info(f"Access token: {self.access_token[:20]}...")
                logger.info(f"User ID: {self.user_id}")
                return True
            else:
                logger.error(f"❌ Login failed: {response.status_code} - {response.text}")
                return False
                
        except Exception as e:
            logger.error(f"❌ Login error: {e}")
            return False
    
    def get_auth_headers(self):
        """Get authorization headers"""
        return {
            "Authorization": f"Bearer {self.access_token}",
            "Content-Type": "application/json"
        }
    
    def test_auth_profile_update(self):
        """Test profile update via /auth/profile endpoint"""
        logger.info("Testing /auth/profile endpoint...")
        
        try:
            update_data = {
                "name": f"{TEST_NAME} Auth",
                "phone": "+1234567890",
                "gender": "other"
            }
            
            response = self.session.put(
                f"{BASE_URL}/auth/profile",
                json=update_data,
                headers=self.get_auth_headers()
            )
            
            if response.status_code == 200:
                data = response.json()
                logger.info("✅ /auth/profile update successful")
                logger.info(f"Response: {json.dumps(data, indent=2)}")
                return True
            else:
                logger.error(f"❌ /auth/profile update failed: {response.status_code} - {response.text}")
                return False
                
        except Exception as e:
            logger.error(f"❌ /auth/profile error: {e}")
            return False
    
    def test_users_profile_update(self):
        """Test profile update via /users/profile endpoint (Android app endpoint)"""
        logger.info("Testing /users/profile endpoint...")
        
        try:
            update_data = {
                "full_name": f"{TEST_NAME} Users",
                "phone": "+0987654321",
                "gender": "male"
            }
            
            response = self.session.put(
                f"{BASE_URL}/users/profile",
                json=update_data,
                headers=self.get_auth_headers()
            )
            
            if response.status_code == 200:
                data = response.json()
                logger.info("✅ /users/profile update successful")
                logger.info(f"Response: {json.dumps(data, indent=2)}")
                return True
            else:
                logger.error(f"❌ /users/profile update failed: {response.status_code} - {response.text}")
                return False
                
        except Exception as e:
            logger.error(f"❌ /users/profile error: {e}")
            return False
    
    def get_sample_product(self):
        """Get a sample product for cart testing"""
        logger.info("Getting sample product...")
        
        try:
            response = self.session.get(f"{BASE_URL}/products")
            
            if response.status_code == 200:
                data = response.json()
                if data.get("data") and len(data["data"]) > 0:
                    self.product_id = data["data"][0]["id"]
                    logger.info(f"✅ Sample product found: {self.product_id}")
                    return True
            
            logger.error(f"❌ No products found: {response.status_code} - {response.text}")
            return False
            
        except Exception as e:
            logger.error(f"❌ Product fetch error: {e}")
            return False
    
    def test_cart_operations(self):
        """Test cart operations with authentication"""
        logger.info("Testing cart operations...")
        
        if not self.get_sample_product():
            return False
        
        # Test adding to cart
        try:
            add_data = {
                "product_id": self.product_id,
                "quantity": 2
            }
            
            response = self.session.post(
                f"{BASE_URL}/cart/add",
                json=add_data,
                headers=self.get_auth_headers()
            )
            
            if response.status_code == 200:
                data = response.json()
                logger.info("✅ Add to cart successful")
                logger.info(f"Response: {json.dumps(data, indent=2)}")
            else:
                logger.error(f"❌ Add to cart failed: {response.status_code} - {response.text}")
                return False
                
        except Exception as e:
            logger.error(f"❌ Add to cart error: {e}")
            return False
        
        # Test getting cart
        try:
            response = self.session.get(
                f"{BASE_URL}/cart",
                headers=self.get_auth_headers()
            )
            
            if response.status_code == 200:
                data = response.json()
                logger.info("✅ Get cart successful")
                logger.info(f"Cart items count: {len(data.get('data', []))}")
                return True
            else:
                logger.error(f"❌ Get cart failed: {response.status_code} - {response.text}")
                return False
                
        except Exception as e:
            logger.error(f"❌ Get cart error: {e}")
            return False
    
    def test_profile_retrieval(self):
        """Test retrieving current user profile"""
        logger.info("Testing profile retrieval...")
        
        try:
            response = self.session.get(
                f"{BASE_URL}/auth/me",
                headers=self.get_auth_headers()
            )
            
            if response.status_code == 200:
                data = response.json()
                logger.info("✅ Profile retrieval successful")
                logger.info(f"Current profile: {json.dumps(data, indent=2)}")
                return True
            else:
                logger.error(f"❌ Profile retrieval failed: {response.status_code} - {response.text}")
                return False
                
        except Exception as e:
            logger.error(f"❌ Profile retrieval error: {e}")
            return False
    
    def run_all_tests(self):
        """Run all tests in sequence"""
        logger.info("=" * 60)
        logger.info("STARTING COMPREHENSIVE COFFEE SHOP TESTS")
        logger.info("=" * 60)
        
        tests = [
            ("Login", self.test_login),
            ("Profile Retrieval", self.test_profile_retrieval),
            ("Auth Profile Update", self.test_auth_profile_update),
            ("Users Profile Update", self.test_users_profile_update),
            ("Cart Operations", self.test_cart_operations),
        ]
        
        passed = 0
        total = len(tests)
        
        for test_name, test_func in tests:
            logger.info(f"\n📝 Running: {test_name}")
            if test_func():
                passed += 1
                logger.info(f"✅ {test_name} PASSED")
            else:
                logger.error(f"❌ {test_name} FAILED")
        
        logger.info("\n" + "=" * 60)
        logger.info(f"TEST RESULTS: {passed}/{total} tests passed")
        logger.info("=" * 60)
        
        if passed == total:
            logger.info("🎉 ALL TESTS PASSED! The fixes are working correctly.")
        else:
            logger.error(f"⚠️  {total - passed} test(s) failed. Check the logs above.")
        
        return passed == total

def main():
    """Main test execution"""
    tester = CoffeeShopTester()
    success = tester.run_all_tests()
    
    if success:
        print("\n🚀 Ready to build and deploy!")
        print("Run: gradlew build")
    else:
        print("\n🔧 Some issues still need to be fixed.")
    
    return success

if __name__ == "__main__":
    main()