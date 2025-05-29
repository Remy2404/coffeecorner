import requests

BASE_URL = "http://localhost:8000"

print("Testing available endpoints...")

print("\n1. Testing server status")
try:
    response = requests.get(f"{BASE_URL}/")
    print(f"Status: {response.status_code}")
    print(f"Response: {response.text[:200]}")
except Exception as e:
    print(f"Error: {e}")

print("\n2. Testing docs endpoint")
try:
    response = requests.get(f"{BASE_URL}/docs")
    print(f"Status: {response.status_code}")
    print(f"Response type: {response.headers.get('content-type', 'unknown')}")
except Exception as e:
    print(f"Error: {e}")

print("\n3. Testing openapi.json to see available routes")
try:
    response = requests.get(f"{BASE_URL}/openapi.json")
    if response.status_code == 200:
        data = response.json()
        paths = data.get("paths", {})
        user_routes = [path for path in paths.keys() if "/users" in path]
        print(f"User routes found: {user_routes}")
        
        if "/users/profile" in paths:
            methods = list(paths["/users/profile"].keys())
            print(f"Methods for /users/profile: {methods}")
    else:
        print(f"Status: {response.status_code}")
except Exception as e:
    print(f"Error: {e}")
