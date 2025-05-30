@echo off
echo ===============================================
echo COFFEE SHOP APP - COMPREHENSIVE FIXES
echo ===============================================

echo.
echo [1/4] Stopping any running FastAPI server...
taskkill /f /im python.exe 2>nul
timeout /t 2 /nobreak >nul

echo.
echo [2/4] Installing dependencies...
pip install -r requirements.txt

echo.
echo [3/4] Starting FastAPI server in background...
start /b python -m uvicorn app.main:app --reload --host 0.0.0.0 --port 8000

echo.
echo [4/4] Waiting for server to start...
timeout /t 5 /nobreak >nul

echo.
echo ===============================================
echo Testing the fixes...
echo ===============================================
python test_comprehensive_fixes.py

echo.
echo ===============================================
echo DEPLOYMENT COMPLETE!
echo ===============================================
echo.
echo Server is running at: http://localhost:8000
echo API Documentation: http://localhost:8000/docs
echo.
echo Next steps:
echo 1. Run Android app tests
echo 2. If tests pass, run: gradlew build
echo.
pause