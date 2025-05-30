# Cart Functionality Fix Deployment Checklist

This document outlines the steps to fix the cart functionality issues in the Coffee Corner app.

## Issues Fixed

1. **Authentication Issue**: Fixed the Supabase authentication method in `cart_service.py`
   - Changed from `client.auth.set_auth_cookie()` to the correct `client.auth.set_auth()`
   - This resolves "AttributeError: 'SupabaseAuthClient' object has no attribute 'set_auth'"

2. **Code Syntax Issue**: Fixed missing `@staticmethod` decorator and syntax errors 
   - Added missing decorator to `remove_from_cart` method
   - Fixed incorrect formatting in the `clear_cart` method

## Deployment Steps

1. **Backup Current Files**
   - Backup `backend/app/services/cart_service.py` before replacing

2. **Deploy Fixed Files**
   - Run `backend/apply_cart_fixes.bat` to:
     - Copy the fixed `cart_service_fixed.py` to `cart_service.py`
     - Test the cart service
     - Apply database fixes
     - Restart the backend

3. **Verify Fixes**
   - Run `python backend/test_cart_fix.py` to verify authentication is working
   - Test cart operations in the Android app

4. **Rollback Plan (If Needed)**
   - Restore the backup of `cart_service.py`
   - Restart the backend

## Administrator Notes

- The main issue was in the Supabase authentication method. The `set_auth_cookie()` method used previously doesn't exist in the Supabase client library version being used.
- The correct method is `set_auth()`.
- If you encounter any issues with the fix, check the Supabase client version documentation for the correct authentication method.
- The Android app's `CartRepository` and `TokenAuthenticator` use the correct authentication headers, so no changes were needed on the client side.

## Testing Summary

After applying the fixes, you should be able to:
1. Add items to your cart
2. View your cart items
3. Update quantities
4. Remove items
5. Clear your cart

All operations should persist on the backend when authenticated.

## Need Help?

If you encounter any issues after deploying these fixes, please contact the development team for assistance.
