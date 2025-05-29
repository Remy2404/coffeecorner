-- Fix Profile Updates in Coffee Corner App

-- 1. Check the current schema for the profiles table
SELECT
    column_name,
    data_type,
    is_nullable
FROM information_schema.columns
WHERE
    table_schema = 'public'
    AND table_name = 'profiles';

-- 2. Check if 'full_name' column exists
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_schema = 'public' 
          AND table_name = 'profiles' 
          AND column_name = 'full_name'
    ) THEN
        -- If it doesn't exist, add it
        ALTER TABLE public.profiles ADD COLUMN full_name TEXT;
        RAISE NOTICE 'Added full_name column to profiles table';
    ELSE
        RAISE NOTICE 'full_name column already exists in profiles table';
    END IF;
END $$;

-- 3. Check if 'name' column exists (might have been added by mistake)
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_schema = 'public' 
          AND table_name = 'profiles' 
          AND column_name = 'name'
    ) THEN
        -- If name exists, migrate data to full_name and drop the column
        UPDATE public.profiles 
        SET full_name = name 
        WHERE full_name IS NULL AND name IS NOT NULL;
        
        ALTER TABLE public.profiles DROP COLUMN name;
        RAISE NOTICE 'Migrated data from name to full_name and dropped name column';
    ELSE
        RAISE NOTICE 'name column does not exist in profiles table';
    END IF;
END $$;

-- 4. Check if 'gender' column exists
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_schema = 'public' 
          AND table_name = 'profiles' 
          AND column_name = 'gender'
    ) THEN
        -- If it doesn't exist, add it
        ALTER TABLE public.profiles ADD COLUMN gender TEXT;
        RAISE NOTICE 'Added gender column to profiles table';
    ELSE
        RAISE NOTICE 'gender column already exists in profiles table';
    END IF;
END $$;

-- 5. Check if 'profile_image_url' column exists
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_schema = 'public' 
          AND table_name = 'profiles' 
          AND column_name = 'profile_image_url'
    ) THEN
        -- If it doesn't exist, add it
        ALTER TABLE public.profiles ADD COLUMN profile_image_url TEXT;
        RAISE NOTICE 'Added profile_image_url column to profiles table';
    ELSE
        RAISE NOTICE 'profile_image_url column already exists in profiles table';
    END IF;
END $$;

-- 6. Check if 'date_of_birth' column exists
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_schema = 'public' 
          AND table_name = 'profiles' 
          AND column_name = 'date_of_birth'
    ) THEN
        -- If it doesn't exist, add it
        ALTER TABLE public.profiles ADD COLUMN date_of_birth TEXT;
        RAISE NOTICE 'Added date_of_birth column to profiles table';
    ELSE
        RAISE NOTICE 'date_of_birth column already exists in profiles table';
    END IF;
END $$;

-- 7. Check if 'firebase_uid' column exists (to store Firebase user ID if different from main ID)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_schema = 'public' 
          AND table_name = 'profiles' 
          AND column_name = 'firebase_uid'
    ) THEN
        -- If it doesn't exist, add it
        ALTER TABLE public.profiles ADD COLUMN firebase_uid TEXT;
        -- Copy ID to firebase_uid as a starting point
        UPDATE public.profiles SET firebase_uid = id;
        RAISE NOTICE 'Added firebase_uid column to profiles table';
    ELSE
        RAISE NOTICE 'firebase_uid column already exists in profiles table';
    END IF;
END $$;

-- 8. Verify sample data
SELECT * FROM public.profiles LIMIT 10;

-- 9. Verify RLS policies
SELECT
    policyname,
    schemaname,
    tablename,
    permissive,
    roles,
    cmd,
    format('%L', qual) as using_expression,
    format('%L', with_check) as with_check_expression
FROM pg_policies
WHERE
    tablename = 'profiles';