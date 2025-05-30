import requests
import json


def test_profile_update_api():
    """Test the actual profile update API endpoint"""
    try:
        base_url = "http://localhost:8000"

        # Test user credentials
        email = "rosexmee1122@gmail.com"
        password = "12345678"
        print("=== Testing Profile Update API ===")
        # Step 1: Login to get JWT token
        print("\n--- Step 1: Login ---")
        login_data = {"email": email, "password": password}

        # Use form data for login
        login_response = requests.post(f"{base_url}/auth/login", data=login_data)
        print(f"Login status: {login_response.status_code}")

        if login_response.status_code != 200:
            print(f"Login failed: {login_response.text}")
            return

        login_result = login_response.json()
        access_token = login_result["data"]["access_token"]
        print("Login successful, token obtained")

        # Step 2: Update profile
        print("\n--- Step 2: Update Profile ---")
        headers = {
            "Authorization": f"Bearer {access_token}",
            "Content-Type": "application/json",
        }

        update_data = {
            "full_name": "Phon Ramy Updated",
            "phone": "9876543210",
            "gender": "other",
        }

        update_response = requests.put(
            f"{base_url}/users/profile", json=update_data, headers=headers
        )

        print(f"Update status: {update_response.status_code}")
        print(f"Update response: {update_response.text}")

        if update_response.status_code == 200:
            print("SUCCESS: Profile update worked!")
            update_result = update_response.json()
            updated_user = update_result["data"]
            print(f"Updated name: {updated_user['name']}")
            print(f"Updated phone: {updated_user['phone']}")
            print(f"Updated gender: {updated_user['gender']}")
        else:
            print(
                f"FAILED: Profile update failed with status {update_response.status_code}"
            )

    except Exception as e:
        print(f"Test failed: {e}")


if __name__ == "__main__":
    test_profile_update_api()
