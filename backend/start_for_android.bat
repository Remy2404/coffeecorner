@echo off
echo Starting Coffee Shop Backend Server for Android Development...
echo.

REM Kill any existing processes on port 8000
echo Cleaning up any existing processes on port 8000...
for /f "tokens=5" %%a in ('netstat -aon ^| find ":8000"') do (
    if not "%%a"=="0" (
        echo Terminating process %%a
        taskkill /f /pid %%a >nul 2>&1
    )
)

echo.
echo Starting server on 0.0.0.0:8000 (accessible from Android emulator)...
echo Android app should connect to: http://10.0.2.2:8000
echo Web browser can access: http://localhost:8000
echo.

cd /d "%~dp0"
python -m uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload

pause
