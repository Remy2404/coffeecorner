import sys
import os
import logging

# Setup logging
logging.basicConfig(
    level=logging.INFO, format="%(asctime)s - %(levelname)s - %(message)s"
)
logger = logging.getLogger(__name__)

# Add the parent directory to sys.path
parent_dir = os.path.abspath(os.path.join(os.path.dirname(__file__)))
sys.path.append(parent_dir)

# Load environment variables
from dotenv import load_dotenv

load_dotenv()

try:
    from app.database.supabase import supabase
    from app.core.config import settings
    from supabase import create_client

    logger.info("Successfully imported required modules")
except Exception as e:
    logger.error(f"Error importing modules: {e}")
    sys.exit(1)


def check_table_schema():
    """Check the cart_items table schema"""
    try:
        # Use admin client to inspect schema
        admin_client = create_client(
            settings.supabase_url, settings.supabase_service_role_key
        )

        logger.info("Checking cart_items table schema...")

        # Try to get table info (this won't work directly, but we can try inserting with UUID)
        import uuid

        test_cart_data = {
            "id": str(uuid.uuid4()),  # Generate UUID for ID
            "user_id": "admin_test_user",
            "product_id": "prod_001",  # Use a valid product ID from the test
            "quantity": 1,
        }

        logger.info(f"Testing insert with UUID: {test_cart_data}")

        # Clean up any existing test data first
        admin_client.table("cart_items").delete().eq(
            "user_id", "admin_test_user"
        ).execute()

        # Try insert with UUID
        result = admin_client.table("cart_items").insert(test_cart_data).execute()

        if result.data:
            logger.info("✓ Cart item insert successful with UUID!")
            logger.info(f"Inserted item: {result.data[0]}")

            # Clean up
            admin_client.table("cart_items").delete().eq(
                "user_id", "admin_test_user"
            ).execute()
            logger.info("✓ Test data cleaned up")
        else:
            logger.error("✗ Cart item insert failed even with UUID")

    except Exception as e:
        logger.error(f"Schema check failed: {e}")


def check_rls_policies():
    """Check RLS policies on cart_items table"""
    try:
        logger.info("Checking RLS policies...")

        # Use admin client to check policies
        admin_client = create_client(
            settings.supabase_url, settings.supabase_service_role_key
        )

        # We can't directly query pg_policies, but we can test RLS behavior
        logger.info("Testing RLS behavior with different clients...")

        # Test 1: Admin client (should work)
        import uuid

        test_id = str(uuid.uuid4())
        admin_test_data = {
            "id": test_id,
            "user_id": "rls_test_admin",
            "product_id": "prod_001",
            "quantity": 1,
        }

        admin_result = (
            admin_client.table("cart_items").insert(admin_test_data).execute()
        )
        if admin_result.data:
            logger.info("✓ Admin client can insert (RLS bypassed)")
            # Clean up
            admin_client.table("cart_items").delete().eq("id", test_id).execute()

        # Test 2: Anon client (should fail)
        anon_client = create_client(settings.supabase_url, settings.supabase_anon_key)

        test_id2 = str(uuid.uuid4())
        anon_test_data = {
            "id": test_id2,
            "user_id": "rls_test_anon",
            "product_id": "prod_001",
            "quantity": 1,
        }

        try:
            anon_result = (
                anon_client.table("cart_items").insert(anon_test_data).execute()
            )
            logger.warning("⚠ Anon client can insert (RLS may not be working)")
        except Exception as e:
            logger.info("✓ Anon client blocked by RLS (expected)")
            logger.info(f"  Error: {e}")

    except Exception as e:
        logger.error(f"RLS check failed: {e}")


def main():
    """Run all checks"""
    logger.info("=" * 60)
    logger.info("CHECKING CART_ITEMS TABLE AND RLS")
    logger.info("=" * 60)

    check_table_schema()
    check_rls_policies()

    logger.info("\n" + "=" * 60)
    logger.info("CHECKS COMPLETED")
    logger.info("=" * 60)


if __name__ == "__main__":
    main()
