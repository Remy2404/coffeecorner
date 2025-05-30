from fastapi import APIRouter, HTTPException, status, Depends
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from app.models.schemas import UserResponse, ApiResponse, UserUpdate
from app.database.supabase import SupabaseClient, supabase
from app.routers.auth import get_current_user
import logging

logger = logging.getLogger(__name__)
security = HTTPBearer()

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


@router.put("/profile", response_model=ApiResponse)
async def update_user_profile(
    user_update: UserUpdate,
    current_user: UserResponse = Depends(get_current_user),
    credentials: HTTPAuthorizationCredentials = Depends(security),
):
    """Update user profile - Android app compatibility endpoint"""
    try:
        update_data = {k: v for k, v in user_update.dict().items() if v is not None}
        if "full_name" in update_data:
            update_data["full_name"] = update_data.get("full_name")
            if "name" in update_data:
                logger.info("Both 'name' and 'full_name' provided, using 'full_name'")
                update_data.pop("name")
        elif "name" in update_data:
            logger.info("Converting 'name' to 'full_name' for database compatibility")
            update_data["full_name"] = update_data.pop("name")

        if not update_data:
            return ApiResponse(
                success=True, message="No profile data to update", data=current_user
            )

        logger.info(f"Updating profile for user ID: {current_user.id}")
        logger.info(f"Update data: {update_data}")

        result = (
            supabase.table("profiles")
            .update(update_data)
            .eq("id", current_user.id)
            .select("*")
            .execute()
        )

        if not result.data:
            logger.error(
                f"Profile update failed: No data returned for user {current_user.id}"
            )
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND, detail="User profile not found"
            )

        updated_profile = result.data[0]

        user_response = UserResponse(
            id=updated_profile.get("id"),
            name=updated_profile.get("full_name", ""),
            email=updated_profile.get("email", ""),
            phone=updated_profile.get("phone", ""),
            gender=updated_profile.get("gender", ""),
            profile_image_url=updated_profile.get("profile_image_url", ""),
            date_of_birth=updated_profile.get("date_of_birth", ""),
            created_at=updated_profile.get("created_at"),
            updated_at=updated_profile.get("updated_at"),
        )

        return ApiResponse(
            success=True, message="Profile updated successfully", data=user_response
        )
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error updating profile: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Failed to update profile: {str(e)}",
        )


@router.delete("/profile", response_model=ApiResponse)
async def delete_user_profile(
    current_user: UserResponse = Depends(get_current_user),
    credentials: HTTPAuthorizationCredentials = Depends(security),
):
    """Delete user profile"""
    try:
        result = supabase.table("profiles").delete().eq("id", current_user.id).execute()

        if not result.data:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND, detail="User profile not found"
            )

        return ApiResponse(
            success=True, message="Profile deleted successfully", data=None
        )
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error deleting profile: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Failed to delete profile: {str(e)}",
        )
