import sys
import os
import asyncio

# Setup logging
logging.basicConfig(
    level=logging.INFO, format="%(asctime)s - %(levelname)s - %(message)s"
)
logger = logging.getLogger(__name__)

# Add the parent directory to sys.path to allow importing app modules
parent_dir = os.path.abspath(os.path.join(os.path.dirname(__file__)))
sys.path.append(parent_dir)

# Load environment variables
from dotenv import load_dotenv

load_dotenv()

try:
    from app.database.supabase import supabase, create_client
    from app.core.config import settings

    logger.info("Successfully imported required modules")
except Exception as e:
    logger.error(f"Error importing modules: {e}")
    sys.exit(1)


def test_auth_methods():
    """Test different authentication methods with Supabase client"""
    logger.info("Testing different Supabase authentication methods...")

    # Dummy access token for testing
    test_token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token"

    try:
        # Create a new client
        client = create_client(settings.supabase_url, settings.supabase_anon_key)

        # Test 1: Check available auth methods
        auth_methods = [
            method for method in dir(client.auth) if not method.startswith("_")
        ]
        logger.info(f"Available auth methods: {auth_methods}")

        # Test 2: Try set_auth method
        try:
            client.auth.set_auth(test_token)
            logger.info("✓ set_auth method works")
        except Exception as e:
            logger.error(f"✗ set_auth method failed: {e}")

        # Test 3: Try set_session method
        try:
            client.auth.set_session(test_token, "refresh_token")
            logger.info("✓ set_session method works")
        except Exception as e:
            logger.error(f"✗ set_session method failed: {e}")

        # Test 4: Try setting headers directly
        try:
            # Check if client has headers attribute
            if hasattr(client, "headers"):
                client.headers["Authorization"] = f"Bearer {test_token}"
                logger.info("✓ Direct headers method works")
            else:
                logger.error("✗ Client doesn't have headers attribute")
        except Exception as e:
            logger.error(f"✗ Direct headers method failed: {e}")

        # Test 5: Try creating client with auth header in options
        try:
            from supabase import ClientOptions

            options = ClientOptions()
            options.headers = {"Authorization": f"Bearer {test_token}"}
            auth_client = create_client(
                settings.supabase_url, settings.supabase_anon_key, options
            )
            logger.info("✓ Client with auth options works")
        except Exception as e:
            logger.error(f"✗ Client with auth options failed: {e}")

        # Test 6: Check if client has postgrest_client
        try:
            if hasattr(client, "postgrest_client"):
                if hasattr(client.postgrest_client, "auth"):
                    client.postgrest_client.auth(test_token)
                    logger.info("✓ postgrest_client.auth method works")
                else:
                    logger.error("✗ postgrest_client doesn't have auth method")
            else:
                logger.error("✗ Client doesn't have postgrest_client")
        except Exception as e:
            logger.error(f"✗ postgrest_client.auth method failed: {e}")

    except Exception as e:
        logger.error(f"Error testing auth methods: {e}")


def main():
    """Main test function"""
    logger.info("Starting Supabase authentication method tests...")
    test_auth_methods()
    logger.info("Test completed!")


if __name__ == "__main__":
    main()
