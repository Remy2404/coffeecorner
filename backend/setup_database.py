#!/usr/bin/env python3
"""
Database setup script for the Coffee Shop app.
This script will create all necessary tables and relationships in Supabase.
"""

import os
import sys
import logging
from dotenv import load_dotenv
import psycopg2
from psycopg2.extensions import ISOLATION_LEVEL_AUTOCOMMIT

# Load environment variables
load_dotenv()

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


def get_database_connection():
    """Get a direct PostgreSQL connection to Supabase"""
    database_url = os.getenv("DATABASE_URL")
    if not database_url:
        logger.error("DATABASE_URL not found in environment variables")
        return None

    try:
        conn = psycopg2.connect(database_url)
        conn.set_isolation_level(ISOLATION_LEVEL_AUTOCOMMIT)
        logger.info("Connected to database successfully")
        return conn
    except Exception as e:
        logger.error(f"Failed to connect to database: {e}")
        return None


def run_sql_file(conn, file_path):
    """Run SQL commands from a file"""
    try:
        with open(file_path, "r", encoding="utf-8") as file:
            sql_content = file.read()

        cursor = conn.cursor()
        cursor.execute(sql_content)
        cursor.close()

        logger.info(f"Successfully executed SQL file: {file_path}")
        return True
    except Exception as e:
        logger.error(f"Failed to execute SQL file {file_path}: {e}")
        return False


def main():
    """Main function to set up the database"""
    logger.info("Starting database setup...")

    # Get database connection
    conn = get_database_connection()
    if not conn:
        sys.exit(1)

    # Run the setup SQL file
    sql_file = "supabase_setup.sql"
    if not os.path.exists(sql_file):
        logger.error(f"SQL file not found: {sql_file}")
        sys.exit(1)

    success = run_sql_file(conn, sql_file)

    # Close connection
    conn.close()

    if success:
        logger.info("Database setup completed successfully!")
    else:
        logger.error("Database setup failed!")
        sys.exit(1)


if __name__ == "__main__":
    main()
