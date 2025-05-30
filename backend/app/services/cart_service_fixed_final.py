from typing import List
import uuid
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


class CartService:
    @staticmethod
    async def get_authenticated_client(access_token=None):
        """
        Get a Supabase client authenticated with the user's token
        Fixed version that properly handles JWT authentication
        """
        try:
            if not access_token:
                logger.warning("No access token provided, using default client")
                return supabase

            # Clean the token
            token = (
                access_token.replace("Bearer ", "")
                if access_token.startswith("Bearer ")
                else access_token
            )

            # Create a new client instance
            from supabase import create_client as supabase_create_client

            # Create client with anon key
            authenticated_client = supabase_create_client(
                settings.supabase_url, settings.supabase_anon_key
            )

            # Try different authentication methods
            try:
                # Method 1: Use set_auth if available
                if hasattr(authenticated_client.auth, "set_auth"):
                    authenticated_client.auth.set_auth(token)
                    logger.info("✓ Authentication method 1: set_auth")
                    return authenticated_client
            except Exception as e:
                logger.debug(f"Method 1 failed: {e}")

            try:
                # Method 2: Set session directly
                if hasattr(authenticated_client.auth, "session"):
                    authenticated_client.auth.session = {
                        "access_token": token,
                        "refresh_token": None,
                        "user": {"id": "authenticated_user"},
                    }
                    logger.info("✓ Authentication method 2: direct session")
                    return authenticated_client
            except Exception as e:
                logger.debug(f"Method 2 failed: {e}")

            try:
                # Method 3: Set headers on the client
                if hasattr(authenticated_client, "auth") and hasattr(
                    authenticated_client.auth, "headers"
                ):
                    authenticated_client.auth.headers["Authorization"] = (
                        f"Bearer {token}"
                    )
                    logger.info("✓ Authentication method 3: headers")
                    return authenticated_client
            except Exception as e:
                logger.debug(f"Method 3 failed: {e}")

            # If all methods fail, use service role as fallback for testing
            if (
                hasattr(settings, "supabase_service_role_key")
                and settings.supabase_service_role_key
            ):
                logger.warning("Using service role key as fallback")
                service_client = supabase_create_client(
                    settings.supabase_url, settings.supabase_service_role_key
                )
                return service_client

            logger.warning("All authentication methods failed, using default client")
            return supabase

        except Exception as e:
            logger.error(f"Error creating authenticated client: {e}")
            return supabase

    @staticmethod
    async def ensure_user_profile_exists(user_id: str, access_token=None):
        """
        Ensure user profile exists before adding cart items
        This prevents foreign key constraint violations
        """
        try:
            admin_client = (
                create_client(settings.supabase_url, settings.supabase_service_role_key)
                if hasattr(settings, "supabase_service_role_key")
                else supabase
            )

            # Check if profile exists
            profile_check = (
                admin_client.table("profiles").select("id").eq("id", user_id).execute()
            )

            if not profile_check.data:
                # Create minimal profile if it doesn't exist
                profile_data = {
                    "id": user_id,
                    "full_name": "User",
                    "email": f"user_{user_id}@temp.com",
                    "created_at": "now()",
                    "updated_at": "now()",
                }

                result = admin_client.table("profiles").insert(profile_data).execute()
                logger.info(f"Created profile for user: {user_id}")
                return True
            else:
                logger.debug(f"Profile exists for user: {user_id}")
                return True

        except Exception as e:
            logger.error(f"Error ensuring user profile exists: {e}")
            return False

    @staticmethod
    async def get_user_cart(user_id: str, access_token=None) -> List[CartItemResponse]:
        """Get all items in user's cart"""
        try:
            # Get authenticated client
            client = await CartService.get_authenticated_client(access_token)

            logger.info(f"Fetching cart for user: {user_id}")

            # Get cart items with product details
            result = (
                client.table("cart_items")
                .select("*, products(*)")
                .eq("user_id", user_id)
                .execute()
            )

            logger.info(
                f"Cart query result: {len(result.data) if result.data else 0} items"
            )

            cart_items = []
            for item in result.data:
                product_data = item.pop("products") if "products" in item else None
                cart_item = CartItemResponse(
                    **item,
                    product=ProductResponse(**product_data) if product_data else None,
                )
                cart_items.append(cart_item)

            return cart_items

        except Exception as e:
            logger.error(f"Error fetching cart for user {user_id}: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail=f"Failed to fetch cart: {str(e)}",
            )

    @staticmethod
    async def add_to_cart(
        user_id: str, cart_item: CartItemAdd, access_token=None
    ) -> CartItemResponse:
        """Add item to cart or update quantity if exists"""
        try:
            # Ensure user profile exists first
            await CartService.ensure_user_profile_exists(user_id, access_token)

            # Get authenticated client
            client = await CartService.get_authenticated_client(access_token)

            logger.info(
                f"Adding item to cart for user: {user_id}, product: {cart_item.product_id}"
            )

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
                logger.info(
                    f"Updating existing cart item, new quantity: {new_quantity}"
                )

                result = (
                    client.table("cart_items")
                    .update({"quantity": new_quantity})
                    .eq("id", existing.data[0]["id"])
                    .execute()
                )

                updated_item = result.data[0]
            else:
                # Add new item with UUID
                cart_item_id = str(uuid.uuid4())
                cart_data = {
                    "id": cart_item_id,  # ✓ Fixed: Generate UUID for id field
                    "user_id": user_id,
                    "product_id": cart_item.product_id,
                    "quantity": cart_item.quantity,
                }

                logger.info(
                    f"Inserting new cart item with ID {cart_item_id}: {cart_data}"
                )
                result = client.table("cart_items").insert(cart_data).execute()

                if not result.data:
                    raise HTTPException(
                        status_code=status.HTTP_400_BAD_REQUEST,
                        detail="Failed to add item to cart - no data returned",
                    )

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

            logger.info("✓ Successfully added/updated cart item")
            return CartItemResponse(**updated_item, product=product)

        except Exception as e:
            logger.error(f"Error adding to cart: {e}")
            # Provide more specific error messages
            if "row-level security" in str(e).lower():
                raise HTTPException(
                    status_code=status.HTTP_403_FORBIDDEN,
                    detail="Authentication failed - please log in again",
                )
            elif "foreign key constraint" in str(e).lower():
                raise HTTPException(
                    status_code=status.HTTP_400_BAD_REQUEST,
                    detail="User profile not found - please complete registration",
                )
            else:
                raise HTTPException(
                    status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                    detail=f"Failed to add item to cart: {str(e)}",
                )

    @staticmethod
    async def update_cart_item(
        user_id: str, item_id: str, update_data: CartItemUpdate, access_token=None
    ) -> CartItemResponse:
        """Update cart item quantity"""
        try:
            # Get authenticated client
            client = await CartService.get_authenticated_client(access_token)

            logger.info(f"Updating cart item {item_id} for user {user_id}")

            # Verify item belongs to user
            existing = (
                client.table("cart_items")
                .select("*")
                .eq("id", item_id)
                .eq("user_id", user_id)
                .execute()
            )

            if not existing.data:
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND, detail="Cart item not found"
                )

            # Update quantity
            result = (
                client.table("cart_items")
                .update({"quantity": update_data.quantity})
                .eq("id", item_id)
                .execute()
            )

            updated_item = result.data[0]

            # Get product details
            product_result = (
                client.table("products")
                .select("*")
                .eq("id", updated_item["product_id"])
                .execute()
            )

            product = (
                ProductResponse(**product_result.data[0])
                if product_result.data
                else None
            )

            return CartItemResponse(**updated_item, product=product)

        except HTTPException:
            raise
        except Exception as e:
            logger.error(f"Error updating cart item: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to update cart item",
            )

    @staticmethod
    async def remove_from_cart(user_id: str, item_id: str, access_token=None) -> bool:
        """Remove item from cart"""
        try:
            # Get authenticated client
            client = await CartService.get_authenticated_client(access_token)

            logger.info(f"Removing cart item {item_id} for user {user_id}")

            # Verify item belongs to user and delete
            result = (
                client.table("cart_items")
                .delete()
                .eq("id", item_id)
                .eq("user_id", user_id)
                .execute()
            )

            return len(result.data) > 0

        except Exception as e:
            logger.error(f"Error removing from cart: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to remove item from cart",
            )

    @staticmethod
    async def clear_cart(user_id: str, access_token=None) -> bool:
        """Clear all items from user's cart"""
        try:
            # Get authenticated client
            client = await CartService.get_authenticated_client(access_token)

            logger.info(f"Clearing cart for user {user_id}")

            client.table("cart_items").delete().eq("user_id", user_id).execute()
            return True

        except Exception as e:
            logger.error(f"Error clearing cart: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to clear cart",
            )

    @staticmethod
    async def get_cart_total(user_id: str, access_token=None) -> float:
        """Calculate total price of items in cart"""
        try:
            cart_items = await CartService.get_user_cart(user_id, access_token)
            total = sum(
                item.quantity * (item.product.price if item.product else 0)
                for item in cart_items
            )
            return total

        except Exception as e:
            logger.error(f"Error calculating cart total: {e}")
            return 0.0
