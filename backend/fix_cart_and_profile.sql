-- Title: Fix Coffee Corner App Database Issues
-- Description: SQL script to fix profile updates and cart operations

-- 1. First check the user's auth status, UUID, and roles
SELECT
  auth.uid() as "Current User UUID",
  auth.role() as "Current Role";

-- 2. Examine and fix RLS policies for cart_items table
DROP POLICY IF EXISTS "Allow insert for authenticated users" ON public.cart_items;
DROP POLICY IF EXISTS "Allow user access to their cart" ON public.cart_items;
DROP POLICY IF EXISTS "Enable read access for all users" ON public.cart_items;

-- Create proper policies for cart_items
-- Make sure these policies use the authenticated role
CREATE POLICY "Allow insert for authenticated users"
ON public.cart_items
FOR INSERT
TO authenticated
WITH CHECK (user_id = auth.uid()::text);

CREATE POLICY "Allow user access to their cart"
ON public.cart_items
FOR ALL
TO authenticated
USING (user_id = auth.uid()::text);

-- 3. Inspect potential issues with uuid vs text type mismatch
SELECT pg_typeof(auth.uid()) as "auth_uid_type";

-- 4. Check if existing cart_items are stored correctly
SELECT id, user_id, product_id, quantity, 
  pg_typeof(user_id) as user_id_type,
  pg_typeof(id) as id_type
FROM public.cart_items
LIMIT 5;

-- 5. Check if user profiles are updated correctly
-- List profile fields to confirm proper structure
SELECT column_name, data_type 
FROM information_schema.columns 
WHERE table_schema = 'public' 
AND table_name = 'profiles';

-- Finally, ensure auth.uid() matches in the right format
CREATE OR REPLACE FUNCTION debug_auth_uid()
RETURNS TABLE (
  raw_uid UUID,
  text_uid TEXT,
  is_authenticated BOOLEAN
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
  RETURN QUERY
  SELECT 
    auth.uid() as raw_uid,
    auth.uid()::text as text_uid,
    auth.role() = 'authenticated' as is_authenticated;
END;
$$;

-- Grant execute permission
GRANT EXECUTE ON FUNCTION debug_auth_uid TO authenticated;
GRANT EXECUTE ON FUNCTION debug_auth_uid TO anon;
