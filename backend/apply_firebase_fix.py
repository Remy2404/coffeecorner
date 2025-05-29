#!/usr/bin/env python3
"""
Apply Firebase UID Fix for Coffee Corner App

This script applies the database schema fix for Firebase authentication UIDs.
It executes the supabase_setup.sql file using the Supabase REST API.
"""

import os
import sys
import requests
import json
from dotenv import load_dotenv
import logging

# Configure logging
logging.basicConfig(
    level=logging.INFO, format="%(asctime)s - %(name)s - %(levelname)s - %(message)s"
)
logger = logging.getLogger(__name__)

# Load environment variables
load_dotenv()


def apply_firebase_fix():
    """Apply the Firebase UID fix using Supabase SQL API"""
    # Get Supabase credentials
    supabase_url = os.getenv("SUPABASE_URL")
    service_role_key = os.getenv("SUPABASE_SERVICE_ROLE_KEY")

    if not supabase_url or not service_role_key:
        logger.error(
            "SUPABASE_URL or SUPABASE_SERVICE_ROLE_KEY not found in environment variables"
        )
        return False

    logger.info(f"Connecting to Supabase at {supabase_url}")

    # Read the SQL file
    sql_file_path = os.path.join(os.path.dirname(__file__), "supabase_setup.sql")
    try:
        with open(sql_file_path, "r", encoding="utf-8") as file:
            sql_content = file.read()
    except Exception as e:
        logger.error(f"Failed to read SQL file: {e}")
        return False

    # Prepare headers for Supabase API call
    headers = {
        "Content-Type": "application/json",
        "apikey": service_role_key,
        "Authorization": f"Bearer {service_role_key}",
    }

    # Endpoint for Supabase SQL execution
    sql_url = f"{supabase_url}/rest/v1/rpc/exec_sql"
    # Execute SQL via Supabase REST API
    try:
        logger.info("Executing SQL to fix Firebase UID handling...")

        # For more granular control, we'll split the SQL into statements
        sql_statements = sql_content.split(";")
        success = True

        for i, statement in enumerate(sql_statements):
            if not statement.strip():
                continue

            statement = statement.strip() + ";"
            try:
                response = requests.post(
                    sql_url, headers=headers, json={"query": statement}
                )

                if response.status_code in (200, 201, 204):
                    logger.info(f"Statement {i+1}/{len(sql_statements)}: Success")
                else:
                    logger.error(
                        f"Statement {i+1}/{len(sql_statements)}: Failed with status {response.status_code}"
                    )
                    logger.error(f"Error: {response.text}")
                    logger.error(f"Failed SQL: {statement}")
                    success = False
                    break
            except Exception as e:
                logger.error(f"Statement {i+1}/{len(sql_statements)}: Exception: {e}")
                success = False
                break

        if success:
            logger.info("✅ Successfully applied Firebase UID fix!")
            return True
        else:
            logger.error("Failed to execute all SQL statements.")
            return False
    except Exception as e:
        logger.error(f"Error calling Supabase API: {e}")
        return False


def test_backend_connection():
    """Test if the backend API is running"""
    try:
        # Check the backend's status endpoint
        backend_url = os.getenv("BACKEND_URL", "http://localhost:8000")
        health_url = f"{backend_url}/health"

        logger.info(f"Testing backend connection at {health_url}")
        response = requests.get(health_url)

        if response.status_code == 200:
            logger.info(f"✅ Backend is running. Status: {response.status_code}")
            return True
        else:
            logger.warning(f"⚠️ Backend returned status code {response.status_code}")
            return False
    except Exception as e:
        logger.warning(f"⚠️ Could not connect to backend: {e}")
        return False


def check_authentication_endpoint():
    """Check if the Firebase authentication endpoint is working"""
    try:
        # Check the auth endpoint
        backend_url = os.getenv("BACKEND_URL", "http://localhost:8000")
        auth_url = f"{backend_url}/auth/status"

        logger.info(f"Testing authentication endpoint at {auth_url}")
        response = requests.get(auth_url)

        if response.status_code == 200:
            logger.info(
                f"✅ Authentication endpoint is working. Status: {response.status_code}"
            )
            logger.info(f"Response: {response.json()}")
            return True
        else:
            logger.warning(
                f"⚠️ Authentication endpoint returned status code {response.status_code}"
            )
            logger.warning(f"Response: {response.text}")
            return False
    except Exception as e:
        logger.warning(f"⚠️ Could not connect to authentication endpoint: {e}")
        return False


if __name__ == "__main__":
    print("=" * 50)
    print("🔄 APPLYING FIREBASE UID FIX")
    print("=" * 50)

    # First test if backend is running
    backend_running = test_backend_connection()

    # Apply the fix
    if apply_firebase_fix():
        print("\n✅ Database schema successfully updated to support Firebase UIDs")
        print("\n📋 Next steps:")
        print("1. Restart your backend API server")
        print("2. Test authentication with the Android app")
        print("3. Verify user profiles are created correctly")
    else:
        print("\n❌ Failed to apply the Firebase UID fix")
        print("\n📋 Manual steps to apply the fix:")
        print("1. Log into the Supabase dashboard")
        print("2. Go to the SQL Editor")
        print("3. Copy and paste the contents of supabase_setup.sql")
        print("4. Execute the SQL script")

    # If backend is running, check authentication endpoint
    if backend_running:
        print("\n🔍 Testing authentication endpoint...")
        check_authentication_endpoint()

    print("\nDone!")
