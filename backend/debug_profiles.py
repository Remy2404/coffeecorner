#!/usr/bin/env python
# Debug profiles update in the Coffee Corner app

import os
import logging
import sys
import json
import requests
from dotenv import load_dotenv

# Setup logging
logging.basicConfig(
    level=logging.INFO, format="%(asctime)s - %(levelname)s - %(message)s"
)
logger = logging.getLogger(__name__)

# Add the parent directory to sys.path to allow importing app modules
parent_dir = os.path.abspath(os.path.join(os.path.dirname(__file__)))
sys.path.append(parent_dir)

# Load environment variables from .env file if present
load_dotenv()


def test_profile_update():
    """Test profile update functionality"""
    logger.info("Testing profile update functionality...")

    try:
        # Replace with your actual API url and user credentials
        base_url = "http://localhost:8000"  # Or the actual API URL

        # Replace with a valid JWT token from your app logs
        jwt_token = input("Enter your JWT token from app logs: ")

        # Replace with a valid user ID from your app
        user_id = input("Enter your user ID: ")

        # 1. First, get the current profile
        headers = {"Authorization": f"Bearer {jwt_token}"}

        logger.info(f"Getting profile for user ID: {user_id}")
        response = requests.get(f"{base_url}/users/profile/{user_id}", headers=headers)

        if response.status_code == 200:
            profile_data = response.json()
            logger.info(f"Current profile data: {json.dumps(profile_data, indent=2)}")
        else:
            logger.error(f"Failed to get profile. Status code: {response.status_code}")
            logger.error(f"Response: {response.text}")
            return

        # 2. Now try to update the profile using backend field names
        update_data = {
            # Try with both 'name' and 'full_name' to see which works
            "name": "Updated Name",
            "full_name": "Updated Full Name",
            "phone": "1234567890",
            "gender": "Male",
        }

        logger.info(f"Updating profile with data: {json.dumps(update_data, indent=2)}")
        response = requests.put(
            f"{base_url}/auth/profile", json=update_data, headers=headers
        )

        if response.status_code == 200:
            update_result = response.json()
            logger.info(f"Profile update result: {json.dumps(update_result, indent=2)}")
        else:
            logger.error(
                f"Failed to update profile. Status code: {response.status_code}"
            )
            logger.error(f"Response: {response.text}")

        # 3. Verify profile was updated
        logger.info("Verifying profile was updated...")
        response = requests.get(f"{base_url}/users/profile/{user_id}", headers=headers)

        if response.status_code == 200:
            updated_profile = response.json()
            logger.info(
                f"Updated profile data: {json.dumps(updated_profile, indent=2)}"
            )
        else:
            logger.error(
                f"Failed to verify profile. Status code: {response.status_code}"
            )
            logger.error(f"Response: {response.text}")

    except Exception as e:
        logger.error(f"Error testing profile update: {e}")


def test_direct_database_update():
    """Test direct database update using Supabase client"""
    logger.info("Testing direct database update...")

    try:
        from app.database.supabase import supabase

        # Replace with a valid user ID
        user_id = input("Enter your user ID: ")

        # Check current data
        result = supabase.table("profiles").select("*").eq("id", user_id).execute()
        if result.data:
            logger.info(f"Current profile data: {json.dumps(result.data[0], indent=2)}")
        else:
            logger.error("User not found")
            return

        # Update directly in the database
        update_data = {
            "full_name": "Direct Database Update",
            "phone": "9876543210",
            "gender": "Other",
        }

        result = (
            supabase.table("profiles").update(update_data).eq("id", user_id).execute()
        )

        if result.data:
            logger.info(f"Update result: {json.dumps(result.data[0], indent=2)}")
        else:
            logger.error("Update failed")

    except ImportError:
        logger.error(
            "Could not import Supabase client. Run this script from the backend directory."
        )
    except Exception as e:
        logger.error(f"Error testing direct database update: {e}")


if __name__ == "__main__":
    logger.info("Starting profile debug tool...")

    print("Select an option:")
    print("1. Test profile update API")
    print("2. Test direct database update")

    choice = input("Enter your choice (1 or 2): ")

    if choice == "1":
        test_profile_update()
    elif choice == "2":
        test_direct_database_update()
    else:
        print("Invalid choice")
