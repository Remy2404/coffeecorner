@echo off
echo =====================================
echo Coffee Corner App - Fix Profiles
echo =====================================
echo.
echo This script will apply fixes for profile updates
echo.

cd %~dp0

echo Step 1: Running SQL script to fix database schema...
echo You need to run fix_profiles_schema.sql in the Supabase SQL Editor manually
echo.
pause

echo Step 2: Running Python script to fix profiles...
python fix_profiles.py
echo.

echo Step 3: Restarting the backend server...
call restart_backend.bat
echo.

echo All done! Try updating your profile again in the app.
