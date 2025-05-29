from typing import List
from fastapi import HTTPException, status
from app.database.supabase import supabase
from app.models.schemas import (
    CartItemAdd,
    CartItemUpdate,
    CartItemResponse,
    ProductResponse,
)
import logging

logger = logging.getLogger(__name__)


class CartService:

    @staticmethod
    async def get_user_cart(user_id: str) -> List[CartItemResponse]:
        """Get all items in user's cart"""
        try:
            # Get cart items with product details
            result = (
                supabase.table("cart_items")
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
    async def add_to_cart(user_id: str, cart_item: CartItemAdd) -> CartItemResponse:
        """Add item to cart or update quantity if exists"""
        try:
            # Check if item already exists in cart
            existing = (
                supabase.table("cart_items")
                .select("*")
                .eq("user_id", user_id)
                .eq("product_id", cart_item.product_id)
                .execute()
            )

            if existing.data:
                # Update existing item quantity
                new_quantity = existing.data[0]["quantity"] + cart_item.quantity
                result = (
                    supabase.table("cart_items")
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

                result = supabase.table("cart_items").insert(cart_data).execute()
                updated_item = result.data[0]

            # Get product details
            product_result = (
                supabase.table("products")
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
        user_id: str, item_id: str, update_data: CartItemUpdate
    ) -> CartItemResponse:
        """Update cart item quantity"""
        try:
            # Verify item belongs to user
            existing = (
                supabase.table("cart_items")
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
                supabase.table("cart_items")
                .update({"quantity": update_data.quantity})
                .eq("id", item_id)
                .execute()
            )

            updated_item = result.data[0]

            # Get product details
            product_result = (
                supabase.table("products")
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
    async def remove_from_cart(user_id: str, item_id: str) -> bool:
        """Remove item from cart"""
        try:
            # Verify item belongs to user and delete
            result = (
                supabase.table("cart_items")
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
    async def clear_cart(user_id: str) -> bool:
        """Clear all items from user's cart"""
        try:
            supabase.table("cart_items").delete().eq("user_id", user_id).execute()
            return True

        except Exception as e:
            logger.error(f"Error clearing cart: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to clear cart",
            )

    @staticmethod
    async def get_cart_total(user_id: str) -> float:
        """Calculate total price of items in cart"""
        try:
            cart_items = await CartService.get_user_cart(user_id)
            total = sum(
                item.quantity * (item.product.price if item.product else 0)
                for item in cart_items
            )
            return total

        except Exception as e:
            logger.error(f"Error calculating cart total: {e}")
            return 0.0
