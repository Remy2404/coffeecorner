
        -- Disable RLS temporarily for testing
        ALTER TABLE cart_items DISABLE ROW LEVEL SECURITY;
        
        -- Re-enable RLS with proper policies
        ALTER TABLE cart_items ENABLE ROW LEVEL SECURITY;
        
        -- Drop existing policies if they exist
        DROP POLICY IF EXISTS "Users can manage their own cart items" ON cart_items;
        DROP POLICY IF EXISTS "Users can view their own cart items" ON cart_items;
        DROP POLICY IF EXISTS "Users can insert their own cart items" ON cart_items;
        DROP POLICY IF EXISTS "Users can update their own cart items" ON cart_items;
        DROP POLICY IF EXISTS "Users can delete their own cart items" ON cart_items;
        
        -- Create comprehensive RLS policies for cart_items
        CREATE POLICY "Users can view their own cart items" ON cart_items
            FOR SELECT USING (auth.uid()::text = user_id);
            
        CREATE POLICY "Users can insert their own cart items" ON cart_items
            FOR INSERT WITH CHECK (auth.uid()::text = user_id);
            
        CREATE POLICY "Users can update their own cart items" ON cart_items
            FOR UPDATE USING (auth.uid()::text = user_id);
            
        CREATE POLICY "Users can delete their own cart items" ON cart_items
            FOR DELETE USING (auth.uid()::text = user_id);
        