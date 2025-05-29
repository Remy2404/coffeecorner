#!/usr/bin/env python3
"""
Local testing script for Coffee Shop API
Run this before deploying to catch issues early
"""

import requests
import json
import time
import sys
import os
from pathlib import Path

# Add the app directory to Python path
backend_dir = Path(__file__).parent
sys.path.insert(0, str(backend_dir))


def test_endpoint(base_url, endpoint, expected_status=200):
    """Test a single endpoint"""
    url = f"{base_url}{endpoint}"
    try:
        print(f"Testing: {url}")
        response = requests.get(url, timeout=10)

        if response.status_code == expected_status:
            print(f"✓ {endpoint} - Status: {response.status_code}")
            try:
                data = response.json()
                if endpoint == "/debug/status":
                    # Pretty print status info
                    print("  System Status:")
                    if data.get("success") and data.get("data"):
                        status_data = data["data"]
                        print(f"    API Version: {status_data.get('api_version')}")
                        print(f"    Debug Mode: {status_data.get('debug_mode')}")
                        print(f"    Database: {status_data.get('database_status')}")
                        print(f"    Firebase: {status_data.get('firebase_status')}")

                        env_vars = status_data.get("environment_variables", {})
                        print("    Environment Variables:")
                        for key, value in env_vars.items():
                            print(f"      {key}: {value}")
                return True
            except json.JSONDecodeError:
                print(f"  Response: {response.text[:100]}...")
                return True
        else:
            print(f"✗ {endpoint} - Status: {response.status_code}")
            print(f"  Response: {response.text[:200]}...")
            return False

    except requests.exceptions.RequestException as e:
        print(f"✗ {endpoint} - Connection Error: {e}")
        return False


def main():
    """Run local API tests"""
    print("🧪 Testing Coffee Shop API Locally")
    print("=" * 50)

    # Test local server first
    base_url = "http://localhost:8000"

    # Check if server is running
    try:
        response = requests.get(f"{base_url}/health", timeout=5)
        print(f"✓ Server is running on {base_url}")
    except requests.exceptions.RequestException:
        print(f"✗ Server is not running on {base_url}")
        print("Please start the server first:")
        print("  python app/main.py")
        print("  or")
        print("  python start.py")
        sys.exit(1)

    # Test endpoints
    endpoints = [
        "/",
        "/health",
        "/debug/status",
        "/products",
        "/auth/check-auth",  # This should return 401 without auth
    ]

    results = []
    for endpoint in endpoints:
        expected_status = 401 if endpoint == "/auth/check-auth" else 200
        success = test_endpoint(base_url, endpoint, expected_status)
        results.append((endpoint, success))

    print("\n" + "=" * 50)
    print("📊 Test Results:")

    passed = sum(1 for _, success in results if success)
    total = len(results)

    for endpoint, success in results:
        status = "✓ PASS" if success else "✗ FAIL"
        print(f"  {endpoint:<20} {status}")

    print(f"\nOverall: {passed}/{total} tests passed")

    if passed == total:
        print("🎉 All tests passed! API is ready for deployment.")
        return 0
    else:
        print("❌ Some tests failed. Please fix issues before deploying.")
        return 1


if __name__ == "__main__":
    sys.exit(main())
