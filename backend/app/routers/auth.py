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
    return await AuthService.register_user(user_data)


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


@router.put("/profile", response_model=ApiResponse)
async def update_profile(
    user_update: UserUpdate, current_user: UserResponse = Depends(get_current_user)
):
    """Update user profile"""
    # Implementation would go here - update user in Supabase
    return ApiResponse(
        success=True, message="Profile updated successfully", data=current_user
    )
