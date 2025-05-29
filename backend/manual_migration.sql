-- CRITICAL: Manual Database Migration for Firebase UID Compatibility
-- Run this script in Supabase SQL Editor (https://supabase.com/dashboard/project/YOUR_PROJECT/sql)
-- This fixes UUID format incompatibility between Firebase UIDs and PostgreSQL

-- STEP 1: Create backup tables
CREATE TABLE IF NOT EXISTS profiles_backup AS
SELECT *
FROM public.profiles;

CREATE TABLE IF NOT EXISTS cart_items_backup AS
SELECT *
FROM public.cart_items;

CREATE TABLE IF NOT EXISTS orders_backup AS
SELECT *
FROM public.orders;

CREATE TABLE IF NOT EXISTS favorites_backup AS
SELECT *
FROM public.favorites;

CREATE TABLE IF NOT EXISTS notifications_backup AS
SELECT *
FROM public.notifications;

-- STEP 2: Drop foreign key constraints
ALTER TABLE public.cart_items
DROP CONSTRAINT IF EXISTS cart_items_user_id_fkey;

ALTER TABLE public.orders
DROP CONSTRAINT IF EXISTS orders_user_id_fkey;

ALTER TABLE public.favorites
DROP CONSTRAINT IF EXISTS favorites_user_id_fkey;

ALTER TABLE public.notifications
DROP CONSTRAINT IF EXISTS notifications_user_id_fkey;

ALTER TABLE public.cart_items
DROP CONSTRAINT IF EXISTS cart_items_user_id_product_id_key;

ALTER TABLE public.favorites
DROP CONSTRAINT IF EXISTS favorites_user_id_product_id_key;

-- STEP 3: Convert UUID columns to TEXT
ALTER TABLE public.profiles ALTER COLUMN id DROP DEFAULT;

ALTER TABLE public.profiles ALTER COLUMN id TYPE TEXT USING id::TEXT;

ALTER TABLE public.cart_items ALTER COLUMN user_id TYPE TEXT USING user_id::TEXT;

ALTER TABLE public.orders ALTER COLUMN user_id TYPE TEXT USING user_id::TEXT;

ALTER TABLE public.favorites ALTER COLUMN user_id TYPE TEXT USING user_id::TEXT;

ALTER TABLE public.notifications ALTER COLUMN user_id TYPE TEXT USING user_id::TEXT;

-- STEP 4: Recreate foreign key constraints
ALTER TABLE public.cart_items
ADD CONSTRAINT cart_items_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.profiles (id) ON DELETE CASCADE;

ALTER TABLE public.orders
ADD CONSTRAINT orders_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.profiles (id) ON DELETE CASCADE;

ALTER TABLE public.favorites
ADD CONSTRAINT favorites_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.profiles (id) ON DELETE CASCADE;

ALTER TABLE public.notifications
ADD CONSTRAINT notifications_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.profiles (id) ON DELETE CASCADE;

-- STEP 5: Recreate unique constraints
ALTER TABLE public.cart_items
ADD CONSTRAINT cart_items_user_id_product_id_key UNIQUE (user_id, product_id);

ALTER TABLE public.favorites
ADD CONSTRAINT favorites_user_id_product_id_key UNIQUE (user_id, product_id);

-- STEP 6: Test Firebase UID insertion
INSERT INTO
    public.profiles (
        id,
        firebase_uid,
        email,
        full_name
    )
VALUES (
        'V4HVeaXIhARgUD4VL4EIh5hf7X42',
        'V4HVeaXIhARgUD4VL4EIh5hf7X42',
        'test@migration.com',
        'Migration Test'
    ) ON CONFLICT (email) DO
UPDATE
SET
    id = EXCLUDED.id,
    firebase_uid = EXCLUDED.firebase_uid;

-- Clean up test data
DELETE FROM public.profiles WHERE email = 'test@migration.com';

-- SUCCESS MESSAGE
SELECT 'Firebase UID Migration Completed Successfully! 🎉' AS result;