# Coffee Corner App - Service Role Key Removal Update

This document explains the changes made to allow the application to function without the `SUPABASE_SERVICE_ROLE_KEY`.

## Overview

The service role key was removed for security reasons. The application now operates using only the anon key with special RLS (Row-Level Security) policies to manage permissions.

## Key Changes Made

1. **Updated RLS Policies**

   - Created new RLS policies that work with the anon key
   - Added header-based bypass mechanism for admin operations

2. **Modified Supabase Client**

   - Removed service role key dependency
   - Added special headers to bypass RLS for admin operations

3. **Updated Auth Service**

   - Modified to work with the anon key for user operations
   - Ensures proper user ID synchronization between Firebase and Supabase

4. **Updated API Endpoints**
   - Refactored cart endpoints to extract user ID from auth token
   - Simplified client-side API calls

## How to Run the System

### Backend Setup

1. Make sure you have the correct environment in `fixed.env`:

   ```
   SUPABASE_URL=https://dqjyxspgrcvfcevevtjh.supabase.co
   SUPABASE_ANON_KEY=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   ```

2. Update RLS policies in Supabase:

   ```
   python update_rls_policies.py
   ```

3. Start the backend server:
   ```
   cd backend
   python -m app.main
   ```

### Android Client

1. Build and run the Android app:
   ```
   ./gradlew installDebug
   ```

## Troubleshooting

If you encounter authentication issues:

1. Make sure the Firebase auth is working properly
2. Check that the JWT token is being included in the Authorization header
3. Verify that the user ID in Firebase matches the user ID in Supabase
4. Check the RLS policies are correctly applied

If you have cart operation issues:

1. Make sure you're logged in (Firebase token is valid)
2. Check the network requests for proper Authorization headers
3. Verify that the request parameters match the updated API

## Database Migration

If you need to migrate existing data:

```sql
-- Update user profiles to match Firebase UIDs
UPDATE profiles
SET id = firebase_uid
WHERE id <> firebase_uid;
```
