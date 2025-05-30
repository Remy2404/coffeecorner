import sys
import os
import asyncio
import logging
import json
from typing import Dict, Any

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
    from app.services.cart_service import CartService
    from app.models.schemas import CartItemAdd, CartItemUpdate
    from app.database.supabase import supabase
    from app.core.config import settings

    logger.info("Successfully imported required modules")
except Exception as e:
    logger.error(f"Error importing modules: {e}")
    sys.exit(1)


async def test_cart_authentication():
    """Test cart operations with and without authentication"""

    logger.info("=" * 60)
    logger.info("TESTING CART AUTHENTICATION AND FUNCTIONALITY")
    logger.info("=" * 60)

    # Test configuration
    test_user_id = "test_user_123"
    test_product_id = "1"  # Assuming product with ID 1 exists
    test_jwt_token = "test_jwt_token_here"  # This would be a real JWT in production

    # Test 1: Get cart without authentication
    logger.info("\n1. Testing get_user_cart without authentication:")
    try:
        cart_items = await CartService.get_user_cart(test_user_id)
        logger.info(f"✓ Get cart succeeded (no auth): {len(cart_items)} items")
    except Exception as e:
        logger.error(f"✗ Get cart failed (no auth): {e}")

    # Test 2: Get cart with mock authentication
    logger.info("\n2. Testing get_user_cart with mock authentication:")
    try:
        cart_items = await CartService.get_user_cart(test_user_id, test_jwt_token)
        logger.info(f"✓ Get cart succeeded (with auth): {len(cart_items)} items")
    except Exception as e:
        logger.error(f"✗ Get cart failed (with auth): {e}")

    # Test 3: Add item to cart without authentication
    logger.info("\n3. Testing add_to_cart without authentication:")
    try:
        cart_item = CartItemAdd(product_id=test_product_id, quantity=1)
        result = await CartService.add_to_cart(test_user_id, cart_item)
        logger.info(f"✓ Add to cart succeeded (no auth): {result}")
    except Exception as e:
        logger.error(f"✗ Add to cart failed (no auth): {e}")
        if "row-level security" in str(e).lower():
            logger.info(
                "  → This is expected - RLS should block unauthenticated requests"
            )

    # Test 4: Add item to cart with mock authentication
    logger.info("\n4. Testing add_to_cart with mock authentication:")
    try:
        cart_item = CartItemAdd(product_id=test_product_id, quantity=1)
        result = await CartService.add_to_cart(test_user_id, cart_item, test_jwt_token)
        logger.info(f"✓ Add to cart succeeded (with auth): {result}")
    except Exception as e:
        logger.error(f"✗ Add to cart failed (with auth): {e}")

    # Test 5: Check Supabase client setup
    logger.info("\n5. Testing Supabase client configuration:")
    try:
        authenticated_client = await CartService.get_authenticated_client(
            test_jwt_token
        )
        logger.info(f"✓ Authenticated client created successfully")
        logger.info(f"  → Client type: {type(authenticated_client)}")
        logger.info(f"  → Has auth: {hasattr(authenticated_client, 'auth')}")
    except Exception as e:
        logger.error(f"✗ Failed to create authenticated client: {e}")

    # Test 6: Check products table access
    logger.info("\n6. Testing products table access:")
    try:
        products = (
            supabase.table("products").select("id, name, price").limit(5).execute()
        )
        logger.info(f"✓ Products query succeeded: {len(products.data)} products found")
        for product in products.data:
            logger.info(
                f"  → Product: {product.get('name')} (ID: {product.get('id')}, Price: ${product.get('price')})"
            )
    except Exception as e:
        logger.error(f"✗ Products query failed: {e}")

    # Test 7: Check cart_items table structure
    logger.info("\n7. Testing cart_items table access:")
    try:
        # Try to get the table schema
        cart_items = supabase.table("cart_items").select("*").limit(1).execute()
        logger.info(
            f"✓ Cart items table accessible: {len(cart_items.data)} items found"
        )
        if cart_items.data:
            logger.info(
                f"  → Sample cart item structure: {list(cart_items.data[0].keys())}"
            )
    except Exception as e:
        logger.error(f"✗ Cart items table access failed: {e}")
        if "row-level security" in str(e).lower():
            logger.info("  → This might be expected due to RLS policies")

    logger.info("\n" + "=" * 60)
    logger.info("CART AUTHENTICATION TEST COMPLETED")
    logger.info("=" * 60)


async def test_with_service_role():
    """Test cart operations using service role key"""

    logger.info("\n" + "=" * 60)
    logger.info("TESTING WITH SERVICE ROLE KEY (ADMIN ACCESS)")
    logger.info("=" * 60)

    if (
        not hasattr(settings, "supabase_service_role_key")
        or not settings.supabase_service_role_key
    ):
        logger.warning("No service role key configured - skipping admin tests")
        return

    try:
        from supabase import create_client

        # Create admin client
        admin_client = create_client(
            settings.supabase_url, settings.supabase_service_role_key
        )

        logger.info("✓ Admin client created successfully")

        # Test products access with admin client
        products = admin_client.table("products").select("*").limit(3).execute()
        logger.info(f"✓ Admin products query: {len(products.data)} products")

        # Test cart_items access with admin client
        cart_items = admin_client.table("cart_items").select("*").limit(3).execute()
        logger.info(f"✓ Admin cart_items query: {len(cart_items.data)} items")

        # Test adding cart item with admin client
        test_cart_data = {
            "user_id": "admin_test_user",
            "product_id": "1",
            "quantity": 1,
        }

        # First, delete any existing test data
        admin_client.table("cart_items").delete().eq(
            "user_id", "admin_test_user"
        ).execute()

        # Add new test item
        result = admin_client.table("cart_items").insert(test_cart_data).execute()
        if result.data:
            logger.info(f"✓ Admin cart insert successful: {result.data[0]['id']}")

            # Clean up test data
            admin_client.table("cart_items").delete().eq(
                "user_id", "admin_test_user"
            ).execute()
            logger.info("✓ Test data cleaned up")
        else:
            logger.error("✗ Admin cart insert failed")

    except Exception as e:
        logger.error(f"✗ Service role test failed: {e}")


async def main():
    """Run all cart tests"""
    try:
        await test_cart_authentication()
        await test_with_service_role()

        logger.info("\n" + "=" * 60)
        logger.info("ALL TESTS COMPLETED")
        logger.info("=" * 60)

        # Print configuration summary
        logger.info(f"\nConfiguration Summary:")
        logger.info(f"Supabase URL: {settings.supabase_url}")
        logger.info(
            f"Anon Key: {'✓ Set' if settings.supabase_anon_key else '✗ Missing'}"
        )
        logger.info(
            f"Service Role Key: {'✓ Set' if hasattr(settings, 'supabase_service_role_key') and settings.supabase_service_role_key else '✗ Missing'}"
        )

    except Exception as e:
        logger.error(f"Test execution failed: {e}")
        import traceback

        traceback.print_exc()


if __name__ == "__main__":
    asyncio.run(main())
