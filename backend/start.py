import os
import uvicorn
import logging
from app.core.config import settings

# Configure logging for production
logging.basicConfig(
    level=logging.INFO, format="%(asctime)s - %(name)s - %(levelname)s - %(message)s"
)
logger = logging.getLogger(__name__)


def main():
    """Start the application with production settings"""

    # Get port from environment (required for most hosting platforms)
    port = int(os.environ.get("PORT", 8000))
    host = "0.0.0.0"

    logger.info(f"Starting Coffee Shop API on {host}:{port}")
    logger.info(f"Debug mode: {settings.debug}")
    logger.info(f"App version: {settings.app_version}")

    # Log environment info
    logger.info(f"Environment variables loaded:")
    logger.info(f"- SUPABASE_URL: {'✓' if settings.supabase_url else '✗'}")
    logger.info(f"- SUPABASE_ANON_KEY: {'✓' if settings.supabase_anon_key else '✗'}")
    logger.info(f"- FIREBASE_PROJECT_ID: {settings.firebase_project_id}")

    try:
        uvicorn.run(
            "app.main:app",
            host=host,
            port=port,
            reload=False,  # Always False in production
            log_level="info",
        )
    except Exception as e:
        logger.error(f"Failed to start server: {e}")
        raise


if __name__ == "__main__":
    main()
