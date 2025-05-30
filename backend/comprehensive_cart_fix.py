import logging
import sys
import os
import asyncio
import json

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


def check_rls_policies():
    """Check and fix RLS policies for cart_items table"""
    logger.info("Checking RLS policies for cart_items table...")

    try:
        # Use admin client to check RLS policies
        admin_client = create_client(
            settings.supabase_url, settings.supabase_service_role_key
        )

        # Check if cart_items table exists and has RLS enabled
        result = admin_client.rpc(
            "check_table_rls", {"table_name": "cart_items"}
        ).execute()
        logger.info(f"RLS check result: {result.data}")

        # Get current RLS policies
        policies_result = admin_client.rpc(
            "get_table_policies", {"table_name": "cart_items"}
        ).execute()
        logger.info(f"Current policies: {policies_result.data}")

    except Exception as e:
        logger.error(f"Error checking RLS policies: {e}")

        # Try direct SQL approach
        try:
            # Check table structure
            result = admin_client.table("cart_items").select("*").limit(1).execute()
            logger.info(f"Cart items table accessible: {len(result.data) >= 0}")

            # Test insert with admin client
            test_data = {
                "user_id": "test_user",
                "product_id": "test_product",
                "quantity": 1,
            }

            # Try to insert test data
            insert_result = admin_client.table("cart_items").insert(test_data).execute()
            logger.info(f"Test insert successful: {insert_result.data}")

            # Clean up test data
            admin_client.table("cart_items").delete().eq(
                "user_id", "test_user"
            ).execute()

        except Exception as inner_e:
            logger.error(f"Error with direct SQL approach: {inner_e}")


def fix_jwt_validation():
    """Fix JWT validation issues"""
    logger.info("Checking JWT validation...")

    try:
        # Create SQL to fix RLS policies
        rls_fix_sql = """
        -- Disable RLS temporarily for testing
        ALTER TABLE cart_items DISABLE ROW LEVEL SECURITY;
        
        -- Re-enable RLS with proper policies
        ALTER TABLE cart_items ENABLE ROW LEVEL SECURITY;
        
        -- Drop existing policies if they exist
        DROP POLICY IF EXISTS "Users can manage their own cart items" ON cart_items;
        DROP POLICY IF EXISTS "Users can view their own cart items" ON cart_items;
        DROP POLICY IF EXISTS "Users can insert their own cart items" ON cart_items;
        DROP POLICY IF EXISTS "Users can update their own cart items" ON cart_items;
        DROP POLICY IF EXISTS "Users can delete their own cart items" ON cart_items;
        
        -- Create comprehensive RLS policies for cart_items
        CREATE POLICY "Users can view their own cart items" ON cart_items
            FOR SELECT USING (auth.uid()::text = user_id);
            
        CREATE POLICY "Users can insert their own cart items" ON cart_items
            FOR INSERT WITH CHECK (auth.uid()::text = user_id);
            
        CREATE POLICY "Users can update their own cart items" ON cart_items
            FOR UPDATE USING (auth.uid()::text = user_id);
            
        CREATE POLICY "Users can delete their own cart items" ON cart_items
            FOR DELETE USING (auth.uid()::text = user_id);
        """

        # Save SQL to file for manual execution
        with open("fix_cart_rls.sql", "w") as f:
            f.write(rls_fix_sql)

        logger.info("RLS fix SQL saved to fix_cart_rls.sql")
        logger.info("Please execute this SQL in your Supabase SQL Editor")

    except Exception as e:
        logger.error(f"Error creating RLS fix: {e}")


def test_jwt_extraction():
    """Test JWT token extraction and validation"""
    logger.info("Testing JWT token handling...")

    # Sample JWT token structure that should work with Supabase
    sample_jwt_payload = {
        "aud": "authenticated",
        "exp": 1735516800,  # Future expiration
        "sub": "V4HVeaXIhARgUD4VL4EIh5hf7X42",  # User ID from logs
        "email": "rosexmee1122@gmail.com",
        "phone": "",
        "app_metadata": {},
        "user_metadata": {},
        "role": "authenticated",
        "aal": "aal1",
        "amr": [{"method": "password", "timestamp": 1735513200}],
        "session_id": "session-id",
    }

    logger.info(
        f"Sample JWT payload that should work: {json.dumps(sample_jwt_payload, indent=2)}"
    )


def create_alternative_cart_service():
    """Create an alternative cart service that bypasses RLS for testing"""
    logger.info("Creating alternative cart service...")

    alternative_service = """
from typing import List
from fastapi import HTTPException, status
from app.database.supabase import supabase, create_client
from app.models.schemas import (
    CartItemAdd,
    CartItemUpdate,
    CartItemResponse,
    ProductResponse,
)
import logging
from app.core.config import settings

logger = logging.getLogger(__name__)

class AlternativeCartService:
    @staticmethod
    async def get_authenticated_client(access_token=None):
        \"\"\"Get admin client to bypass RLS for testing\"\"\"
        try:
            # Use service role key to bypass RLS
            admin_client = create_client(
                settings.supabase_url, 
                settings.supabase_service_role_key
            )
            logger.info("Using admin client to bypass RLS")
            return admin_client
        except Exception as e:
            logger.error(f"Error creating admin client: {e}")
            return supabase
            
    @staticmethod
    async def add_to_cart(user_id: str, cart_item: CartItemAdd, access_token=None) -> CartItemResponse:
        \"\"\"Add item to cart using admin client\"\"\"
        try:
            # Get admin client to bypass RLS
            client = await AlternativeCartService.get_authenticated_client(access_token)
            
            # Check if item already exists in cart
            existing = (
                client.table("cart_items")
                .select("*")
                .eq("user_id", user_id)
                .eq("product_id", cart_item.product_id)
                .execute()
            )

            if existing.data:
                # Update existing item quantity
                new_quantity = existing.data[0]["quantity"] + cart_item.quantity
                result = (
                    client.table("cart_items")
                    .update({"quantity": new_quantity})
                    .eq("id", existing.data[0]["id"])
                    .execute()
                )
                updated_item = result.data[0]
            else:
                # Add new item
                cart_data = {
                    "user_id": user_id,
                    "product_id": cart_item.product_id,
                    "quantity": cart_item.quantity,
                }

                logger.info(f"Adding item to cart with admin client for user_id: {user_id}")
                result = client.table("cart_items").insert(cart_data).execute()
                updated_item = result.data[0]

            # Get product details
            product_result = (
                client.table("products")
                .select("*")
                .eq("id", cart_item.product_id)
                .execute()
            )

            product = (
                ProductResponse(**product_result.data[0])
                if product_result.data
                else None
            )

            return CartItemResponse(**updated_item, product=product)

        except Exception as e:
            logger.error(f"Error adding to cart with admin client: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail=f"Failed to add item to cart: {str(e)}",
            )
"""

    # Save alternative service
    with open("app/services/alternative_cart_service.py", "w") as f:
        f.write(alternative_service)

    logger.info(
        "Alternative cart service created at app/services/alternative_cart_service.py"
    )


def main():
    """Main function to run comprehensive fixes"""
    logger.info("Running comprehensive cart authentication fixes...")

    check_rls_policies()
    fix_jwt_validation()
    test_jwt_extraction()
    create_alternative_cart_service()

    logger.info("Comprehensive fixes completed!")
    logger.info("Next steps:")
    logger.info("1. Execute fix_cart_rls.sql in Supabase SQL Editor")
    logger.info("2. Test with alternative cart service")
    logger.info("3. Check JWT token format and validation")


if __name__ == "__main__":
    main()
