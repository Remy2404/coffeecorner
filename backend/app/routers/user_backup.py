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


@router.put("/profile", response_model=ApiResponse)
async def update_user_profile(
    user_update: UserUpdate, 
    current_user: UserResponse = Depends(get_current_user),
    credentials: HTTPAuthorizationCredentials = Depends(security)
):
    """Update user profile - mirrors the /auth/profile endpoint for Android app compatibility"""
    try:
        # Create a dictionary with only the fields that are provided (not None)
        update_data = {k: v for k, v in user_update.dict().items() if v is not None}
        
        # Handle field name conversion from Android app to backend
        # If full_name is provided, use it for the database field
        if 'full_name' in update_data:
            update_data['full_name'] = update_data.get('full_name')
            # Remove name if it exists to avoid conflicts
            if 'name' in update_data:
                logger.info("Both 'name' and 'full_name' provided, using 'full_name'")
                update_data.pop('name')
        # If only name is provided, map it to full_name for database
        elif 'name' in update_data:
            logger.info("Converting 'name' to 'full_name' for database compatibility")
            update_data['full_name'] = update_data.pop('name')
                
        if not update_data:
            return ApiResponse(
                success=True, message="No profile data to update", data=current_user
            )
            
        # Log the data being sent to the database
        logger.info(f"Updating profile for user ID: {current_user.id}")
        logger.info(f"Update data after field name processing: {update_data}")
        
        # Update the user profile in Supabase
        result = supabase.table("profiles").update(update_data).eq("id", current_user.id).execute()
        logger.info(f"Update result: {result.data if hasattr(result, 'data') else 'No result data'}")
        
        if not result.data:
            logger.error(f"Profile update failed: No data returned for user {current_user.id}")
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND, 
                detail="User profile not found"
            )
            
        updated_profile = result.data[0]
        return ApiResponse(
            success=True, message="Profile updated successfully", data=updated_profile
        )
    except Exception as e:
        logger.error(f"Error updating profile: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Failed to update profile: {str(e)}"
        )
