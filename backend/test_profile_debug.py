import asyncio
import logging
from app.services.auth_service import AuthService
from app.models.schemas import UserUpdate
from app.database.supabase import supabase

# Set up logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


async def test_profile_update():
    """Test the profile update flow"""
    try:
        user_id = "V4HVeaXIhARgUD4VL4EIh5hf7X42"

        # Test 1: Check if user exists directly
        print("=== Test 1: Direct profile lookup ===")
        result = supabase.table("profiles").select("*").eq("id", user_id).execute()
        print(f"Profile exists: {bool(result.data)}")
        if result.data:
            print(f"Profile data: {result.data[0]}")

        # Test 2: Create a JWT token for this user
        print("\n=== Test 2: Create JWT token ===")
        token_data = {
            "sub": "rosexmee1122@gmail.com",
            "user_id": user_id,
            "firebase_uid": user_id,
        }
        token = AuthService.create_access_token(data=token_data)
        print(f"Token created: {token[:50]}...")

        # Test 3: Verify token and get user
        print("\n=== Test 3: Verify token and get user ===")
        try:
            current_user = await AuthService.get_current_user(token)
            print(f"Current user retrieved: {current_user.id}")
            print(f"Current user data: {current_user}")
        except Exception as e:
            print(f"Error getting current user: {e}")
            return

        # Test 4: Try to update profile
        print("\n=== Test 4: Update profile ===")
        update_data = UserUpdate(
            full_name="phon Ramy",
            email="rosexmee1122@gmail.com",
            phone="1245431423",
            gender="other",
            date_of_birth="",
            profile_image_url="",
        )

        # Simulate the profile update logic
        update_dict = {k: v for k, v in update_data.dict().items() if v is not None}
        print(f"Update data: {update_dict}")  # Execute the update
        result = (
            supabase.table("profiles")
            .update(update_dict)
            .eq("id", current_user.id)
            .execute()
        )

        print(f"Update result data: {result.data}")
        print(f"Update result count: {getattr(result, 'count', 'No count attribute')}")
        print(f"Update result status: {getattr(result, 'status_code', 'No status')}")

        # Fetch the updated record to verify
        verify_result = (
            supabase.table("profiles").select("*").eq("id", current_user.id).execute()
        )

        print(f"Verification result: {verify_result.data}")

        if not verify_result.data:
            print("ERROR: Profile not found after update")
        else:
            print("SUCCESS: Profile update verified")
            updated_profile = verify_result.data[0]
            print(f"Updated profile data: {updated_profile}")

    except Exception as e:
        logger.error(f"Test failed: {e}")
        import traceback

        traceback.print_exc()


if __name__ == "__main__":
    asyncio.run(test_profile_update())
