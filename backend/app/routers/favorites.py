from fastapi import APIRouter, Depends, HTTPException, status
from app.models.schemas import ApiResponse
from app.services.favorite_service import FavoriteService
from app.routers.auth import get_current_user, UserResponse

router = APIRouter(prefix="/favorites", tags=["Favorites"])


@router.get("", response_model=ApiResponse)
async def get_favorites(current_user: UserResponse = Depends(get_current_user)):
    """Get user's favorite products"""
    favorites = await FavoriteService.get_user_favorites(current_user.id)
    return ApiResponse(
        success=True, message="Favorites retrieved successfully", data=favorites
    )


@router.post("/{product_id}", response_model=ApiResponse)
async def add_to_favorites(
    product_id: str, current_user: UserResponse = Depends(get_current_user)
):
    """Add product to favorites"""
    favorite = await FavoriteService.add_to_favorites(current_user.id, product_id)
    return ApiResponse(
        success=True, message="Product added to favorites successfully", data=favorite
    )


@router.delete("/{product_id}", response_model=ApiResponse)
async def remove_from_favorites(
    product_id: str, current_user: UserResponse = Depends(get_current_user)
):
    """Remove product from favorites"""
    success = await FavoriteService.remove_from_favorites(current_user.id, product_id)
    if not success:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="Product not in favorites"
        )

    return ApiResponse(
        success=True, message="Product removed from favorites successfully"
    )


@router.get("/{product_id}/check", response_model=ApiResponse)
async def check_favorite_status(
    product_id: str, current_user: UserResponse = Depends(get_current_user)
):
    """Check if product is in favorites"""
    is_favorite = await FavoriteService.is_favorite(current_user.id, product_id)
    return ApiResponse(
        success=True,
        message="Favorite status checked successfully",
        data={"is_favorite": is_favorite},
    )
