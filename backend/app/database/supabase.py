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
                cls._instance = create_client(
                    settings.supabase_url,
                    settings.supabase_anon_key
                )
                logger.info("Supabase client initialized successfully")
            except Exception as e:
                logger.error(f"Failed to initialize Supabase client: {e}")
                raise
        
        return cls._instance
    
    @classmethod
    def get_admin_client(cls) -> Client:
        """Get admin client with service role key for admin operations"""
        if not settings.supabase_service_role_key:
            raise ValueError("Service role key required for admin operations")
        
        return create_client(
            settings.supabase_url,
            settings.supabase_service_role_key
        )


# Global supabase client instance
supabase: Client = SupabaseClient.get_client()