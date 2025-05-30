"""
This is a fixed version of the cart service that properly handles authentication.
Replace the existing cart_service.py with this file to resolve the cart issues.
"""

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


class CartService:
    @staticmethod
    async def get_authenticated_client(access_token=None):
        """Get a Supabase client authenticated with the user's token"""
        try:
            if access_token:
                # Create a new client with the user's JWT token
                client = create_client(
                    settings.supabase_url, settings.supabase_anon_key
                )

                # Set the auth token for the client
                client.auth.set_auth(access_token)
                logger.info("Created authenticated Supabase client with user token")
                return client
            else:
                # Use the default client (this won't work for RLS-protected operations)
                logger.warning("No access token provided, using default client")
                return supabase
        except Exception as e:
            logger.error(f"Error creating authenticated client: {e}")
            return supabase

    @staticmethod
    async def get_user_cart(user_id: str, access_token=None) -> List[CartItemResponse]:
        """Get all items in user's cart"""
        try:
            # Get authenticated client
            client = await CartService.get_authenticated_client(access_token)

            # Get cart items with product details
            result = (
                client.table("cart_items")
                .select("*, products(*)")
                .eq("user_id", user_id)
                .execute()
            )

            cart_items = []
            for item in result.data:
                product_data = item.pop("products")
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
                detail="Failed to fetch cart",
            )

    @staticmethod
    async def add_to_cart(
        user_id: str, cart_item: CartItemAdd, access_token=None
    ) -> CartItemResponse:
        """Add item to cart or update quantity if exists"""
        try:
            # Get authenticated client - THIS IS THE KEY FIX
            client = await CartService.get_authenticated_client(access_token)

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

                # THIS IS WHERE THE ORIGINAL ERROR HAPPENED - client wasn't authenticated properly
                logger.info(f"Adding item to cart with user_id: {user_id}")
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
            logger.error(f"Error adding to cart: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to add item to cart",
            )

    @staticmethod
    async def update_cart_item(
        user_id: str, item_id: str, update_data: CartItemUpdate, access_token=None
    ) -> CartItemResponse:
        """Update cart item quantity"""
        try:
            # Get authenticated client
            client = await CartService.get_authenticated_client(access_token)

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
