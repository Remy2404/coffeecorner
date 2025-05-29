#!/usr/bin/env python3
"""
Test Supabase connection using the Supabase client
"""

import os
import asyncio
import logging
from dotenv import load_dotenv
from supabase import create_client, Client

# Load environment variables
load_dotenv()

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


async def test_supabase_connection():
    """Test connection to Supabase using the client"""
    try:
        supabase_url = os.getenv("SUPABASE_URL")
        supabase_key = os.getenv("SUPABASE_ANON_KEY")

        if not supabase_url or not supabase_key:
            logger.error("Supabase URL or key not found in environment variables")
            return False

        logger.info(f"Testing connection to: {supabase_url}")

        # Create Supabase client
        supabase: Client = create_client(supabase_url, supabase_key)

        # Test connection by trying to query a system table
        response = supabase.table("profiles").select("*").limit(1).execute()

        logger.info("Successfully connected to Supabase!")
        logger.info(f"Response: {response}")
        return True

    except Exception as e:
        logger.error(f"Failed to connect to Supabase: {e}")
        return False


def main():
    """Main function"""
    logger.info("Testing Supabase connection...")

    # Run the async function
    result = asyncio.run(test_supabase_connection())

    if result:
        logger.info("Supabase connection test successful!")
    else:
        logger.error("Supabase connection test failed!")


if __name__ == "__main__":
    main()
