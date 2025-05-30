import os
import logging
import sys
from dotenv import load_dotenv
from supabase import create_client, Client

# Load environment variables
load_dotenv()

# Configure logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

# Add the parent directory to sys.path to allow importing app modules
parent_dir = os.path.abspath(os.path.join(os.path.dirname(__file__)))
sys.path.append(parent_dir)


def fix_profiles_table():
    """Fix profiles table by ensuring all required columns exist"""
    try:
        supabase_url = os.getenv("SUPABASE_URL")
        supabase_key = os.getenv("SUPABASE_ANON_KEY")

        logger.info(f"Connecting to: {supabase_url}")

        # Create Supabase client
        supabase: Client = create_client(supabase_url, supabase_key)

        # First, let's see what columns currently exist by trying to query
        logger.info("Checking current profiles table structure...")

        try:
            # Try to query with all expected columns
            result = supabase.table("profiles").select("*").limit(1).execute()
            logger.info(f"Current profiles data: {result.data}")

            if result.data:
                logger.info(f"Current columns: {list(result.data[0].keys())}")

        except Exception as e:
            logger.error(f"Error querying profiles: {e}")

        # Try to create a test user profile with minimal data first
        logger.info("Testing user creation with minimal data...")

        test_user_data = {
            "firebase_uid": "test_uid_123",
            "email": "test@example.com",
            "full_name": "Test User",  # Use existing column name
        }

        try:
            # Delete test user if exists
            supabase.table("profiles").delete().eq(
                "email", "test@example.com"
            ).execute()

            # Create test user
            result = supabase.table("profiles").insert(test_user_data).execute()
            logger.info(f"Test user created: {result.data}")

            # Clean up
            supabase.table("profiles").delete().eq(
                "email", "test@example.com"
            ).execute()
            logger.info("Test user cleaned up")

        except Exception as e:
            logger.error(f"Error creating test user: {e}")

        return True

    except Exception as e:
        logger.error(f"Failed to fix profiles table: {e}")
        return False


def modify_user_profile():
    """Directly modify a user profile in the database to fix issues"""
    try:
        # Use the app's Supabase client
        try:
            from app.database.supabase import supabase
            logger.info("Using app's Supabase client")
        except ImportError:
            # Fall back to creating a new client
            logger.info("Creating a new Supabase client")
            supabase_url = os.getenv("SUPABASE_URL")
            supabase_key = os.getenv("SUPABASE_ANON_KEY")
            supabase = create_client(supabase_url, supabase_key)
        
        # Get user ID from input
        user_id = input("Enter user ID to fix (e.g., V4HVeaXIhARgUD4VL4EIh5hf7X42): ")
        
        # Check if profile exists
        result = supabase.table("profiles").select("*").eq("id", user_id).execute()
        if not result.data:
            logger.error(f"No profile found for user ID: {user_id}")
            return
            
        logger.info(f"Current profile data: {result.data[0]}")
        
        # Get full_name from input
        full_name = input("Enter name to set (leave empty to skip): ").strip()
        phone = input("Enter phone to set (leave empty to skip): ").strip()
        gender = input("Enter gender to set (leave empty to skip): ").strip()
        
        # Create update data dictionary
        update_data = {}
        if full_name:
            update_data["full_name"] = full_name
        if phone:
            update_data["phone"] = phone
        if gender:
            update_data["gender"] = gender
            
        if not update_data:
            logger.info("No data provided for update. Exiting.")
            return
            
        # Update profile
        logger.info(f"Updating profile with data: {update_data}")
        result = supabase.table("profiles").update(update_data).eq("id", user_id).execute()
        
        if result.data:
            logger.info(f"Profile updated successfully: {result.data[0]}")
        else:
            logger.error("Profile update failed")
            
    except Exception as e:
        logger.error(f"Error: {e}")

if __name__ == "__main__":
    print("Choose an action:")
    print("1. Fix profiles table structure")
    print("2. Modify a specific user profile")
    
    choice = input("Enter your choice (1 or 2): ")
    
    if choice == "1":
        fix_profiles_table()
    elif choice == "2":
        modify_user_profile()
    else:
        print("Invalid choice")
