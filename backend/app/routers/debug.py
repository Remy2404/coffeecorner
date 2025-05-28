from fastapi import APIRouter, Depends, HTTPException, status
from app.models.schemas import ApiResponse
from app.database.supabase import supabase, SupabaseClient

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
