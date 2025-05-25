import os
from typing import Optional
from dotenv import load_dotenv

# Load environment variables from .env file
load_dotenv()


class Settings:
    # Supabase Configuration
    supabase_url: str = os.getenv("SUPABASE_URL", "")
    supabase_anon_key: str = os.getenv("SUPABASE_ANON_KEY", "")
    supabase_service_role_key: Optional[str] = os.getenv(
        "SUPABASE_SERVICE_ROLE_KEY", None
    )

    # Firebase Configuration
    firebase_project_id: str = os.getenv("FIREBASE_PROJECT_ID", "")
    firebase_admin_sdk_path: str = os.getenv(
        "FIREBASE_ADMIN_SDK_PATH", "firebase-admin-sdk.json"
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
