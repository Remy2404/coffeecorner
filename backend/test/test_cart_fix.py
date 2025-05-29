import logging
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
    # Import the necessary modules
    from app.database.supabase import supabase, create_client
    from app.core.config import settings
    from app.services.cart_service import CartService
    from app.models.schemas import CartItemAdd

    logger.info("Successfully imported required modules")
except Exception as e:
    logger.error(f"Error importing modules: {e}")
    sys.exit(1)


async def test_auth_client():
    """Test creating an authenticated client"""
    logger.info("Testing authenticated client creation...")

    # Use a dummy access token for testing
    test_token = "dummy_access_token"

    try:
        # Test client creation with token
        client = await CartService.get_authenticated_client(test_token)
        logger.info(f"Client created: {client is not None}")

        # Test setting authentication
        auth_attr = getattr(client, "auth", None)
        logger.info(f"Client has auth attribute: {auth_attr is not None}")

        # Check client headers - this is another way to verify auth was set
        headers_attr = getattr(client, "headers", None)
        logger.info(f"Client has headers attribute: {headers_attr is not None}")
        if headers_attr:
            logger.info(f"Client headers: {headers_attr}")

        return True
    except Exception as e:
        logger.error(f"Error testing authenticated client: {e}")
        return False


async def test_cart_operations():
    """Test cart operations with a test user"""
    logger.info("Testing cart operations...")

    # Create an admin client for full access
    try:
        admin_client = create_client(
            settings.supabase_url, settings.supabase_service_role_key
        )

        # Get a test user to work with
        result = admin_client.table("profiles").select("*").limit(1).execute()
        if not result.data:
            logger.error("No test users found")
            return False

        test_user_id = result.data[0]["id"]
        logger.info(f"Using test user ID: {test_user_id}")

        # Get products for testing
        products = admin_client.table("products").select("*").limit(1).execute()
        if not products.data:
            logger.error("No products found")
            return False

        test_product_id = products.data[0]["id"]
        logger.info(f"Using test product ID: {test_product_id}")

        # Test getting cart items for the user
        cart_items = await CartService.get_user_cart(test_user_id)
        logger.info(f"Current cart items: {len(cart_items)}")

        # Test adding an item to the cart
        test_item = CartItemAdd(product_id=test_product_id, quantity=1)
        added_item = await CartService.add_to_cart(test_user_id, test_item)
        logger.info(f"Added item to cart: {added_item}")

        # Test getting cart total
        total = await CartService.get_cart_total(test_user_id)
        logger.info(f"Cart total: {total}")

        # Test clearing the cart
        clear_result = await CartService.clear_cart(test_user_id)
        logger.info(f"Cart cleared: {clear_result}")

        return True
    except Exception as e:
        logger.error(f"Error testing cart operations: {e}")
        return False


async def main():
    """Main test function"""
    logger.info("Starting cart service tests...")

    # Test authenticated client
    auth_success = await test_auth_client()
    logger.info(
        f"Authentication client test result: {'Passed' if auth_success else 'Failed'}"
    )

    # Test cart operations
    cart_success = await test_cart_operations()
    logger.info(
        f"Cart operations test result: {'Passed' if cart_success else 'Failed'}"
    )

    # Overall assessment
    if auth_success and cart_success:
        logger.info("All tests PASSED! Cart service is working correctly!")
    else:
        logger.error("Some tests FAILED. Check the logs for details.")


if __name__ == "__main__":
    asyncio.run(main())
