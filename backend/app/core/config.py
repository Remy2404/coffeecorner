import os
import logging
from typing import Optional
from dotenv import load_dotenv
from pathlib import Path

# Configure logger
logger = logging.getLogger(__name__)

# Get the absolute path to the backend directory and .env file
backend_dir = Path(__file__).resolve().parent.parent.parent
env_path = backend_dir / ".env"

# Load environment variables from .env file
logger.info(f"Loading environment variables from: {env_path}")
load_dotenv(dotenv_path=env_path)


class Settings:
    # Supabase Configuration
    supabase_url: str = os.getenv("SUPABASE_URL", "")
    supabase_anon_key: str = os.getenv("SUPABASE_ANON_KEY", "")
    supabase_service_role_key: str = os.getenv("SUPABASE_SERVICE_ROLE_KEY", "")

    def __init__(self):
        # Log configuration details
        if not self.supabase_url:
            logger.error("SUPABASE_URL not found in environment variables")
        else:
            logger.info(f"SUPABASE_URL loaded: {self.supabase_url}")

        if not self.supabase_anon_key:
            logger.error("SUPABASE_ANON_KEY not found in environment variables")
        else:
            anon_key_preview = (
                self.supabase_anon_key[:10] + "..." if self.supabase_anon_key else ""
            )
            logger.info(f"SUPABASE_ANON_KEY loaded: {anon_key_preview}")
            
        if not self.supabase_service_role_key:
            logger.warning("SUPABASE_SERVICE_ROLE_KEY not found - using anon key for admin operations")
        else:
            service_key_preview = (
                self.supabase_service_role_key[:10] + "..." if self.supabase_service_role_key else ""
            )
            logger.info(f"SUPABASE_SERVICE_ROLE_KEY loaded: {service_key_preview}")

    # Firebase Configuration
    firebase_project_id: str = os.getenv("FIREBASE_PROJECT_ID", "coffee-corner-2697f")
    firebase_admin_sdk_path: str = os.getenv(
        "FIREBASE_ADMIN_SDK_PATH",
        "firebase-admin-sdk.json",
    )

    # FastAPI Configuration
    app_name: str = "Coffee Shop API"
    app_version: str = "1.0.0"
    debug: bool = os.getenv("DEBUG", "False").lower() == "true"

    # JWT Configuration
    secret_key: str = os.getenv(
        "JWT_SECRET_KEY", "your-secret-key-here-change-in-production"
    )
    algorithm: str = os.getenv("JWT_ALGORITHM", "HS256")
    access_token_expire_minutes: int = int(os.getenv("JWT_EXPIRE_HOURS", "24")) * 60

    # Database Configuration
    database_url: Optional[str] = os.getenv("DATABASE_URL", None)


settings = Settings()
