from fastapi import APIRouter, Depends, Form
from app.models.schemas import OrderCreate, ApiResponse
from app.services.order_service import OrderService
from app.routers.auth import get_current_user, UserResponse

router = APIRouter(prefix="/orders", tags=["Orders"])


@router.get("", response_model=ApiResponse)
async def get_orders(current_user: UserResponse = Depends(get_current_user)):
    """Get user's orders"""
    orders = await OrderService.get_user_orders(current_user.id)
    return ApiResponse(
        success=True, message="Orders retrieved successfully", data=orders
    )


@router.get("/{order_id}", response_model=ApiResponse)
async def get_order(
    order_id: str, current_user: UserResponse = Depends(get_current_user)
):
    """Get specific order by ID"""
    order = await OrderService.get_order_by_id(current_user.id, order_id)
    return ApiResponse(success=True, message="Order retrieved successfully", data=order)


@router.post("", response_model=ApiResponse)
async def create_order(
    order_data: OrderCreate, current_user: UserResponse = Depends(get_current_user)
):
    """Create a new order"""
    order = await OrderService.create_order(current_user.id, order_data)
    return ApiResponse(success=True, message="Order created successfully", data=order)


@router.put("/{order_id}/status", response_model=ApiResponse)
async def update_order_status(order_id: str, status: str = Form(...)):
    """Update order status (admin only)"""
    order = await OrderService.update_order_status(order_id, status)
    return ApiResponse(
        success=True, message="Order status updated successfully", data=order
    )
