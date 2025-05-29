# Firebase UID Database Migration Guide

## 🚨 IMPORTANT: Run this migration to fix Firebase authentication issues

The Coffee Corner app is currently failing because Firebase UIDs (28-character alphanumeric strings) cannot be stored in PostgreSQL UUID columns. This migration converts the necessary columns from UUID to TEXT.

## 📋 Pre-Migration Checklist

1. ✅ **Backup**: The migration script automatically creates backup tables
2. ✅ **Testing**: Test on a development database first if possible
3. ✅ **Timing**: Run during low-traffic periods
4. ✅ **Rollback Plan**: Backup tables allow data recovery if needed

## 🌐 Steps to Run Migration in Supabase

### Step 1: Access Supabase SQL Editor
1. Go to [Supabase Dashboard](https://supabase.com/dashboard)
2. Select your project: `coffee-corner-2697f`
3. Navigate to **SQL Editor** in the left sidebar
4. Click **New Query**

### Step 2: Execute the Migration Script
1. Open the file: `backend/firebase_uid_migration.sql`
2. Copy the **entire contents** of the file
3. Paste it into the Supabase SQL Editor
4. Click **Run** button (or press Ctrl+Enter)

### Step 3: Monitor Execution
The script will show progress messages like:
```
NOTICE: Creating backup tables...
NOTICE: Step 1: Dropping foreign key constraints...
NOTICE: Step 2: Converting UUID columns to TEXT...
NOTICE: Step 3: Recreating foreign key constraints...
NOTICE: Step 4: Recreating indexes...
NOTICE: Step 5: Updating RLS policies...
NOTICE: Step 6: Adding validation constraints...
NOTICE: Step 7: Testing Firebase UID insertion...
NOTICE: Step 8: Verifying migration...
NOTICE: ✅ Migration verification passed!
NOTICE: 🎉🎉🎉 MIGRATION COMPLETED SUCCESSFULLY! 🎉🎉🎉
```

### Step 4: Verify Migration Success
If successful, you should see:
```
=== SUMMARY OF CHANGES ===
✅ profiles.id: UUID → TEXT
✅ cart_items.user_id: UUID → TEXT
✅ orders.user_id: UUID → TEXT
✅ favorites.user_id: UUID → TEXT
✅ notifications.user_id: UUID → TEXT
✅ Foreign key constraints: RECREATED
✅ RLS policies: UPDATED for TEXT IDs
✅ Performance indexes: RECREATED
✅ Validation constraints: ADDED
```

## 🧪 Post-Migration Testing

### Test 1: Backend Authentication
```bash
cd backend
python simple_test.py
```
Should show:
```
✅ Firebase UID insertion test passed
✅ Authentication service works correctly
```

### Test 2: Live Authentication Test
```bash
cd backend
python test_auth_fix.py
```
Should show:
```
✅ Firebase token verified successfully
✅ User profile created/retrieved successfully
```

### Test 3: Android App Build
```bash
cd "C:\Users\User\Downloads\Telegram Desktop\Apsaraandroid"
./gradlew build
```

## 🔍 What This Migration Does

### Database Schema Changes
- **profiles.id**: `UUID` → `TEXT` (to store Firebase UIDs directly)
- **cart_items.user_id**: `UUID` → `TEXT`
- **orders.user_id**: `UUID` → `TEXT`
- **favorites.user_id**: `UUID` → `TEXT`
- **notifications.user_id**: `UUID` → `TEXT`

### Foreign Key Constraints
- Drops all existing foreign key constraints
- Recreates them with TEXT data types
- Maintains referential integrity

### RLS Policies Update
- Updates all Row Level Security policies
- Changes `auth.uid() = user_id` to `auth.uid()::TEXT = user_id`
- Maintains proper access control

### Performance Optimization
- Recreates indexes for TEXT columns
- Maintains query performance

### Validation
- Adds constraints to validate Firebase UID format
- Ensures data integrity

## 🛡️ Safety Features

### Automatic Backups
The migration creates these backup tables:
- `profiles_backup`
- `cart_items_backup`
- `orders_backup`
- `order_items_backup`
- `favorites_backup`
- `notifications_backup`

### Transaction Safety
- Entire migration runs in a single transaction
- If any step fails, all changes are rolled back
- Database remains in consistent state

### Verification
- Automated testing of Firebase UID insertion
- Constraint count verification
- Column type verification

## 🔧 Rollback Plan (If Needed)

If something goes wrong, you can restore from backups:

```sql
-- Rollback script (only if needed)
BEGIN;

-- Restore original tables
DROP TABLE public.profiles CASCADE;
CREATE TABLE public.profiles AS SELECT * FROM profiles_backup;

DROP TABLE public.cart_items CASCADE;
CREATE TABLE public.cart_items AS SELECT * FROM cart_items_backup;

DROP TABLE public.orders CASCADE;
CREATE TABLE public.orders AS SELECT * FROM orders_backup;

DROP TABLE public.order_items CASCADE;
CREATE TABLE public.order_items AS SELECT * FROM order_items_backup;

DROP TABLE public.favorites CASCADE;
CREATE TABLE public.favorites AS SELECT * FROM favorites_backup;

DROP TABLE public.notifications CASCADE;
CREATE TABLE public.notifications AS SELECT * FROM notifications_backup;

-- Recreate original constraints and indexes
-- (Run original schema creation script)

COMMIT;
```

## ✅ Success Indicators

After successful migration:
1. **No UUID errors** in backend logs
2. **Firebase authentication works** without errors
3. **User profiles are created** with Firebase UIDs
4. **Android app can authenticate** successfully

## 🚀 Next Steps After Migration

1. **Test the Android app** with Firebase authentication
2. **Verify user registration/login** works end-to-end
3. **Test cart operations** with authenticated users
4. **Check order creation** functionality
5. **Remove backup tables** after confirming everything works

## 📞 Troubleshooting

### Common Issues

**Issue**: "relation does not exist" errors
**Solution**: Make sure you're running the script in the correct database

**Issue**: Permission denied errors
**Solution**: Ensure you're using the service role key, not anon key

**Issue**: Transaction timeout
**Solution**: The migration should complete in under 30 seconds for most databases

### Getting Help

If you encounter issues:
1. Check the exact error message in Supabase SQL Editor
2. Verify your database connection settings
3. Ensure backup tables were created before making changes
4. Contact support with the specific error message

---

**Remember**: This migration is essential for Firebase authentication to work properly. Without it, users cannot log in to your Coffee Corner app!