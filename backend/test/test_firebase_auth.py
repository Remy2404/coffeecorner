#!/usr/bin/env python3
# filepath: c:\Users\User\Downloads\Telegram Desktop\Apsaraandroid\backend\test_firebase_auth.py
"""
Firebase Authentication Test for Coffee Corner App

This script tests the integration between Firebase authentication and the Supabase database.
It verifies that a Firebase UID can be properly saved and retrieved from the profiles table.
"""

import os
import sys
import json
import requests
from dotenv import load_dotenv
from supabase import create_client, Client

# Load environment variables
load_dotenv()

# Supabase credentials
SUPABASE_URL = os.getenv("SUPABASE_URL")
SUPABASE_KEY = os.getenv("SUPABASE_SERVICE_ROLE_KEY")

if not SUPABASE_URL or not SUPABASE_KEY:
    print(
        "Error: SUPABASE_URL and SUPABASE_SERVICE_ROLE_KEY must be set in the .env file"
    )
    sys.exit(1)

# Sample Firebase UID (similar format to real Firebase UIDs)
TEST_FIREBASE_UID = "V4HVeaXIhARgUD4VL4EIh5hf7X42"
TEST_EMAIL = "test_user@example.com"
TEST_NAME = "Test User"


def create_test_profile():
    """Create a test user profile with a Firebase UID format"""
    try:
        # Initialize Supabase client
        supabase = create_client(SUPABASE_URL, SUPABASE_KEY)
        print(f"✅ Connected to Supabase at {SUPABASE_URL}")

        # Check if the user already exists
        result = (
            supabase.table("profiles").select("*").eq("email", TEST_EMAIL).execute()
        )
        if result.data:
            print(f"⚠️ User with email {TEST_EMAIL} already exists. Deleting...")
            user_id = result.data[0]["id"]
            supabase.table("profiles").delete().eq("id", user_id).execute()
            print(f"🗑️ Deleted existing user with ID: {user_id}")

        # Insert a new test profile with Firebase UID
        result = (
            supabase.table("profiles")
            .insert(
                {"id": TEST_FIREBASE_UID, "email": TEST_EMAIL, "full_name": TEST_NAME}
            )
            .execute()
        )

        if result.data:
            print(
                f"✅ Successfully created user profile with Firebase UID: {TEST_FIREBASE_UID}"
            )
            print(f"📊 Database record: {json.dumps(result.data[0], indent=2)}")
            return True
        else:
            print(f"❌ Failed to create user profile")
            print(f"Error data: {result}")
            return False

    except Exception as e:
        print(f"❌ Error creating test profile: {str(e)}")
        return False


def verify_profile_retrieval():
    """Verify that the profile can be retrieved using the Firebase UID"""
    try:
        # Initialize Supabase client
        supabase = create_client(SUPABASE_URL, SUPABASE_KEY)

        # Retrieve the profile by Firebase UID
        result = (
            supabase.table("profiles").select("*").eq("id", TEST_FIREBASE_UID).execute()
        )

        if result.data:
            print(
                f"✅ Successfully retrieved profile with Firebase UID: {TEST_FIREBASE_UID}"
            )
            print(f"📊 Retrieved record: {json.dumps(result.data[0], indent=2)}")
            return True
        else:
            print(
                f"❌ Failed to retrieve profile with Firebase UID: {TEST_FIREBASE_UID}"
            )
            return False

    except Exception as e:
        print(f"❌ Error retrieving profile: {str(e)}")
        return False


def test_related_tables():
    """Test inserting records in related tables with the Firebase UID as foreign key"""
    try:
        # Initialize Supabase client
        supabase = create_client(SUPABASE_URL, SUPABASE_KEY)

        # Test 1: Insert a notification for the user
        notif_result = (
            supabase.table("notifications")
            .insert(
                {
                    "user_id": TEST_FIREBASE_UID,
                    "title": "Test Notification",
                    "message": "This is a test notification to verify Firebase UID works as a foreign key",
                }
            )
            .execute()
        )

        if notif_result.data:
            print(
                f"✅ Successfully created notification for Firebase UID: {TEST_FIREBASE_UID}"
            )
        else:
            print(f"❌ Failed to create notification")
            return False

        # Test 2: Verify we can retrieve the notification by user_id
        get_notif = (
            supabase.table("notifications")
            .select("*")
            .eq("user_id", TEST_FIREBASE_UID)
            .execute()
        )
        if get_notif.data:
            print(f"✅ Successfully retrieved notification by Firebase UID")
        else:
            print(f"❌ Failed to retrieve notification by Firebase UID")
            return False

        return True

    except Exception as e:
        print(f"❌ Error testing related tables: {str(e)}")
        return False


def cleanup():
    """Clean up test data"""
    try:
        # Initialize Supabase client
        supabase = create_client(SUPABASE_URL, SUPABASE_KEY)

        # Delete notifications first (due to foreign key constraints)
        supabase.table("notifications").delete().eq(
            "user_id", TEST_FIREBASE_UID
        ).execute()
        print(f"🧹 Deleted test notifications")

        # Delete test user
        supabase.table("profiles").delete().eq("id", TEST_FIREBASE_UID).execute()
        print(f"🧹 Deleted test user profile")

        return True

    except Exception as e:
        print(f"❌ Error during cleanup: {str(e)}")
        return False


if __name__ == "__main__":
    print("=" * 50)
    print("🔥 FIREBASE AUTHENTICATION INTEGRATION TEST")
    print("=" * 50)

    # Run tests
    print("\n📝 TEST 1: CREATE USER PROFILE WITH FIREBASE UID")
    if not create_test_profile():
        print("❌ TEST FAILED: Could not create user profile")
        sys.exit(1)

    print("\n📝 TEST 2: RETRIEVE USER PROFILE BY FIREBASE UID")
    if not verify_profile_retrieval():
        print("❌ TEST FAILED: Could not retrieve user profile by Firebase UID")
        sys.exit(1)

    print("\n📝 TEST 3: TEST FOREIGN KEY RELATIONSHIPS")
    if not test_related_tables():
        print("❌ TEST FAILED: Foreign key relationships not working properly")
        sys.exit(1)

    # All tests passed
    print("\n✅ ALL TESTS PASSED! Firebase UID integration is working correctly.")

    # Clean up
    print("\n🧹 CLEANING UP TEST DATA")
    cleanup()

    print(
        "\n✅ DONE! Firebase authentication is properly integrated with the database."
    )
    print("=" * 50)
