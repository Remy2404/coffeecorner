#!/usr/bin/env python3
"""
Coffee Corner Backend Authentication Test Script
Tests the authentication endpoints to verify JWT functionality
"""

import requests
import json
import sys
from datetime import datetime

# Backend configuration
BASE_URL = "http://localhost:8000"
TEST_FIREBASE_TOKEN = "test_firebase_token_123"  # This would be a real Firebase token in practice

def test_authentication_flow():
    """Test the complete authentication flow"""
    print("=" * 60)
    print("Coffee Corner Authentication Test")
    print("=" * 60)
    print(f"Backend URL: {BASE_URL}")
    print(f"Time: {datetime.now()}")
    print()
    
    # Test 1: Health check
    print("1. Testing server health...")
    try:
        response = requests.get(f"{BASE_URL}/health", timeout=5)
        if response.status_code == 200:
            print("   ✓ Server is running")
        else:
            print(f"   ✗ Server health check failed: {response.status_code}")
            return False
    except requests.exceptions.RequestException as e:
        print(f"   ✗ Cannot connect to server: {e}")
        return False
    
    # Test 2: Check API documentation
    print("2. Testing API documentation...")
    try:
        response = requests.get(f"{BASE_URL}/docs", timeout=5)
        if response.status_code == 200:
            print("   ✓ API documentation accessible")
        else:
            print(f"   ✗ API docs not accessible: {response.status_code}")
    except requests.exceptions.RequestException as e:
        print(f"   ✗ API docs request failed: {e}")
    
    # Test 3: Test Firebase authentication endpoint
    print("3. Testing Firebase authentication endpoint...")
    try:
        auth_data = {
            "firebase_token": TEST_FIREBASE_TOKEN
        }
        response = requests.post(
            f"{BASE_URL}/auth/firebase-auth",
            data=auth_data,
            headers={"Content-Type": "application/x-www-form-urlencoded"},
            timeout=10
        )
        
        print(f"   Response status: {response.status_code}")
        
        if response.status_code == 200:
            result = response.json()
            print(f"   ✓ Authentication successful")
            print(f"   Response: {json.dumps(result, indent=2)}")
            
            # Extract JWT token if available
            if "access_token" in result:
                jwt_token = result["access_token"]
                print(f"   ✓ JWT token received: {jwt_token[:50]}...")
                
                # Test JWT endpoints
                test_jwt_endpoints(jwt_token)
            else:
                print("   ✗ No access_token in response")
                
        else:
            print(f"   ✗ Authentication failed: {response.status_code}")
            try:
                error_detail = response.json()
                print(f"   Error: {json.dumps(error_detail, indent=2)}")
            except:
                print(f"   Error text: {response.text}")
                
    except requests.exceptions.RequestException as e:
        print(f"   ✗ Authentication request failed: {e}")
    
    return True

def test_jwt_endpoints(jwt_token):
    """Test JWT-protected endpoints"""
    print("\n4. Testing JWT-protected endpoints...")
    
    headers = {
        "Authorization": f"Bearer {jwt_token}",
        "Content-Type": "application/json"
    }
    
    # Test cart endpoint
    print("   Testing cart endpoint...")
    try:
        response = requests.get(f"{BASE_URL}/cart", headers=headers, timeout=5)
        print(f"   Cart endpoint status: {response.status_code}")
        
        if response.status_code == 200:
            print("   ✓ Cart endpoint successful")
            result = response.json()
            print(f"   Cart response: {json.dumps(result, indent=2)}")
        else:
            print(f"   ✗ Cart endpoint failed: {response.status_code}")
            try:
                error_detail = response.json()
                print(f"   Cart error: {json.dumps(error_detail, indent=2)}")
            except:
                print(f"   Cart error text: {response.text}")
                
    except requests.exceptions.RequestException as e:
        print(f"   ✗ Cart request failed: {e}")
    
    # Test orders endpoint
    print("   Testing orders endpoint...")
    try:
        response = requests.get(f"{BASE_URL}/orders", headers=headers, timeout=5)
        print(f"   Orders endpoint status: {response.status_code}")
        
        if response.status_code == 200:
            print("   ✓ Orders endpoint successful")
            result = response.json()
            print(f"   Orders response: {json.dumps(result, indent=2)}")
        else:
            print(f"   ✗ Orders endpoint failed: {response.status_code}")
            try:
                error_detail = response.json()
                print(f"   Orders error: {json.dumps(error_detail, indent=2)}")
            except:
                print(f"   Orders error text: {response.text}")
                
    except requests.exceptions.RequestException as e:
        print(f"   ✗ Orders request failed: {e}")

def test_products_endpoint():
    """Test public products endpoint (no authentication required)"""
    print("\n5. Testing public endpoints...")
    try:
        response = requests.get(f"{BASE_URL}/products", timeout=5)
        print(f"   Products endpoint status: {response.status_code}")
        
        if response.status_code == 200:
            print("   ✓ Products endpoint successful")
            result = response.json()
            if isinstance(result, dict) and "data" in result:
                products = result["data"]
                print(f"   Products count: {len(products) if products else 0}")
            else:
                print(f"   Products response: {json.dumps(result, indent=2)}")
        else:
            print(f"   ✗ Products endpoint failed: {response.status_code}")
            
    except requests.exceptions.RequestException as e:
        print(f"   ✗ Products request failed: {e}")

def main():
    """Main test function"""
    try:
        # Run authentication flow test
        success = test_authentication_flow()
        
        # Test public endpoints
        test_products_endpoint()
        
        print("\n" + "=" * 60)
        if success:
            print("Authentication test completed.")
            print("Check the output above for any issues.")
        else:
            print("Authentication test failed - server not accessible.")
            sys.exit(1)
            
        print("\nTo test with the Android app:")
        print("1. Open the Coffee Corner app")
        print("2. Go to Profile tab")
        print("3. Tap profile picture 7 times")
        print("4. Select 'Run Auth Diagnostic'")
        print("5. Check Android Studio logcat for detailed output")
        print("=" * 60)
        
    except KeyboardInterrupt:
        print("\nTest interrupted by user")
        sys.exit(0)
    except Exception as e:
        print(f"\nUnexpected error: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()
