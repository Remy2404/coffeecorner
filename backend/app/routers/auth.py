from fastapi import APIRouter, Form, HTTPException, Depends, status
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from app.models.schemas import (
    UserCreate,
    UserLogin,
    UserResponse,
    AuthResponse,
    ApiResponse,
    UserUpdate,
)
from app.services.auth_service import AuthService
import logging

from app.database.supabase import supabase

logger = logging.getLogger(__name__)

router = APIRouter(prefix="/auth", tags=["Authentication"])
security = HTTPBearer()


async def get_current_user(
    credentials: HTTPAuthorizationCredentials = Depends(security),
) -> UserResponse:
    """Get current authenticated user"""
    return await AuthService.get_current_user(credentials.credentials)


@router.post("/register", response_model=AuthResponse)
async def register(
    name: str = Form(...), email: str = Form(...), password: str = Form(...)
):
    """Register a new user"""
    user_data = UserCreate(name=name, email=email, password=password)
    auth_response = await AuthService.register_user(user_data)
    # Automatically create user profile in Supabase after registration
    if auth_response and auth_response.user:
        supabase.table("profiles").insert(
            {
                "id": auth_response.user.id,
                "email": auth_response.user.email,
                "full_name": name,
            }
        ).execute()
    return auth_response


@router.post("/login", response_model=AuthResponse)
async def login(email: str = Form(...), password: str = Form(...)):
    """Login user"""
    user_data = UserLogin(email=email, password=password)
    return await AuthService.login_user(user_data)


@router.post("/firebase-auth", response_model=AuthResponse)
async def firebase_auth(firebase_token: str = Form(...)):
    """Authenticate user with Firebase ID token"""
    return await AuthService.verify_firebase_token_and_get_user(firebase_token)


@router.post("/forgot-password", response_model=ApiResponse)
async def forgot_password(email: str = Form(...)):
    """Send password reset email"""
    success = await AuthService.forgot_password(email)
    if success:
        return ApiResponse(
            success=True, message="Password reset email sent successfully"
        )
    else:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Failed to send password reset email",
        )


@router.get("/me", response_model=ApiResponse)
async def get_profile(current_user: UserResponse = Depends(get_current_user)):
    """Get current user profile"""
    return ApiResponse(
        success=True, message="Profile retrieved successfully", data=current_user
    )


@router.get("/profile", response_model=ApiResponse)
async def get_current_user_profile(current_user: UserResponse = Depends(get_current_user)):
    """Get current authenticated user profile - Android app compatibility endpoint"""
    try:
        # Fetch fresh profile data from database using admin client
        from app.database.supabase import SupabaseClient
        
        admin_client = SupabaseClient.get_admin_client()
        result = (
            admin_client.table("profiles")
            .select("*")
            .eq("id", current_user.id)
            .limit(1)
            .execute()
        )
        
        if not result.data:
            logger.error(f"Profile not found for authenticated user: {current_user.id}")
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND, 
                detail="User profile not found"
            )
        
        user_data = result.data[0]
        
        # Create user response with fresh data from database
        user_response = UserResponse(
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
        
        logger.info(f"Profile retrieved for user: {current_user.id}")
        return ApiResponse(
            success=True, 
            message="Profile retrieved successfully", 
            data=user_response
        )
        
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error retrieving current user profile: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Failed to retrieve profile: {str(e)}"
        )


@router.put("/profile", response_model=ApiResponse)
async def update_profile(
    user_update: UserUpdate, current_user: UserResponse = Depends(get_current_user)
):
    """Update user profile"""
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


def create_user_profile(user_id: str, email: str, full_name: str):
    supabase.table("profiles").insert(
        {"id": user_id, "email": email, "full_name": full_name}
    ).execute()
