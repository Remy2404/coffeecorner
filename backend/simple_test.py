import requests
import json

BASE_URL = "http://localhost:8000"

def test_endpoints():
    print("🧪 Testing Coffee Corner API Endpoints")
    
    # Test 1: Health check
    print("\n📝 Step 1: Health Check")
    try:
        response = requests.get(f"{BASE_URL}/health")
        print(f"Health check: {response.status_code} - {response.json()}")
    except Exception as e:
        print(f"Health check failed: {e}")
        return
    
    # Test 2: Create test user
    print("\n📝 Step 2: Creating test user")
    try:
        register_data = {
            "name": "Test User",
            "email": "test@example.com", 
            "password": "password123"
        }
        response = requests.post(f"{BASE_URL}/auth/register", data=register_data)
        print(f"Register: {response.status_code}")
        if response.status_code not in [200, 400]:  # 400 might mean user exists
            print(f"Register response: {response.text}")
    except Exception as e:
        print(f"Register failed: {e}")
    
    # Test 3: Login
    print("\n📝 Step 3: Login")
    try:
        login_data = {
            "email": "test@example.com",
            "password": "password123"
        }
        response = requests.post(f"{BASE_URL}/auth/login", data=login_data)
        print(f"Login: {response.status_code}")
        
        if response.status_code == 200:
            login_result = response.json()
            access_token = login_result["data"]["access_token"]
            print(f"✅ Login successful, got token")
            
            # Test 4: Test /users/profile endpoint
            print("\n📝 Step 4: Testing /users/profile PUT endpoint")
            headers = {
                "Authorization": f"Bearer {access_token}",
                "Content-Type": "application/json"
            }
            
            profile_data = {
                "full_name": "Updated Test User",
                "phone": "+1234567890",
                "gender": "male"
            }
            
            response = requests.put(f"{BASE_URL}/users/profile", 
                                  json=profile_data, 
                                  headers=headers)
            print(f"Profile update: {response.status_code}")
            
            if response.status_code == 200:
                result = response.json()
                print("✅ Profile update successful!")
                print(f"Updated data: {json.dumps(result.get('data', {}), indent=2)}")
            else:
                print(f"❌ Profile update failed: {response.text}")
                
        else:
            print(f"❌ Login failed: {response.text}")
            
    except Exception as e:
        print(f"Test failed: {e}")

if __name__ == "__main__":
    test_endpoints()
