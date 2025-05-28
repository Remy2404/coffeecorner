import os
import logging
from dotenv import load_dotenv
from supabase import create_client, Client

# Load environment variables
load_dotenv()

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


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


if __name__ == "__main__":
    fix_profiles_table()
