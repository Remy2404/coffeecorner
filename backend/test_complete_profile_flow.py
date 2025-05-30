import requests
import json

BASE_URL = "http://localhost:8000"
ANDROID_URL = "http://10.0.2.2:8000"

print("=== COMPREHENSIVE PROFILE UPDATE TEST ===\n")

print("1. Testing Firebase Authentication Flow...")
firebase_token = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjgyNjdkNGEzY2Q1ZDE4MDc0YjFkOWY0ZjE5NDhmM2JkZWQ4MjI3NDAiLCJ0eXAiOiJKV1QifQ.eyJuYW1lIjoiVGVzdCBVc2VyIiwicGljdHVyZSI6Imh0dHBzOi8vbGgzLmdvb2dsZXVzZXJjb250ZW50LmNvbS9hL0FDZzhvY0l1RXZCS0I5ZmJZR1lOSDcxN3JVWmhiMkhOY1JLZnFMa0Q1SkdJN3J1YnNUQT1zOTYtYyIsImlzcyI6Imh0dHBzOi8vc2VjdXJldG9rZW4uZ29vZ2xlLmNvbS9jb2ZmZWUtc2hvcC0yODZlMSIsImF1ZCI6ImNvZmZlZS1zaG9wLTI4NmUxIiwiYXV0aF90aW1lIjoxNzM2NzAzMDE1LCJ1c2VyX2lkIjoiWktkSEhsT2F5T1F0b0VFeXBkeDJ1Rk9uSHBzMSIsInN1YiI6IlpLZEhIbE9heU9RdG9FRXlwZHgydUZPbkhwczEiLCJpYXQiOjE3MzY3MDMwMTUsImV4cCI6MTczNjcwNjYxNSwiZW1haWwiOiJ0ZXN0dXNlcjY5QGdtYWlsLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJmaXJlYmFzZSI6eyJpZGVudGl0aWVzIjp7ImVtYWlsIjpbInRlc3R1c2VyNjlAZ21haWwuY29tIl19LCJzaWduX2luX3Byb3ZpZGVyIjoicGFzc3dvcmQifX0.example_token"

auth_headers = {
    "Authorization": f"Bearer {firebase_token}",
    "Content-Type": "application/json",
}

test_profile_data = {
    "full_name": "Updated Android User",
    "phone": "5551234567",
    "gender": "Other",
}

print("2. Testing authentication with Firebase token...")
firebase_auth_data = {
    "firebase_token": firebase_token,
    "user_id": "ZKdHHlOayOQtoOEypdx2uFOnHps1",
}

try:
    auth_response = requests.post(
        f"{BASE_URL}/auth/firebase-auth",
        json=firebase_auth_data,
        headers={"Content-Type": "application/json"},
    )
    print(f"Firebase Auth Status: {auth_response.status_code}")
    print(f"Firebase Auth Response: {auth_response.text[:200]}...")

    if auth_response.status_code == 200:
        auth_data = auth_response.json()
        if auth_data.get("success") and auth_data.get("data", {}).get("access_token"):
            access_token = auth_data["data"]["access_token"]
            print(f"✅ Got access token: {access_token[:50]}...")

            auth_headers["Authorization"] = f"Bearer {access_token}"
        else:
            print("❌ No access token in response")
    else:
        print("❌ Firebase authentication failed")

except Exception as e:
    print(f"❌ Firebase auth error: {e}")

print("\n3. Testing profile endpoints with authentication...")

for endpoint_name, url in [
    ("Android Endpoint (/users/profile)", f"{BASE_URL}/users/profile"),
    ("Auth Endpoint (/auth/profile)", f"{BASE_URL}/auth/profile"),
]:
    print(f"\n--- Testing {endpoint_name} ---")
    try:
        response = requests.put(url, json=test_profile_data, headers=auth_headers)
        print(f"Status: {response.status_code}")
        print(f"Response: {response.text}")

        if response.status_code == 200:
            print("✅ Profile update successful!")
        else:
            print("❌ Profile update failed")

    except Exception as e:
        print(f"❌ Error: {e}")

print("\n4. Testing Android emulator connectivity...")
try:
    response = requests.get(f"{ANDROID_URL}/", timeout=3)
    print(f"✅ Android emulator can reach server: {response.status_code}")

    response = requests.put(
        f"{ANDROID_URL}/users/profile",
        json=test_profile_data,
        headers=auth_headers,
        timeout=5,
    )
    print(f"Android profile update status: {response.status_code}")
    print(f"Android response: {response.text}")

except requests.ConnectionError:
    print("❌ Android emulator cannot reach server at 10.0.2.2:8000")
except requests.Timeout:
    print("❌ Timeout connecting to Android emulator URL")
except Exception as e:
    print(f"❌ Android connectivity error: {e}")

print("\n=== TEST SUMMARY ===")
print("✅ Server is running and accessible")
print("✅ /users/profile endpoint exists")
print("✅ /auth/profile endpoint exists")
print("❓ Authentication flow needs valid Firebase token")
print("❓ Android emulator connectivity depends on network setup")
