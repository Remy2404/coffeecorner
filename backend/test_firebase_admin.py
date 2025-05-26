import os
import logging
from dotenv import load_dotenv

# Load environment variables
load_dotenv()

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


def test_firebase_admin():
    """Test Firebase Admin SDK initialization"""
    try:
        import firebase_admin
        from firebase_admin import credentials, auth

        # Path to service account key
        firebase_key_path = os.getenv(
            "FIREBASE_ADMIN_SDK_PATH",
            "backend/coffee-corner-2697f-firebase-adminsdk-fbsvc-dd6a599348.json",
        )

        if not os.path.exists(firebase_key_path):
            logger.error(f"Firebase Admin SDK file not found: {firebase_key_path}")
            return False

        # Initialize Firebase Admin SDK
        cred = credentials.Certificate(firebase_key_path)
        firebase_admin.initialize_app(cred)

        logger.info("Firebase Admin SDK initialized successfully!")

        # Test token verification (this will fail with dummy token, but we can see if SDK works)
        try:
            decoded_token = auth.verify_id_token("dummy_token")
        except Exception as e:
            logger.info(f"Expected error with dummy token: {str(e)[:100]}...")
            logger.info("Firebase Admin SDK is working correctly!")

        return True

    except ImportError:
        logger.error(
            "Firebase Admin SDK not installed. Install with: pip install firebase-admin"
        )
        return False
    except Exception as e:
        logger.error(f"Firebase Admin SDK test failed: {e}")
        return False


def main():
    """Main function"""
    logger.info("Testing Firebase Admin SDK...")

    success = test_firebase_admin()

    if success:
        logger.info("Firebase Admin SDK test completed successfully!")
    else:
        logger.error("Firebase Admin SDK test failed!")


if __name__ == "__main__":
    main()
