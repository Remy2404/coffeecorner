import firebase_admin
from firebase_admin import credentials, auth
from app.core.config import settings
import os
import logging

logger = logging.getLogger(__name__)


class FirebaseAuthService:
    _initialized = False

    @classmethod
    def initialize_firebase(cls):
        """Initialize Firebase Admin SDK"""
        if cls._initialized:
            return

        try:
            # Check if Firebase is already initialized
            firebase_admin.get_app()
            logger.info("Firebase Admin SDK already initialized")
        except ValueError:
            # Firebase not initialized, let's initialize it
            try:
                # Try to use service account file
                if os.path.exists(settings.firebase_admin_sdk_path):
                    logger.info(
                        f"Initializing Firebase with service account: {settings.firebase_admin_sdk_path}"
                    )
                    cred = credentials.Certificate(settings.firebase_admin_sdk_path)
                    firebase_admin.initialize_app(cred)
                else:
                    logger.warning(
                        f"Service account file not found: {settings.firebase_admin_sdk_path}"
                    )
                    # Use default credentials (for production with environment variables)
                    cred = credentials.ApplicationDefault()
                    firebase_admin.initialize_app(
                        cred,
                        {
                            "projectId": settings.firebase_project_id,
                        },
                    )

                cls._initialized = True
                logger.info("Firebase Admin SDK initialized successfully")

            except Exception as e:
                logger.error(f"Failed to initialize Firebase Admin SDK: {e}")
                # For development, we'll continue without Firebase if it fails
                raise

    @classmethod
    def verify_firebase_token(cls, id_token: str) -> dict:
        """Verify Firebase ID token and return user data"""
        logger.info("Starting Firebase token verification...")
        cls.initialize_firebase()

        try:
            logger.info(f"Attempting to verify token of length: {len(id_token)}")
            # Verify the ID token
            decoded_token = auth.verify_id_token(id_token)
            logger.info(
                f"Token verified successfully for user: {decoded_token.get('email')}"
            )
            return {
                "uid": decoded_token["uid"],
                "email": decoded_token.get("email"),
                "name": decoded_token.get("name"),
                "email_verified": decoded_token.get("email_verified", False),
                "firebase_uid": decoded_token["uid"],
            }
        except Exception as e:
            logger.error(f"Firebase token verification failed: {e}")
            logger.error(f"Token being verified: {id_token[:50]}...")
            raise ValueError("Invalid Firebase token")

    @classmethod
    def get_user_by_uid(cls, uid: str):
        """Get Firebase user by UID"""
        cls.initialize_firebase()

        try:
            user_record = auth.get_user(uid)
            return {
                "uid": user_record.uid,
                "email": user_record.email,
                "name": user_record.display_name,
                "email_verified": user_record.email_verified,
            }
        except Exception as e:
            logger.error(f"Failed to get Firebase user: {e}")
            return None
