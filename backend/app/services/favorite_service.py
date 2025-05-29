from typing import List
from fastapi import HTTPException, status
from app.database.supabase import supabase
from app.models.schemas import FavoriteResponse, ProductResponse
import logging

logger = logging.getLogger(__name__)


class FavoriteService:

    @staticmethod
    async def get_user_favorites(user_id: str) -> List[FavoriteResponse]:
        """Get user's favorite products"""
        try:
            result = (
                supabase.table("favorites")
                .select("*, products(*)")
                .eq("user_id", user_id)
                .execute()
            )

            favorites = []
            for item in result.data:
                product_data = item.pop("products")
                favorite = FavoriteResponse(
                    **item,
                    product=ProductResponse(**product_data) if product_data else None,
                )
                favorites.append(favorite)

            return favorites

        except Exception as e:
            logger.error(f"Error fetching favorites for user {user_id}: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to fetch favorites",
            )

    @staticmethod
    async def add_to_favorites(user_id: str, product_id: str) -> FavoriteResponse:
        """Add product to favorites"""
        try:
            # Check if already in favorites
            existing = (
                supabase.table("favorites")
                .select("*")
                .eq("user_id", user_id)
                .eq("product_id", product_id)
                .execute()
            )

            if existing.data:
                # Already in favorites, return existing
                product_result = (
                    supabase.table("products")
                    .select("*")
                    .eq("id", product_id)
                    .execute()
                )

                product = (
                    ProductResponse(**product_result.data[0])
                    if product_result.data
                    else None
                )
                return FavoriteResponse(**existing.data[0], product=product)

            # Add to favorites
            favorite_data = {"user_id": user_id, "product_id": product_id}

            result = supabase.table("favorites").insert(favorite_data).execute()

            if not result.data:
                raise HTTPException(
                    status_code=status.HTTP_400_BAD_REQUEST,
                    detail="Failed to add to favorites",
                )

            # Get product details
            product_result = (
                supabase.table("products").select("*").eq("id", product_id).execute()
            )

            product = (
                ProductResponse(**product_result.data[0])
                if product_result.data
                else None
            )

            return FavoriteResponse(**result.data[0], product=product)

        except HTTPException:
            raise
        except Exception as e:
            logger.error(f"Error adding to favorites: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to add to favorites",
            )

    @staticmethod
    async def remove_from_favorites(user_id: str, product_id: str) -> bool:
        """Remove product from favorites"""
        try:
            result = (
                supabase.table("favorites")
                .delete()
                .eq("user_id", user_id)
                .eq("product_id", product_id)
                .execute()
            )

            return len(result.data) > 0

        except Exception as e:
            logger.error(f"Error removing from favorites: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to remove from favorites",
            )

    @staticmethod
    async def is_favorite(user_id: str, product_id: str) -> bool:
        """Check if product is in user's favorites"""
        try:
            result = (
                supabase.table("favorites")
                .select("id")
                .eq("user_id", user_id)
                .eq("product_id", product_id)
                .execute()
            )

            return len(result.data) > 0

        except Exception as e:
            logger.error(f"Error checking favorite status: {e}")
            return False
