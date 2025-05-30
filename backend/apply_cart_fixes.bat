@echo off
echo Applying cart service fixes to Coffee Corner App...

echo 1. Copying fixed cart service...
copy /Y "app\services\cart_service_fixed.py" "app\services\cart_service.py"

echo 2. Testing cart service...
python test_cart_fix.py

echo 3. Applying database fixes...
python fix_cart_and_auth.py

echo 4. Restarting backend...
call restart_backend.bat

echo All fixes applied! The cart functionality should now work properly.
echo The backend has been restarted.
