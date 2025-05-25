import os
import sys

sys.path.append("C:\\Users\\User\\Downloads\\Telegram Desktop\\Apsaraandroid\\backend")

from app.database.supabase import supabase


async def test_connection():
    try:
        # Test connection by listing tables
        result = supabase.table("profiles").select("count").execute()
        print("✅ Successfully connected to Supabase!")
        print(f"Connection result: {result}")
        return True
    except Exception as e:
        print(f"❌ Failed to connect to Supabase: {e}")
        return False


if __name__ == "__main__":
    import asyncio

    asyncio.run(test_connection())
