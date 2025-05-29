import requests
import json

BASE_URL = "http://localhost:8000"

print("Testing Profile Update Endpoints...")

test_data = {"full_name": "Test User Update", "phone": "1234567890"}

print("\n1. Testing PUT /users/profile (Android app endpoint)")
try:
    response = requests.put(
        f"{BASE_URL}/users/profile",
        json=test_data,
        headers={"Content-Type": "application/json"},
    )
    print(f"Status: {response.status_code}")
    print(f"Response: {response.text}")
except Exception as e:
    print(f"Error: {e}")

print("\n2. Testing PUT /auth/profile (Working endpoint)")
try:
    response = requests.put(
        f"{BASE_URL}/auth/profile",
        json=test_data,
        headers={"Content-Type": "application/json"},
    )
    print(f"Status: {response.status_code}")
    print(f"Response: {response.text}")
except Exception as e:
    print(f"Error: {e}")

print("\n3. Testing with mock Authorization header")
try:
    response = requests.put(
        f"{BASE_URL}/users/profile",
        json=test_data,
        headers={
            "Content-Type": "application/json",
            "Authorization": "Bearer mock_token",
        },
    )
    print(f"Status: {response.status_code}")
    print(f"Response: {response.text}")
except Exception as e:
    print(f"Error: {e}")
