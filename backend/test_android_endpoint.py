import requests
import json

ANDROID_EMULATOR_URL = "http://10.0.2.2:8000"
LOCALHOST_URL = "http://localhost:8000"

test_data = {
    "full_name": "Android Test User",
    "phone": "9876543210"
}

print("Testing profile update with Android emulator URL...")

for base_url, name in [(ANDROID_EMULATOR_URL, "Android Emulator"), (LOCALHOST_URL, "Localhost")]:
    print(f"\n=== Testing {name} URL: {base_url} ===")
    
    try:
        response = requests.put(
            f"{base_url}/users/profile",
            json=test_data,
            headers={"Content-Type": "application/json"},
            timeout=5
        )
        print(f"Status: {response.status_code}")
        print(f"Response: {response.text}")
        
    except requests.ConnectionError:
        print("Connection error - server not reachable")
    except requests.Timeout:
        print("Timeout error")
    except Exception as e:
        print(f"Error: {e}")

print("\n=== Testing actual authentication endpoint that works ===")
try:
    response = requests.put(
        f"{LOCALHOST_URL}/auth/profile",
        json=test_data,
        headers={"Content-Type": "application/json"}
    )
    print(f"Status: {response.status_code}")
    print(f"Response: {response.text}")
except Exception as e:
    print(f"Error: {e}")
