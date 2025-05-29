import os
import sys
from dotenv import load_dotenv
from supabase import create_client, Client
import logging

# Load environment variables
load_dotenv()

# Setup logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


def get_supabase_client():
    """Get Supabase client with service role key"""
    supabase_url = os.getenv("SUPABASE_URL")
    service_role_key = os.getenv("SUPABASE_SERVICE_ROLE_KEY")

    if not supabase_url or not service_role_key:
        raise ValueError(
            "SUPABASE_URL and SUPABASE_SERVICE_ROLE_KEY must be set in environment"
        )

    return create_client(supabase_url, service_role_key)


def run_migration():
    """Run the Firebase UID migration using Supabase SQL functions"""
    try:
        logger.info("🚀 Starting Firebase UID Database Migration via Supabase API")
        logger.info("=" * 60)

        # Get Supabase client
        supabase = get_supabase_client()
        logger.info("✅ Connected to Supabase successfully")

        # Step 1: Create backup tables
        logger.info("📦 Creating backup tables...")
        backup_queries = [
            "DROP TABLE IF EXISTS profiles_backup;",
            "CREATE TABLE profiles_backup AS SELECT * FROM public.profiles;",
            "DROP TABLE IF EXISTS cart_items_backup;",
            "CREATE TABLE cart_items_backup AS SELECT * FROM public.cart_items;",
            "DROP TABLE IF EXISTS orders_backup;",
            "CREATE TABLE orders_backup AS SELECT * FROM public.orders;",
            "DROP TABLE IF EXISTS favorites_backup;",
            "CREATE TABLE favorites_backup AS SELECT * FROM public.favorites;",
            "DROP TABLE IF EXISTS notifications_backup;",
            "CREATE TABLE notifications_backup AS SELECT * FROM public.notifications;",
        ]

        for query in backup_queries:
            try:
                result = supabase.rpc("execute_sql", {"sql": query}).execute()
                logger.info(f"✅ Backup query executed: {query[:50]}...")
            except Exception as e:
                logger.warning(f"⚠️ Backup query warning: {e}")

        # Step 2: Drop foreign key constraints
        logger.info("🔗 Dropping foreign key constraints...")
        fk_drop_queries = [
            "ALTER TABLE public.cart_items DROP CONSTRAINT IF EXISTS cart_items_user_id_fkey;",
            "ALTER TABLE public.orders DROP CONSTRAINT IF EXISTS orders_user_id_fkey;",
            "ALTER TABLE public.favorites DROP CONSTRAINT IF EXISTS favorites_user_id_fkey;",
            "ALTER TABLE public.notifications DROP CONSTRAINT IF EXISTS notifications_user_id_fkey;",
            "ALTER TABLE public.cart_items DROP CONSTRAINT IF EXISTS cart_items_user_id_product_id_key;",
            "ALTER TABLE public.favorites DROP CONSTRAINT IF EXISTS favorites_user_id_product_id_key;",
        ]

        for query in fk_drop_queries:
            try:
                result = supabase.rpc("execute_sql", {"sql": query}).execute()
                logger.info(f"✅ Constraint drop executed: {query[:50]}...")
            except Exception as e:
                logger.warning(f"⚠️ Constraint drop warning: {e}")

        # Step 3: Alter column types to TEXT
        logger.info("🔄 Converting UUID columns to TEXT...")
        alter_queries = [
            "ALTER TABLE public.profiles ALTER COLUMN id DROP DEFAULT;",
            "ALTER TABLE public.profiles ALTER COLUMN id TYPE TEXT USING id::TEXT;",
            "ALTER TABLE public.cart_items ALTER COLUMN user_id TYPE TEXT USING user_id::TEXT;",
            "ALTER TABLE public.orders ALTER COLUMN user_id TYPE TEXT USING user_id::TEXT;",
            "ALTER TABLE public.favorites ALTER COLUMN user_id TYPE TEXT USING user_id::TEXT;",
            "ALTER TABLE public.notifications ALTER COLUMN user_id TYPE TEXT USING user_id::TEXT;",
        ]

        for query in alter_queries:
            try:
                result = supabase.rpc("execute_sql", {"sql": query}).execute()
                logger.info(f"✅ Column type change executed: {query[:50]}...")
            except Exception as e:
                logger.error(f"❌ Column type change failed: {e}")
                # This is critical, so we should stop here
                return False

        # Step 4: Recreate foreign key constraints
        logger.info("🔗 Recreating foreign key constraints...")
        fk_create_queries = [
            """ALTER TABLE public.cart_items 
               ADD CONSTRAINT cart_items_user_id_fkey 
               FOREIGN KEY (user_id) REFERENCES public.profiles(id) ON DELETE CASCADE;""",
            """ALTER TABLE public.orders 
               ADD CONSTRAINT orders_user_id_fkey 
               FOREIGN KEY (user_id) REFERENCES public.profiles(id) ON DELETE CASCADE;""",
            """ALTER TABLE public.favorites 
               ADD CONSTRAINT favorites_user_id_fkey 
               FOREIGN KEY (user_id) REFERENCES public.profiles(id) ON DELETE CASCADE;""",
            """ALTER TABLE public.notifications 
               ADD CONSTRAINT notifications_user_id_fkey 
               FOREIGN KEY (user_id) REFERENCES public.profiles(id) ON DELETE CASCADE;""",
            """ALTER TABLE public.cart_items 
               ADD CONSTRAINT cart_items_user_id_product_id_key 
               UNIQUE (user_id, product_id);""",
            """ALTER TABLE public.favorites 
               ADD CONSTRAINT favorites_user_id_product_id_key 
               UNIQUE (user_id, product_id);""",
        ]

        for query in fk_create_queries:
            try:
                result = supabase.rpc("execute_sql", {"sql": query}).execute()
                logger.info(f"✅ Constraint recreation executed: {query[:50]}...")
            except Exception as e:
                logger.warning(f"⚠️ Constraint recreation warning: {e}")

        # Step 5: Test Firebase UID insertion
        logger.info("🧪 Testing Firebase UID insertion...")
        test_uid = "V4HVeaXIhARgUD4VL4EIh5hf7X42"

        try:
            # Try to insert test data
            test_insert = (
                supabase.table("profiles")
                .upsert(
                    {
                        "id": test_uid,
                        "firebase_uid": test_uid,
                        "email": "migration-test@example.com",
                        "full_name": "Migration Test User",
                    }
                )
                .execute()
            )

            if test_insert.data:
                logger.info("✅ Firebase UID insertion test passed")
                logger.info(f"  - ID: {test_insert.data[0]['id']}")
                logger.info(f"  - Firebase UID: {test_insert.data[0]['firebase_uid']}")
                logger.info(f"  - Email: {test_insert.data[0]['email']}")

                # Cleanup test data
                supabase.table("profiles").delete().eq(
                    "email", "migration-test@example.com"
                ).execute()
                logger.info("🧹 Test data cleaned up")
            else:
                logger.error("❌ Firebase UID insertion test failed - no data returned")
                return False

        except Exception as test_error:
            logger.error(f"❌ Firebase UID insertion test failed: {test_error}")
            return False

        logger.info("🎉 Migration completed successfully!")
        logger.info("🔥 Firebase UID compatibility has been enabled")
        logger.info(
            "📱 Your Coffee Corner app should now work with Firebase authentication"
        )

        return True

    except Exception as e:
        logger.error(f"❌ Migration failed: {e}")
        return False


def main():
    """Main function to run the migration"""
    # Check if we have the required dependencies
    try:
        from supabase import create_client, Client
    except ImportError:
        logger.error(
            "❌ supabase is not installed. Install it with: pip install supabase"
        )
        return 1

    # Run the migration
    success = run_migration()

    if success:
        logger.info("✅ Migration completed successfully!")
        return 0
    else:
        logger.error("❌ Migration failed!")
        return 1


if __name__ == "__main__":
    sys.exit(main())
