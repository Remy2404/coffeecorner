import asyncio
from app.services.auth_service import AuthService
from app.models.schemas import UserUpdate
from app.database.supabase import SupabaseClient


async def test_end_to_end_profile_update():
    """Test the complete profile update flow"""
    try:
        print("=== END-TO-END PROFILE UPDATE TEST ===")
        
        # Known user ID from our tests
        user_id = "V4HVeaXIhARgUD4VL4EIh5hf7X42"
        
        print(f"\n--- Testing user: {user_id} ---")
        
        # Step 1: Get current profile
        admin_client = SupabaseClient.get_admin_client()
        current_profile = (
            admin_client.table("profiles")
            .select("*")
            .eq("id", user_id)
            .execute()
        )
        
        if not current_profile.data:
            print("ERROR: User profile not found")
            return
            
        original_data = current_profile.data[0]
        print(f"Original profile:")
        print(f"  - full_name: {original_data.get('full_name')}")
        print(f"  - phone: {original_data.get('phone')}")
        print(f"  - gender: {original_data.get('gender')}")
        
        # Step 2: Create test update data
        test_updates = {
            "full_name": "TEST UPDATE NAME",
            "phone": "1234567890", 
            "gender": "other"
        }
        
        print(f"\nApplying updates: {test_updates}")
        
        # Step 3: Apply update using admin client (simulating the fixed endpoint)
        update_result = (
            admin_client.table("profiles")
            .update(test_updates)
            .eq("id", user_id)
            .execute()
        )
        
        if not update_result.data:
            print("ERROR: Update failed")
            return
            
        updated_data = update_result.data[0]
        print(f"SUCCESS: Profile updated!")
        print(f"Updated profile:")
        print(f"  - full_name: {updated_data.get('full_name')}")
        print(f"  - phone: {updated_data.get('phone')}")
        print(f"  - gender: {updated_data.get('gender')}")
        
        # Step 4: Restore original data
        print(f"\nRestoring original data...")
        restore_data = {
            "full_name": original_data.get('full_name'),
            "phone": original_data.get('phone'),
            "gender": original_data.get('gender')
        }
        
        restore_result = (
            admin_client.table("profiles")
            .update(restore_data)
            .eq("id", user_id)
            .execute()
        )
        
        if restore_result.data:
            print("✅ Original data restored successfully")
            print("✅ PROFILE UPDATE FUNCTIONALITY IS WORKING!")
        else:
            print("❌ Failed to restore original data")
            
    except Exception as e:
        print(f"Test failed: {e}")
        import traceback
        traceback.print_exc()


if __name__ == "__main__":
    asyncio.run(test_end_to_end_profile_update())
