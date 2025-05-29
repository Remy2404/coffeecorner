from typing import List
from fastapi import HTTPException, status
from app.database.supabase import supabase
from app.models.schemas import OrderCreate, OrderResponse
import logging
from datetime import datetime

logger = logging.getLogger(__name__)


class OrderService:

    @staticmethod
    async def create_order(user_id: str, order_data: OrderCreate) -> OrderResponse:
        """Create a new order"""
        try:
            order_dict = {
                "user_id": user_id,
                "items": order_data.items,
                "total": order_data.total,
                "status": "pending",
                "delivery_address": order_data.delivery_address,
                "notes": order_data.notes,
                "order_date": datetime.utcnow().isoformat(),
            }

            result = supabase.table("orders").insert(order_dict).execute()

            if not result.data:
                raise HTTPException(
                    status_code=status.HTTP_400_BAD_REQUEST,
                    detail="Failed to create order",
                )

            return OrderResponse(**result.data[0])

        except HTTPException:
            raise
        except Exception as e:
            logger.error(f"Error creating order: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to create order",
            )

    @staticmethod
    async def get_user_orders(user_id: str) -> List[OrderResponse]:
        """Get all orders for a user"""
        try:
            result = (
                supabase.table("orders")
                .select("*")
                .eq("user_id", user_id)
                .order("created_at", desc=True)
                .execute()
            )

            return [OrderResponse(**order) for order in result.data]

        except Exception as e:
            logger.error(f"Error fetching orders for user {user_id}: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to fetch orders",
            )

    @staticmethod
    async def get_order_by_id(user_id: str, order_id: str) -> OrderResponse:
        """Get a specific order by ID"""
        try:
            result = (
                supabase.table("orders")
                .select("*")
                .eq("id", order_id)
                .eq("user_id", user_id)
                .execute()
            )

            if not result.data:
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND, detail="Order not found"
                )

            return OrderResponse(**result.data[0])

        except HTTPException:
            raise
        except Exception as e:
            logger.error(f"Error fetching order {order_id}: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to fetch order",
            )

    @staticmethod
    async def update_order_status(order_id: str, new_status: str) -> OrderResponse:
        """Update order status (admin function)"""
        try:
            result = (
                supabase.table("orders")
                .update({"status": new_status})
                .eq("id", order_id)
                .execute()
            )

            if not result.data:
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND, detail="Order not found"
                )

            return OrderResponse(**result.data[0])

        except HTTPException:
            raise
        except Exception as e:
            logger.error(f"Error updating order status: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to update order status",
            )
