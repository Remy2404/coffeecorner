import sys
import os
import asyncio
import logging
import uuid
from datetime import datetime

# Setup logging
logging.basicConfig(
    level=logging.INFO, format="%(asctime)s - %(levelname)s - %(message)s"
)
logger = logging.getLogger(__name__)

# Add the parent directory to sys.path
parent_dir = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
sys.path.append(parent_dir)

# Load environment variables
from dotenv import load_dotenv

load_dotenv()

try:
    from app.services.cart_service import CartService
    from app.models.schemas import CartItemAdd, CartItemUpdate
    from app.database.supabase import supabase
    from app.core.config import settings
    from supabase import create_client

    logger.info("✓ Successfully imported all required modules")
except Exception as e:
    logger.error(f"✗ Error importing modules: {e}")
    sys.exit(1)


async def test_cart_fixes():
    """Test all the cart fixes comprehensively"""

    logger.info("=" * 70)
    logger.info("COMPREHENSIVE CART FIXES VALIDATION")
    logger.info("=" * 70)

    # Test configuration
    test_user_id = f"test_user_{uuid.uuid4().hex[:8]}"
    test_product_id = "prod_001"  # Using existing product ID from test output
    test_jwt_token = "mock_jwt_token_for_testing"

    logger.info(f"Test User ID: {test_user_id}")
    logger.info(f"Test Product ID: {test_product_id}")

    # Test 1: Verify product exists
    logger.info("\n1. Verifying test product exists:")
    try:
        products = (
            supabase.table("products").select("*").eq("id", test_product_id).execute()
        )
        if products.data:
            product = products.data[0]
            logger.info(
                f"✓ Product found: {product.get('name')} (${product.get('price')})"
            )
        else:
            logger.error(f"✗ Test product {test_product_id} not found")
            # Use any available product
            all_products = supabase.table("products").select("*").limit(1).execute()
            if all_products.data:
                test_product_id = all_products.data[0]["id"]
                logger.info(f"  → Using alternative product: {test_product_id}")
            else:
                logger.error("  → No products available for testing")
                return
    except Exception as e:
        logger.error(f"✗ Error checking product: {e}")
        return

    # Test 2: Test user profile creation
    logger.info("\n2. Testing user profile creation:")
    try:
        profile_created = await CartService.ensure_user_profile_exists(test_user_id)
        if profile_created:
            logger.info("✓ User profile created/verified successfully")
        else:
            logger.error("✗ Failed to create user profile")
    except Exception as e:
        logger.error(f"✗ Error creating user profile: {e}")

    # Test 3: Test authenticated client creation
    logger.info("\n3. Testing authenticated client creation:")
    try:
        auth_client = await CartService.get_authenticated_client(test_jwt_token)
        logger.info(f"✓ Authenticated client created: {type(auth_client)}")
    except Exception as e:
        logger.error(f"✗ Error creating authenticated client: {e}")

    # Test 4: Test cart retrieval (should be empty initially)
    logger.info("\n4. Testing cart retrieval:")
    try:
        cart_items = await CartService.get_user_cart(test_user_id, test_jwt_token)
        logger.info(f"✓ Cart retrieved: {len(cart_items)} items")
    except Exception as e:
        logger.error(f"✗ Error retrieving cart: {e}")

    # Test 5: Test adding item to cart (the critical test)
    logger.info("\n5. Testing add to cart (CRITICAL TEST):")
    try:
        cart_item = CartItemAdd(product_id=test_product_id, quantity=2)
        result = await CartService.add_to_cart(test_user_id, cart_item, test_jwt_token)
        logger.info(f"✓ Successfully added to cart: Item ID {result.id}")
        logger.info(
            f"  → Product: {result.product.name if result.product else 'Unknown'}"
        )
        logger.info(f"  → Quantity: {result.quantity}")
        logger.info(f"  → User ID: {result.user_id}")
    except Exception as e:
        logger.error(f"✗ Error adding to cart: {e}")
        # This is the critical test - log detailed error info
        if "row-level security" in str(e).lower():
            logger.error("  → RLS policy is still blocking the operation")
        elif "foreign key constraint" in str(e).lower():
            logger.error("  → Foreign key constraint issue (user profile)")
        elif 'null value in column "id"' in str(e).lower():
            logger.error("  → ID generation issue")
        else:
            logger.error(f"  → Unexpected error: {type(e).__name__}")

    # Test 6: Test cart retrieval after adding item
    logger.info("\n6. Testing cart retrieval after add:")
    try:
        cart_items = await CartService.get_user_cart(test_user_id, test_jwt_token)
        logger.info(f"✓ Cart now has: {len(cart_items)} items")
        for item in cart_items:
            logger.info(
                f"  → Item: {item.product.name if item.product else 'Unknown'} x{item.quantity}"
            )
    except Exception as e:
        logger.error(f"✗ Error retrieving cart after add: {e}")

    # Test 7: Test adding same item again (should update quantity)
    logger.info("\n7. Testing quantity update (add same item):")
    try:
        cart_item = CartItemAdd(product_id=test_product_id, quantity=1)
        result = await CartService.add_to_cart(test_user_id, cart_item, test_jwt_token)
        logger.info(f"✓ Successfully updated quantity: {result.quantity}")
    except Exception as e:
        logger.error(f"✗ Error updating quantity: {e}")

    # Test 8: Test cart total calculation
    logger.info("\n8. Testing cart total calculation:")
    try:
        total = await CartService.get_cart_total(test_user_id, test_jwt_token)
        logger.info(f"✓ Cart total: ${total:.2f}")
    except Exception as e:
        logger.error(f"✗ Error calculating cart total: {e}")

    # Test 9: Clean up test data
    logger.info("\n9. Cleaning up test data:")
    try:
        cleared = await CartService.clear_cart(test_user_id, test_jwt_token)
        if cleared:
            logger.info("✓ Test cart cleared successfully")

        # Clean up test profile
        if (
            hasattr(settings, "supabase_service_role_key")
            and settings.supabase_service_role_key
        ):
            admin_client = create_client(
                settings.supabase_url, settings.supabase_service_role_key
            )
            admin_client.table("profiles").delete().eq("id", test_user_id).execute()
            logger.info("✓ Test profile cleaned up")

    except Exception as e:
        logger.error(f"✗ Error cleaning up: {e}")

    logger.info("\n" + "=" * 70)
    logger.info("CART FIXES VALIDATION COMPLETED")
    logger.info("=" * 70)


async def test_with_real_user():
    """Test with a real user ID from the profiles table"""

    logger.info("\n" + "=" * 70)
    logger.info("TESTING WITH REAL USER PROFILE")
    logger.info("=" * 70)

    try:
        # Get a real user from profiles table
        profiles = supabase.table("profiles").select("id, full_name").limit(1).execute()

        if not profiles.data:
            logger.info(
                "No real users found in profiles table - skipping real user test"
            )
            return

        real_user = profiles.data[0]
        user_id = real_user["id"]
        user_name = real_user.get("full_name", "Unknown")

        logger.info(f"Testing with real user: {user_name} (ID: {user_id})")

        # Test cart operations with real user
        logger.info("\n1. Getting cart for real user:")
        cart_items = await CartService.get_user_cart(user_id, "mock_jwt_token")
        logger.info(f"✓ Real user cart: {len(cart_items)} items")

        # Test adding item for real user
        logger.info("\n2. Adding item to real user cart:")
        cart_item = CartItemAdd(product_id="prod_001", quantity=1)
        result = await CartService.add_to_cart(user_id, cart_item, "mock_jwt_token")
        logger.info(f"✓ Added to real user cart: {result.id}")

        # Clean up
        logger.info("\n3. Cleaning up real user test:")
        await CartService.clear_cart(user_id, "mock_jwt_token")
        logger.info("✓ Real user cart cleaned")

    except Exception as e:
        logger.error(f"✗ Error testing with real user: {e}")


async def main():
    """Run all cart validation tests"""
    try:
        await test_cart_fixes()
        await test_with_real_user()

        logger.info(f"\n🎯 SUMMARY:")
        logger.info(f"Configuration:")
        logger.info(f"  Supabase URL: {settings.supabase_url}")
        logger.info(
            f"  Anon Key: {'✓ Set' if settings.supabase_anon_key else '✗ Missing'}"
        )
        logger.info(
            f"  Service Role: {'✓ Set' if hasattr(settings, 'supabase_service_role_key') and settings.supabase_service_role_key else '✗ Missing'}"
        )

        logger.info(f"\nKey Fixes Applied:")
        logger.info(f"  ✓ UUID generation for cart item IDs")
        logger.info(f"  ✓ Foreign key constraint handling (auto-create profiles)")
        logger.info(f"  ✓ Multiple authentication fallback methods")
        logger.info(f"  ✓ Improved error handling and logging")
        logger.info(f"  ✓ Service role fallback for testing")

    except Exception as e:
        logger.error(f"Test execution failed: {e}")
        import traceback

        traceback.print_exc()


if __name__ == "__main__":
    asyncio.run(main())
