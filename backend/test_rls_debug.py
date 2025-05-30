import asyncio
import logging
from app.database.supabase import SupabaseClient

# Set up logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


async def test_rls_issue():
    """Test if RLS is blocking profile updates"""
    try:
        user_id = "V4HVeaXIhARgUD4VL4EIh5hf7X42"
        
        print("=== Testing RLS Issue ===")
        
        # Test 1: Use regular client (with RLS)
        print("\n--- Test 1: Regular client (with RLS) ---")
        from app.database.supabase import supabase
        
        regular_result = (
            supabase.table("profiles")
            .update({"phone": "TEST_REGULAR"})
            .eq("id", user_id)
            .execute()
        )
        
        print(f"Regular client update result: {regular_result.data}")
        
        # Verify
        regular_verify = (
            supabase.table("profiles")
            .select("phone")
            .eq("id", user_id)
            .execute()
        )
        print(f"Regular client phone after update: {regular_verify.data}")
        
        # Test 2: Use admin client (bypasses RLS)
        print("\n--- Test 2: Admin client (bypasses RLS) ---")
        admin_client = SupabaseClient.get_admin_client()
        
        admin_result = (
            admin_client.table("profiles")
            .update({"phone": "TEST_ADMIN"})
            .eq("id", user_id)
            .execute()
        )
        
        print(f"Admin client update result: {admin_result.data}")
        
        # Verify
        admin_verify = (
            admin_client.table("profiles")
            .select("phone")
            .eq("id", user_id)
            .execute()
        )
        print(f"Admin client phone after update: {admin_verify.data}")
        
        # Restore original phone
        restore_result = (
            admin_client.table("profiles")
            .update({"phone": "0962064081"})
            .eq("id", user_id)
            .execute()
        )
        print(f"Restored original phone: {restore_result.data}")

    except Exception as e:
        logger.error(f"Test failed: {e}")
        import traceback
        traceback.print_exc()


if __name__ == "__main__":
    asyncio.run(test_rls_issue())
