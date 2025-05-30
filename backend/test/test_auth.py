#!/usr/bin/env python3
"""
Test script to verify JWT authentication flow
"""
import os
import sys
sys.path.append('.')

from app.services.auth_service import AuthService
from app.core.config import settings
import logging

# Set up logging
logging.basicConfig(level=logging.DEBUG)
logger = logging.getLogger(__name__)

def test_jwt_creation_and_verification():
    """Test JWT token creation and verification"""
    logger.info("Testing JWT token creation and verification...")
    
    # Test data
    test_user_data = {
        "sub": "test@example.com",
        "user_id": "test_user_123",
        "firebase_uid": "firebase_test_uid"
    }
    
    # Create token
    logger.info(f"Creating token with data: {test_user_data}")
    token = AuthService.create_access_token(data=test_user_data)
    logger.info(f"Created token: {token[:50]}...")
    
    # Verify token
    logger.info("Verifying token...")
    payload = AuthService.verify_token(token)
    logger.info(f"Decoded payload: {payload}")
    
    if payload:
        logger.info("✅ JWT creation and verification working correctly!")
        return token
    else:
        logger.error("❌ JWT verification failed!")
        return None

def test_get_current_user():
    """Test get_current_user method"""
    logger.info("Testing get_current_user method...")
    
    # Create a test token
    token = test_jwt_creation_and_verification()
    if not token:
        logger.error("Cannot test get_current_user without valid token")
        return
    
    try:
        import asyncio
        user = asyncio.run(AuthService.get_current_user(token))
        logger.info(f"✅ get_current_user returned: {user}")
    except Exception as e:
        logger.error(f"❌ get_current_user failed: {e}")

if __name__ == "__main__":
    logger.info("Starting authentication tests...")
    logger.info(f"JWT Secret Key: {settings.secret_key[:10]}...")
    logger.info(f"JWT Algorithm: {settings.algorithm}")
    logger.info(f"Token Expire Minutes: {settings.access_token_expire_minutes}")
    
    test_jwt_creation_and_verification()
    test_get_current_user()