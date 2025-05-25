from typing import Optional
from fastapi import HTTPException, status
from passlib.context import CryptContext
from jose import jwt, JWTError
from datetime import datetime, timedelta
from app.core.config import settings
from app.database.supabase import supabase
from app.models.schemas import UserCreate, UserLogin, UserResponse, AuthResponse
from app.services.firebase_auth_service import FirebaseAuthService
import logging

logger = logging.getLogger(__name__)

# Password hashing (for fallback scenarios)
pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")


class AuthService:

    @staticmethod
    def verify_password(plain_password: str, hashed_password: str) -> bool:
        """Verify a password against its hash"""
        return pwd_context.verify(plain_password, hashed_password)

    @staticmethod
    def get_password_hash(password: str) -> str:
        """Hash a password"""
        return pwd_context.hash(password)

    @staticmethod
    def create_access_token(data: dict, expires_delta: Optional[timedelta] = None):
        """Create a JWT access token"""
        to_encode = data.copy()
        if expires_delta:
            expire = datetime.utcnow() + expires_delta
        else:
            expire = datetime.utcnow() + timedelta(
                minutes=settings.access_token_expire_minutes
            )

        to_encode.update({"exp": expire})
        encoded_jwt = jwt.encode(
            to_encode, settings.secret_key, algorithm=settings.algorithm
        )
        return encoded_jwt

    @staticmethod
    def verify_token(token: str) -> Optional[dict]:
        """Verify and decode a JWT token"""
        try:
            payload = jwt.decode(
                token, settings.secret_key, algorithms=[settings.algorithm]
            )
            return payload
        except JWTError:
            return None

    @staticmethod
    async def verify_firebase_token_and_get_user(firebase_token: str) -> AuthResponse:
        """Verify Firebase token and return user data from Supabase"""
        try:
            # Verify Firebase token
            firebase_user = FirebaseAuthService.verify_firebase_token(firebase_token)

            # Get or create user in Supabase
            user_profile = await AuthService.get_or_create_user_profile(firebase_user)

            # Create our own JWT token for API access
            access_token = AuthService.create_access_token(
                data={
                    "sub": firebase_user["email"],
                    "user_id": user_profile.id,
                    "firebase_uid": firebase_user["uid"],
                }
            )

            return AuthResponse(
                success=True,
                message="Authentication successful",
                user=user_profile,
                access_token=access_token,
            )

        except ValueError as e:
            raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail=str(e))
        except Exception as e:
            logger.error(f"Firebase authentication error: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Authentication failed",
            )

    @staticmethod
    async def get_or_create_user_profile(firebase_user: dict) -> UserResponse:
        """Get existing user profile or create new one in Supabase"""
        try:
            # Try to find existing user by Firebase UID
            existing_user = (
                supabase.table("profiles")
                .select("*")
                .eq("firebase_uid", firebase_user["uid"])
                .execute()
            )

            if existing_user.data:
                return UserResponse(**existing_user.data[0])

            # Try to find by email (for migration purposes)
            existing_email_user = (
                supabase.table("profiles")
                .select("*")
                .eq("email", firebase_user["email"])
                .execute()
            )

            if existing_email_user.data:
                # Update existing user with Firebase UID
                updated_user = (
                    supabase.table("profiles")
                    .update({"firebase_uid": firebase_user["uid"]})
                    .eq("email", firebase_user["email"])
                    .execute()
                )

                return UserResponse(**updated_user.data[0])

            # Create new user profile
            new_user_data = {
                "firebase_uid": firebase_user["uid"],
                "email": firebase_user["email"],
                "name": firebase_user.get("name", ""),
                "phone": "",
                "gender": "",
                "profile_image_url": "",
                "date_of_birth": None,
                "email_verified": firebase_user.get("email_verified", False),
            }

            new_user = supabase.table("profiles").insert(new_user_data).execute()

            if new_user.data:
                return UserResponse(**new_user.data[0])
            else:
                raise HTTPException(
                    status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                    detail="Failed to create user profile",
                )

        except HTTPException:
            raise
        except Exception as e:
            logger.error(f"Error getting/creating user profile: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to process user profile",
            )

    @staticmethod
    async def register_user(user_data: UserCreate) -> AuthResponse:
        """Register a new user using Supabase Auth"""
        try:
            # Check if user already exists
            existing_user = (
                supabase.table("profiles")
                .select("*")
                .eq("email", user_data.email)
                .execute()
            )
            if existing_user.data:
                raise HTTPException(
                    status_code=status.HTTP_400_BAD_REQUEST,
                    detail="User with this email already exists",
                )

            # Register user with Supabase Auth
            auth_response = supabase.auth.sign_up(
                {
                    "email": user_data.email,
                    "password": user_data.password,
                    "options": {"data": {"name": user_data.name}},
                }
            )

            if not auth_response.user:
                raise HTTPException(
                    status_code=status.HTTP_400_BAD_REQUEST,
                    detail="Failed to create user account",
                )

            # Create user profile in database
            profile_data = {
                "id": auth_response.user.id,
                "name": user_data.name,
                "email": user_data.email,
                "phone": "",
                "gender": "",
                "profile_image_url": "",
                "date_of_birth": None,
            }

            profile_result = supabase.table("profiles").insert(profile_data).execute()

            if not profile_result.data:
                logger.error("Failed to create user profile")
                # Note: In production, you might want to delete the auth user if profile creation fails

            # Create access token
            access_token = AuthService.create_access_token(
                data={"sub": auth_response.user.email, "user_id": auth_response.user.id}
            )

            user_response = UserResponse(**profile_data)

            return AuthResponse(
                success=True,
                message="User registered successfully",
                user=user_response,
                access_token=access_token,
            )

        except HTTPException:
            raise
        except Exception as e:
            logger.error(f"Registration error: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Internal server error during registration",
            )

    @staticmethod
    async def login_user(user_data: UserLogin) -> AuthResponse:
        """Login a user using Supabase Auth"""
        try:
            # Authenticate with Supabase
            auth_response = supabase.auth.sign_in_with_password(
                {"email": user_data.email, "password": user_data.password}
            )

            if not auth_response.user:
                raise HTTPException(
                    status_code=status.HTTP_401_UNAUTHORIZED,
                    detail="Invalid email or password",
                )

            # Get user profile
            profile_result = (
                supabase.table("profiles")
                .select("*")
                .eq("id", auth_response.user.id)
                .execute()
            )

            if not profile_result.data:
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="User profile not found",
                )

            # Create access token
            access_token = AuthService.create_access_token(
                data={"sub": auth_response.user.email, "user_id": auth_response.user.id}
            )

            user_response = UserResponse(**profile_result.data[0])

            return AuthResponse(
                success=True,
                message="Login successful",
                user=user_response,
                access_token=access_token,
            )

        except HTTPException:
            raise
        except Exception as e:
            logger.error(f"Login error: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Internal server error during login",
            )

    @staticmethod
    async def get_current_user(token: str) -> UserResponse:
        """Get current user from JWT token"""
        try:
            payload = AuthService.verify_token(token)
            if not payload:
                raise HTTPException(
                    status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid token"
                )

            user_id = payload.get("user_id")
            if not user_id:
                raise HTTPException(
                    status_code=status.HTTP_401_UNAUTHORIZED,
                    detail="Invalid token payload",
                )

            # Get user profile
            profile_result = (
                supabase.table("profiles").select("*").eq("id", user_id).execute()
            )

            if not profile_result.data:
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND, detail="User not found"
                )

            return UserResponse(**profile_result.data[0])

        except HTTPException:
            raise
        except Exception as e:
            logger.error(f"Get current user error: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to get user information",
            )

    @staticmethod
    async def forgot_password(email: str) -> bool:
        """Send password reset email"""
        try:
            supabase.auth.reset_password_for_email(email)
            return True
        except Exception as e:
            logger.error(f"Password reset error: {e}")
            return False
