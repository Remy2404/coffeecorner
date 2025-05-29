from supabase import create_client, Client
from app.core.config import settings
import logging

logger = logging.getLogger(__name__)


class SupabaseClient:
    _instance: Client = None

    @classmethod
    def get_client(cls) -> Client:
        if cls._instance is None:
            if not settings.supabase_url or not settings.supabase_anon_key:
                raise ValueError("Supabase URL and ANON KEY must be provided")

            try:
                # Initialize with basic configuration - no proxy parameter
                cls._instance = create_client(
                    settings.supabase_url, settings.supabase_anon_key
                )
                logger.info("Supabase client initialized successfully")
            except Exception as e:
                logger.error(f"Failed to initialize Supabase client: {e}")
                raise

        return cls._instance

    @classmethod
    def get_admin_client(cls) -> Client:
        """Get client with service role key or admin headers"""
        try:
            if (
                hasattr(settings, "supabase_service_role_key")
                and settings.supabase_service_role_key
            ):
                # Use service role key if available
                admin_client = create_client(
                    settings.supabase_url, settings.supabase_service_role_key
                )
                logger.info("Admin client created with service role key")
                return admin_client
            else:
                # Use anon key for admin operations (with RLS bypass if configured)
                admin_client = create_client(
                    settings.supabase_url, settings.supabase_anon_key
                )
                logger.info("Admin client created with anon key")
                return admin_client

        except Exception as e:
            logger.error(f"Failed to create admin client: {e}")
            return cls.get_client()


# Global supabase client instance - initialize with direct API key values for reliability
try:
    # Try to create client directly with values
    logger.info("Creating Supabase client directly with API keys...")

    # Get URL and keys from environment
    url = settings.supabase_url
    anon_key = settings.supabase_anon_key

    # Log key lengths for debugging (without exposing actual keys)
    logger.info(f"SUPABASE_URL length: {len(url) if url else 0}")
    logger.info(f"SUPABASE_ANON_KEY length: {len(anon_key) if anon_key else 0}")

    if url and anon_key:
        # Create client with anon key - simplified approach
        supabase = create_client(url, anon_key)
        logger.info("Successfully created Supabase client with anon key")
    else:
        logger.error("Missing Supabase URL or ANON_KEY")
        raise ValueError("Missing Supabase URL or ANON_KEY")

except Exception as e:
    logger.error(f"Failed to create Supabase client: {e}")
    # Provide a fallback client for testing purposes only
    logger.warning(
        "Creating fallback mock Supabase client for testing - NO DATABASE ACCESS"
    )

    # Mock class for testing
    class MockSupabaseClient:
        def table(self, _):
            return self

        def select(self, _):
            return self

        def insert(self, _):
            return self

        def update(self, _):
            return self

        def delete(self):
            return self

        def eq(self, _, __):
            return self

        def limit(self, _):
            return self

        def order(self, _, **kwargs):
            return self

        def execute(self):
            return type("obj", (object,), {"data": []})

        @property
        def auth(self):
            # Mock auth object
            return type(
                "auth",
                (object,),
                {
                    "sign_up": lambda self, _: type(
                        "response", (object,), {"user": None}
                    ),
                    "sign_in_with_password": lambda self, _: type(
                        "response", (object,), {"user": None, "session": None}
                    ),
                },
            )()

    supabase = MockSupabaseClient()
