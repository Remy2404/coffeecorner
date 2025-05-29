from fastapi import APIRouter, HTTPException, status
from app.models.schemas import UserResponse, ApiResponse
from app.database.supabase import SupabaseClient

router = APIRouter(prefix="/users", tags=["Users"])


@router.get("/profile/{user_id}", response_model=ApiResponse)
async def get_user_profile(user_id: str):
    """Get user profile by user_id (Firebase UID)"""
    try:
        admin_client = SupabaseClient.get_admin_client()
        result = (
            admin_client.table("profiles")
            .select("*")
            .eq("id", user_id)
            .limit(1)
            .execute()
        )
        if not result.data:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND, detail="User not found"
            )
        user_data = result.data[0]
        # Map DB fields to UserResponse
        user = UserResponse(
            id=user_data.get("id"),
            name=user_data.get("full_name", ""),
            email=user_data.get("email", ""),
            phone=user_data.get("phone", ""),
            gender=user_data.get("gender", ""),
            profile_image_url=user_data.get("profile_image_url", ""),
            date_of_birth=user_data.get("date_of_birth", ""),
            created_at=user_data.get("created_at"),
            updated_at=user_data.get("updated_at"),
        )
        return ApiResponse(success=True, message="User profile retrieved", data=user)
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(
            status_code=500, detail=f"Error retrieving user profile: {e}"
        )
