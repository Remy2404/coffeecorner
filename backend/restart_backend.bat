@echo off
echo =====================================
echo Coffee Corner App - Backend Restart
echo =====================================
echo.
echo This script will restart the backend server with the recent fixes for:
echo  1. Profile updates not being saved
echo  2. Cart functionality (500 internal server errors)
echo.
echo Make sure you have all required dependencies installed.
echo.

cd %~dp0
cd ..

echo Stopping any running backend processes...
taskkill /f /im python.exe /fi "WINDOWTITLE eq Coffee*" > nul 2>&1

echo.
echo Starting the backend server...
echo.
start "Coffee Corner Backend" cmd /k python start.py

echo.
echo Backend server is starting in a new window...
echo If you don't see a new terminal window, the server may have failed to start.
echo Check for any error messages in that window.
echo.
echo After the backend starts, go to your Android app and test:
echo  1. Profile updates (you should be able to save name, phone, gender)
echo  2. Adding items to cart (should no longer give 500 errors)
echo.

pause
