# Coffee Corner App - Authentication Debug Guide

## Overview

This guide explains how to debug JWT authentication issues in the Coffee Corner Android app using the built-in diagnostic tools.

## Quick Start - Running Diagnostics

### Step 1: Access Debug Menu

1. Open the Coffee Corner app
2. Navigate to the **Profile** tab (bottom navigation)
3. **Tap the profile picture 7 times quickly** to open the debug menu

### Step 2: Debug Menu Options

The debug menu provides these options:

1. **Run Auth Diagnostic** - Comprehensive authentication test
2. **Check Auth State** - Quick check of current authentication status
3. **Force Re-auth** - Force fresh authentication with backend
4. **Clear Auth Data** - Reset all authentication data (requires login)

## Detailed Diagnostic Information

### 1. Run Auth Diagnostic

This performs a comprehensive test of the authentication flow:

**Firebase Authentication Check:**

- ✓ Verifies Firebase user is logged in
- ✓ Checks Firebase UID and email
- ✓ Gets fresh Firebase ID token
- ✓ Shows token length and preview

**Stored Data Check:**

- ✓ Checks `PreferencesHelper.isLoggedIn()`
- ✓ Verifies JWT access token storage
- ✓ Shows user data (ID, name, email)

**Backend Authentication Test:**

- ✓ Tests Firebase token → JWT exchange
- ✓ Verifies backend returns access token
- ✓ Checks user data in response

**JWT Endpoints Test:**

- ✓ Tests `/cart` endpoint with JWT
- ✓ Tests `/orders` endpoint with JWT
- ✓ Tests user profile endpoints

### 2. Check Auth State

Quick overview showing:

- Login status (`true`/`false`)
- JWT token presence (`present`/`null`)
- Current user ID

### 3. Force Re-auth

- Gets fresh Firebase ID token
- Re-authenticates with backend
- Saves new JWT access token
- Re-tests JWT endpoints

### 4. Clear Auth Data

- Clears all stored user data
- Signs out from Firebase
- Returns to login screen

## Common Authentication Issues & Solutions

### Issue 1: "User not found" (404 Error)

**Symptoms:**

- Cart/Orders return 404 "User not found"
- JWT token present but invalid

**Debug Steps:**

1. Run "Auth Diagnostic"
2. Check if Firebase authentication is successful
3. Verify backend authentication returns valid JWT
4. Check JWT token format and expiration

**Solution:**

```
1. Clear Auth Data
2. Login again with valid Firebase credentials
3. Verify backend `/auth/firebase-auth` endpoint works
4. Ensure JWT secret is consistent between sessions
```

### Issue 2: No JWT Token Stored

**Symptoms:**

- Auth state shows "Token: null"
- All JWT endpoints fail

**Debug Steps:**

1. Check "Auth State"
2. Run "Auth Diagnostic" → Backend Auth Test
3. Check Firebase authentication status

**Solution:**

```
1. Ensure user is logged in to Firebase
2. Force Re-auth to get fresh tokens
3. Check backend logs for authentication errors
```

### Issue 3: JWT Token Expired

**Symptoms:**

- Authentication was working before
- Now getting 401/403 errors

**Debug Steps:**

1. Run "Auth Diagnostic"
2. Check token expiration in logs
3. Try "Force Re-auth"

**Solution:**

```
1. Force Re-auth to get fresh JWT
2. Consider implementing automatic token refresh
```

## Log Analysis

### Reading Diagnostic Logs

Use Android Studio Logcat or `adb logcat` to view detailed logs:

```bash
adb logcat -s AuthDiagnostic MainActivity UserRepository RetrofitClient
```

### Key Log Tags

- `AuthDiagnostic` - Main diagnostic output
- `UserRepository` - Authentication flow
- `RetrofitClient` - JWT token debugging
- `CartRepository` - Cart API calls
- `OrderRepository` - Order API calls

### Important Log Messages

**✓ Success Indicators:**

```
✓ Firebase user logged in: user@example.com
✓ Firebase ID token obtained (length: 943)
✓ Backend auth successful: true
✓ Access token saved successfully
✓ Cart endpoint success: true
```

**✗ Error Indicators:**

```
✗ No Firebase user logged in
✗ Backend auth failed: 404
✗ Cart endpoint failed: 404
✗ No auth token stored
```

## Backend Server Requirements

Ensure the backend server is running:

```bash
cd backend
python -m uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
```

**Check server status:**

- Server should be accessible at `http://localhost:8000`
- Check `/docs` endpoint for API documentation
- Verify Firebase configuration is correct

## Testing Different Scenarios

### Test 1: Fresh Install Authentication

1. Clear Auth Data
2. Login with Firebase credentials
3. Run Auth Diagnostic
4. Verify all endpoints work

### Test 2: Token Refresh

1. Login successfully
2. Wait for token expiration (or manipulate token)
3. Try cart/order operations
4. Force Re-auth if needed

### Test 3: Network Connectivity

1. Disable network
2. Try authentication operations
3. Check error handling
4. Re-enable network and Force Re-auth

## Troubleshooting Steps

### Step 1: Verify Environment

- ✓ Backend server running on port 8000
- ✓ Android emulator/device connected
- ✓ Network connectivity between app and server

### Step 2: Check Firebase Configuration

- ✓ `google-services.json` is present and valid
- ✓ Firebase project has authentication enabled
- ✓ Email/password authentication is configured

### Step 3: Backend Authentication

- ✓ Backend can verify Firebase ID tokens
- ✓ JWT secret is configured correctly
- ✓ User exists in backend database

### Step 4: Token Management

- ✓ JWT tokens are being saved to SharedPreferences
- ✓ Tokens are included in API requests
- ✓ Token format is correct (Bearer token)

## Advanced Debugging

### Manual Token Testing

Use the backend's `/docs` interface to manually test JWT endpoints:

1. Get Firebase ID token from diagnostic logs
2. Call `/auth/firebase-auth` with the token
3. Copy the returned JWT access token
4. Test `/cart` and `/orders` endpoints with the JWT

### Database Verification

Check if user exists in backend database:

```bash
cd backend
python debug_db.py
```

### Network Traffic Analysis

Use Android Studio's Network Inspector or Charles Proxy to inspect HTTP requests and responses.

## Integration with Development Workflow

### During Development

- Run "Auth Diagnostic" after any authentication changes
- Use "Check Auth State" for quick status checks
- Clear Auth Data when switching test accounts

### Before Testing

- Ensure backend server is running
- Run "Auth Diagnostic" to verify system health
- Check logs for any authentication warnings

### Bug Reports

When reporting authentication bugs, include:

- Full diagnostic log output
- Steps to reproduce
- Current authentication state
- Backend server logs (if available)

## Next Steps

After fixing authentication issues:

1. **Test Cart Functionality:**

   - Add items to cart
   - Verify cart persistence
   - Test cart synchronization

2. **Test Order Functionality:**

   - Place test orders
   - Check order history
   - Verify order status updates

3. **End-to-End Testing:**
   - Complete user registration → login → shopping → ordering flow
   - Test across app restarts
   - Verify data persistence

## Support

If authentication issues persist after following this guide:

1. Check the latest backend logs
2. Verify Firebase configuration
3. Ensure JWT secret consistency
4. Consider regenerating Firebase service account key
