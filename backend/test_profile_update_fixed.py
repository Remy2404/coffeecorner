#!/usr/bin/env python3

"""
Test profile update endpoint to verify Android app compatibility
"""

import asyncio
import httpx
import json
import logging
from typing import Dict, Any

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Configuration
BASE_URL = "http://localhost:8000"
TEST_USER_EMAIL = "test@example.com"
TEST_USER_PASSWORD = "password123"
TEST_USER_NAME = "Test User"

async def create_test_user():
    """Create a test user for testing"""
    async with httpx.AsyncClient() as client:
        try:
            logger.info("👤 Creating test user...")
            response = await client.post(
                f"{BASE_URL}/auth/register",
                data={
                    "name": TEST_USER_NAME,
                    "email": TEST_USER_EMAIL,
                    "password": TEST_USER_PASSWORD
                }
            )
            
            if response.status_code == 200:
                logger.info("✅ Test user created successfully")
                return True
            elif response.status_code == 400:
                logger.info("ℹ️ Test user already exists, proceeding with login test")
                return True
            else:
                logger.error(f"❌ Failed to create test user: {response.status_code} - {response.text}")
                return False
                
        except Exception as e:
            logger.error(f"❌ Error creating test user: {e}")
            return False

async def test_profile_update_endpoint():
    """Test the /users/profile PUT endpoint that Android app uses"""
    
    async with httpx.AsyncClient() as client:
        try:
            logger.info("🧪 Starting Profile Update Endpoint Test")
            
            # Step 0: Create test user if needed
            await create_test_user()
            
            # Step 1: Login to get authentication token (using form data)
            logger.info("📝 Step 1: Authenticating user...")
            login_response = await client.post(
                f"{BASE_URL}/auth/login",
                data={
                    "email": TEST_USER_EMAIL,
                    "password": TEST_USER_PASSWORD
                }
            )
            
            if login_response.status_code != 200:
                logger.error(f"❌ Login failed: {login_response.status_code} - {login_response.text}")
                return False
                
            login_data = login_response.json()
            access_token = login_data["data"]["access_token"]
            logger.info("✅ Authentication successful")
            
            # Step 2: Test the /users/profile PUT endpoint (Android app endpoint)
            logger.info("📝 Step 2: Testing /users/profile PUT endpoint...")
            
            headers = {
                "Authorization": f"Bearer {access_token}",
                "Content-Type": "application/json"
            }
            
            # Test data that Android app would send
            profile_update_data = {
                "full_name": "Updated Test User",
                "phone": "+1234567890",
                "gender": "male"
            }
            
            update_response = await client.put(
                f"{BASE_URL}/users/profile",
                json=profile_update_data,
                headers=headers
            )
            
            logger.info(f"Response Status: {update_response.status_code}")
            logger.info(f"Response Body: {update_response.text}")
            
            if update_response.status_code == 200:
                response_data = update_response.json()
                if response_data.get("success"):
                    logger.info("✅ Profile update successful!")
                    logger.info(f"Updated user data: {json.dumps(response_data['data'], indent=2)}")
                    return True
                else:
                    logger.error(f"❌ Profile update failed: {response_data.get('message', 'Unknown error')}")
                    return False
            else:
                logger.error(f"❌ Profile update failed with status {update_response.status_code}")
                logger.error(f"Error details: {update_response.text}")
                return False
                
        except Exception as e:
            logger.error(f"❌ Test failed with exception: {e}")
            return False

async def test_both_endpoints():
    """Test both /auth/profile and /users/profile endpoints for comparison"""
    
    async with httpx.AsyncClient() as client:
        try:
            logger.info("🔍 Comparing both profile update endpoints")
            
            # Login first (using form data)
            login_response = await client.post(
                f"{BASE_URL}/auth/login",
                data={
                    "email": TEST_USER_EMAIL,
                    "password": TEST_USER_PASSWORD
                }
            )
            
            if login_response.status_code != 200:
                logger.error("❌ Login failed")
                return False
                
            login_data = login_response.json()
            access_token = login_data["data"]["access_token"]
            
            headers = {
                "Authorization": f"Bearer {access_token}",
                "Content-Type": "application/json"
            }
            
            # Test /auth/profile endpoint
            logger.info("📝 Testing /auth/profile endpoint...")
            auth_response = await client.put(
                f"{BASE_URL}/auth/profile",
                json={"full_name": "Auth Endpoint Test"},
                headers=headers
            )
            logger.info(f"/auth/profile response: {auth_response.status_code}")
            
            # Test /users/profile endpoint
            logger.info("📝 Testing /users/profile endpoint...")
            users_response = await client.put(
                f"{BASE_URL}/users/profile",
                json={"full_name": "Users Endpoint Test"},
                headers=headers
            )
            logger.info(f"/users/profile response: {users_response.status_code}")
            
            return auth_response.status_code == 200 and users_response.status_code == 200
            
        except Exception as e:
            logger.error(f"❌ Comparison test failed: {e}")
            return False

async def main():
    """Run all profile update tests"""
    logger.info("🚀 Starting Profile Update Tests")
    
    # Test 1: Basic profile update endpoint
    test1_result = await test_profile_update_endpoint()
    
    # Test 2: Compare both endpoints
    test2_result = await test_both_endpoints()
    
    logger.info("\n" + "="*50)
    logger.info("📊 TEST RESULTS SUMMARY")
    logger.info("="*50)
    logger.info(f"Profile Update Endpoint Test: {'✅ PASS' if test1_result else '❌ FAIL'}")
    logger.info(f"Endpoint Comparison Test: {'✅ PASS' if test2_result else '❌ FAIL'}")
    
    if test1_result and test2_result:
        logger.info("🎉 ALL TESTS PASSED - Profile update endpoint is working!")
    else:
        logger.info("❌ Some tests failed - Check logs for details")
    
    return test1_result and test2_result

if __name__ == "__main__":
    asyncio.run(main())
