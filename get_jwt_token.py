#!/usr/bin/env python3
"""
Simple script to generate a valid JWT token for testing
"""
import sys
import os
sys.path.append('.')

from app.services.auth_service import AuthService
from datetime import datetime

def generate_test_token():
    """Generate a test JWT token"""
    print("=== JWT Token Generator ===")
    
    # Create test user data (this would normally come from Firebase)
    test_user_data = {
        "sub": "test@example.com",
        "user_id": "test_user_123",
        "firebase_uid": "firebase_test_uid",
        "email": "test@example.com",
        "exp": int((datetime.utcnow().timestamp() + 3600))  # Expires in 1 hour
    }
    
    print(f"Creating JWT token with data: {test_user_data}")
    
    # Generate the token
    token = AuthService.create_access_token(data=test_user_data)
    
    print(f"\n✅ JWT Token Generated Successfully!")
    print(f"Token: {token}")
    print(f"\n📋 Copy this token and paste it into Swagger HTTPBearer:")
    print(f"   Authorization: Bearer {token}")
    print(f"\n⏰ Token expires in 1 hour")
    print(f"🔗 Access Swagger UI at: http://localhost:8000/docs")
    
    return token

if __name__ == "__main__":
    try:
        generate_test_token()
    except Exception as e:
        print(f"❌ Error generating token: {e}")
        print("Make sure you're in the backend directory and dependencies are installed.")
