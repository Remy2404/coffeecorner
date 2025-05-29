from fastapi import APIRouter, Depends, HTTPException, status
from app.models.schemas import CartItemAdd, CartItemUpdate, ApiResponse
from app.services.cart_service import CartService
from app.routers.auth import (
    get_current_user,
    UserResponse,
    security,
    HTTPAuthorizationCredentials,
)

router = APIRouter(prefix="/cart", tags=["Cart"])


@router.get("", response_model=ApiResponse)
async def get_cart(
    current_user: UserResponse = Depends(get_current_user),
    credentials: HTTPAuthorizationCredentials = Depends(security),
):
    """Get user's cart items"""
    try:
        cart_items = await CartService.get_user_cart(
            current_user.id, access_token=credentials.credentials
        )
        return ApiResponse(
            success=True, message="Cart retrieved successfully", data=cart_items
        )
    except Exception as e:
        # Always return an empty cart on error, not a 404
        return ApiResponse(success=True, message="Cart retrieved successfully", data=[])


@router.post("/add", response_model=ApiResponse)
async def add_to_cart(
    cart_item: CartItemAdd,
    current_user: UserResponse = Depends(get_current_user),
    credentials: HTTPAuthorizationCredentials = Depends(security),
):
    """Add item to cart"""
    # Pass the access token to the cart service
    item = await CartService.add_to_cart(
        current_user.id, cart_item, access_token=credentials.credentials
    )
    return ApiResponse(
        success=True, message="Item added to cart successfully", data=item
    )


@router.put("/update/{item_id}", response_model=ApiResponse)
async def update_cart_item(
    item_id: str,
    update_data: CartItemUpdate,
    current_user: UserResponse = Depends(get_current_user),
    credentials: HTTPAuthorizationCredentials = Depends(security),
):
    """Update cart item quantity"""
    item = await CartService.update_cart_item(
        current_user.id, item_id, update_data, access_token=credentials.credentials
    )
    return ApiResponse(
        success=True, message="Cart item updated successfully", data=item
    )


@router.delete("/{item_id}", response_model=ApiResponse)
async def remove_from_cart(
    item_id: str,
    current_user: UserResponse = Depends(get_current_user),
    credentials: HTTPAuthorizationCredentials = Depends(security),
):
    """Remove item from cart"""
    success = await CartService.remove_from_cart(
        current_user.id, item_id, access_token=credentials.credentials
    )
    if not success:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="Cart item not found"
        )

    return ApiResponse(success=True, message="Item removed from cart successfully")


@router.delete("/clear", response_model=ApiResponse)
async def clear_cart(
    current_user: UserResponse = Depends(get_current_user),
    credentials: HTTPAuthorizationCredentials = Depends(security),
):
    """Clear all items from cart"""
    await CartService.clear_cart(current_user.id, access_token=credentials.credentials)
    return ApiResponse(success=True, message="Cart cleared successfully")


@router.get("/total", response_model=ApiResponse)
async def get_cart_total(
    current_user: UserResponse = Depends(get_current_user),
    credentials: HTTPAuthorizationCredentials = Depends(security),
):
    """Get cart total"""
    total = await CartService.get_cart_total(
        current_user.id, access_token=credentials.credentials
    )
    return ApiResponse(
        success=True,
        message="Cart total calculated successfully",
        data={"total": total},
    )


@router.get("/{user_id}", response_model=ApiResponse)
async def get_cart_by_user_id(user_id: str):
    """Get cart items by user ID (for testing purposes)"""
    try:
        cart_items = await CartService.get_user_cart(user_id)
        return ApiResponse(
            success=True, message="Cart retrieved successfully", data=cart_items
        )
    except Exception as e:
        return ApiResponse(success=False, message="Failed to get cart", data=[])
