from fastapi import APIRouter, Depends, HTTPException, status
from app.models.schemas import ApiResponse
from app.database.supabase import supabase, SupabaseClient
from app.core.config import settings
import os
import sys

router = APIRouter(prefix="/debug", tags=["Debug"])


@router.get("/test-profiles", response_model=ApiResponse)
async def test_profiles():
    """Test profiles table access with service role"""
    try:
        # Get admin client with service role key
        admin_client = SupabaseClient.get_admin_client()

        # Try to query profiles table
        result = admin_client.table("profiles").select("*").limit(5).execute()

        return ApiResponse(
            success=True,
            message=f"Successfully queried profiles table. Found {len(result.data)} profiles.",
            data=result.data,
        )
    except Exception as e:
        return ApiResponse(
            success=False,
            message=f"Error accessing profiles table: {str(e)}",
            data=None,
        )


@router.get("/test-cart", response_model=ApiResponse)
async def test_cart():
    """Test cart table access with service role"""
    try:
        # Get admin client with service role key
        admin_client = SupabaseClient.get_admin_client()

        # Try to query cart_items table
        result = admin_client.table("cart_items").select("*").limit(5).execute()

        return ApiResponse(
            success=True,
            message=f"Successfully queried cart_items table. Found {len(result.data)} items.",
            data=result.data,
        )
    except Exception as e:
        return ApiResponse(
            success=False,
            message=f"Error accessing cart_items table: {str(e)}",
            data=None,
        )


@router.post("/add-test-profile", response_model=ApiResponse)
async def add_test_profile():
    """Add a test profile to the profiles table"""
    try:
        # Get admin client with service role key
        admin_client = SupabaseClient.get_admin_client()

        # Create test profile data
        test_profile_data = {
            "email": "test_user@example.com",
            "full_name": "Test User",
            "firebase_uid": "test_firebase_uid_123",
        }

        # Try to insert into profiles table
        result = admin_client.table("profiles").insert(test_profile_data).execute()

        return ApiResponse(
            success=True, message="Successfully added test profile", data=result.data
        )
    except Exception as e:
        return ApiResponse(
            success=False, message=f"Error adding test profile: {str(e)}", data=None
        )


@router.get("/status", response_model=ApiResponse)
async def system_status():
    """Get comprehensive system status for deployment troubleshooting"""
    try:
        status_info = {
            "api_version": settings.app_version,
            "debug_mode": settings.debug,
            "python_version": sys.version,
            "environment_variables": {
                "SUPABASE_URL": "✓ Set" if settings.supabase_url else "✗ Missing",
                "SUPABASE_ANON_KEY": (
                    "✓ Set" if settings.supabase_anon_key else "✗ Missing"
                ),
                "SUPABASE_SERVICE_ROLE_KEY": (
                    "✓ Set" if settings.supabase_service_role_key else "✗ Missing"
                ),
                "FIREBASE_PROJECT_ID": settings.firebase_project_id,
                "JWT_SECRET_KEY": (
                    "✓ Set"
                    if settings.secret_key
                    != "your-secret-key-here-change-in-production"
                    else "⚠ Using default"
                ),
                "PORT": os.environ.get("PORT", "Not set (using 8000)"),
            },
            "database_status": "Unknown",
            "firebase_status": "Unknown",
        }

        # Test Supabase connection
        try:
            admin_client = SupabaseClient.get_admin_client()
            test_result = admin_client.table("profiles").select("id").limit(1).execute()
            status_info["database_status"] = "✓ Connected"
            status_info["database_records"] = (
                len(test_result.data) if hasattr(test_result, "data") else 0
            )
        except Exception as db_error:
            status_info["database_status"] = f"✗ Error: {str(db_error)}"

        # Test Firebase (basic check)
        try:
            from app.services.firebase_auth_service import FirebaseAuthService

            # Just check if Firebase can be initialized
            FirebaseAuthService.initialize_firebase()
            status_info["firebase_status"] = "✓ Initialized"
        except Exception as firebase_error:
            status_info["firebase_status"] = f"✗ Error: {str(firebase_error)}"

        return ApiResponse(
            success=True,
            message="System status retrieved successfully",
            data=status_info,
        )
    except Exception as e:
        return ApiResponse(
            success=False, message=f"Error getting system status: {str(e)}", data=None
        )
