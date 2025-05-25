-- Coffee Shop App Database Setup for Supabase
-- This script creates all necessary tables and relationships

-- Enable RLS (Row Level Security)
-- Create profiles table (extends Supabase auth.users)
CREATE TABLE IF NOT EXISTS public.profiles (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    firebase_uid TEXT UNIQUE,
    email TEXT NOT NULL UNIQUE,
    full_name TEXT,
    phone TEXT,
    address TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Create products table
CREATE TABLE IF NOT EXISTS public.products (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    image_url TEXT,
    category TEXT NOT NULL,
    is_available BOOLEAN DEFAULT true,
    rating DECIMAL(3,2) DEFAULT 0.0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Create cart_items table
CREATE TABLE IF NOT EXISTS public.cart_items (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID REFERENCES public.profiles(id) ON DELETE CASCADE NOT NULL,
    product_id UUID REFERENCES public.products(id) ON DELETE CASCADE NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    UNIQUE(user_id, product_id)
);

-- Create orders table
CREATE TABLE IF NOT EXISTS public.orders (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID REFERENCES public.profiles(id) ON DELETE CASCADE NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status TEXT NOT NULL DEFAULT 'pending',
    delivery_address TEXT,
    phone TEXT,
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Create order_items table
CREATE TABLE IF NOT EXISTS public.order_items (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    order_id UUID REFERENCES public.orders(id) ON DELETE CASCADE NOT NULL,
    product_id UUID REFERENCES public.products(id) ON DELETE CASCADE NOT NULL,
    quantity INTEGER NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Create favorites table
CREATE TABLE IF NOT EXISTS public.favorites (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID REFERENCES public.profiles(id) ON DELETE CASCADE NOT NULL,
    product_id UUID REFERENCES public.products(id) ON DELETE CASCADE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    UNIQUE(user_id, product_id)
);

-- Create notifications table
CREATE TABLE IF NOT EXISTS public.notifications (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID REFERENCES public.profiles(id) ON DELETE CASCADE NOT NULL,
    title TEXT NOT NULL,
    message TEXT NOT NULL,
    type TEXT NOT NULL DEFAULT 'info',
    is_read BOOLEAN DEFAULT false,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_profiles_email ON public.profiles(email);
CREATE INDEX IF NOT EXISTS idx_products_category ON public.products(category);
CREATE INDEX IF NOT EXISTS idx_products_available ON public.products(is_available);
CREATE INDEX IF NOT EXISTS idx_cart_items_user_id ON public.cart_items(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_user_id ON public.orders(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON public.orders(status);
CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON public.order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_favorites_user_id ON public.favorites(user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON public.notifications(user_id);

-- Enable Row Level Security (RLS)
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.cart_items ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.orders ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.order_items ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.favorites ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.notifications ENABLE ROW LEVEL SECURITY;

-- RLS Policies for profiles
CREATE POLICY "Users can view own profile" ON public.profiles
    FOR SELECT USING (id = (current_setting('request.jwt.claims', true)::json->>'user_id')::uuid);

CREATE POLICY "Users can update own profile" ON public.profiles
    FOR UPDATE USING (id = (current_setting('request.jwt.claims', true)::json->>'user_id')::uuid);

CREATE POLICY "Users can insert own profile" ON public.profiles
    FOR INSERT WITH CHECK (true);

-- RLS Policies for cart_items
CREATE POLICY "Users can manage own cart" ON public.cart_items
    FOR ALL USING (user_id = (current_setting('request.jwt.claims', true)::json->>'user_id')::uuid);

-- RLS Policies for orders
CREATE POLICY "Users can view own orders" ON public.orders
    FOR SELECT USING (user_id = (current_setting('request.jwt.claims', true)::json->>'user_id')::uuid);

CREATE POLICY "Users can create own orders" ON public.orders
    FOR INSERT WITH CHECK (user_id = (current_setting('request.jwt.claims', true)::json->>'user_id')::uuid);

-- RLS Policies for order_items
CREATE POLICY "Users can view own order items" ON public.order_items
    FOR SELECT USING (
        EXISTS (
            SELECT 1 FROM public.orders 
            WHERE id = order_items.order_id AND user_id = (current_setting('request.jwt.claims', true)::json->>'user_id')::uuid
        )
    );

CREATE POLICY "Users can create order items for own orders" ON public.order_items
    FOR INSERT WITH CHECK (
        EXISTS (
            SELECT 1 FROM public.orders 
            WHERE id = order_items.order_id AND user_id = (current_setting('request.jwt.claims', true)::json->>'user_id')::uuid
        )
    );

-- RLS Policies for favorites
CREATE POLICY "Users can manage own favorites" ON public.favorites
    FOR ALL USING (user_id = (current_setting('request.jwt.claims', true)::json->>'user_id')::uuid);

-- RLS Policies for notifications
CREATE POLICY "Users can view own notifications" ON public.notifications
    FOR SELECT USING (user_id = (current_setting('request.jwt.claims', true)::json->>'user_id')::uuid);

CREATE POLICY "Users can update own notifications" ON public.notifications
    FOR UPDATE USING (user_id = (current_setting('request.jwt.claims', true)::json->>'user_id')::uuid);

-- Products table is public (no RLS needed for reading)
-- But we might want to restrict who can modify products

-- Insert sample products
INSERT INTO public.products (name, description, price, image_url, category, rating) VALUES
('Espresso', 'Rich and strong coffee shot', 2.50, 'https://via.placeholder.com/150', 'coffee', 4.5),
('Cappuccino', 'Espresso with steamed milk and foam', 3.75, 'https://via.placeholder.com/150', 'coffee', 4.7),
('Latte', 'Espresso with steamed milk', 4.25, 'https://via.placeholder.com/150', 'coffee', 4.6),
('Americano', 'Espresso with hot water', 3.00, 'https://via.placeholder.com/150', 'coffee', 4.3),
('Mocha', 'Espresso with chocolate and steamed milk', 4.75, 'https://via.placeholder.com/150', 'coffee', 4.8),
('Macchiato', 'Espresso with a dollop of foam', 3.25, 'https://via.placeholder.com/150', 'coffee', 4.4),
('Cold Brew', 'Smooth cold coffee', 3.50, 'https://via.placeholder.com/150', 'coffee', 4.2),
('Iced Coffee', 'Regular coffee served cold', 3.25, 'https://via.placeholder.com/150', 'coffee', 4.1),
('Croissant', 'Buttery French pastry', 2.25, 'https://via.placeholder.com/150', 'pastry', 4.5),
('Muffin', 'Soft baked muffin', 2.75, 'https://via.placeholder.com/150', 'pastry', 4.3),
('Donut', 'Sweet glazed donut', 1.75, 'https://via.placeholder.com/150', 'pastry', 4.0),
('Bagel', 'Fresh baked bagel', 2.50, 'https://via.placeholder.com/150', 'pastry', 4.2),
('Green Tea', 'Premium green tea', 2.25, 'https://via.placeholder.com/150', 'tea', 4.1),
('Earl Grey', 'Classic black tea with bergamot', 2.50, 'https://via.placeholder.com/150', 'tea', 4.3),
('Sandwich', 'Fresh made sandwich', 5.50, 'https://via.placeholder.com/150', 'food', 4.4),
('Salad', 'Fresh garden salad', 4.75, 'https://via.placeholder.com/150', 'food', 4.2)
ON CONFLICT DO NOTHING;

-- Create function to handle updated_at timestamps
CREATE OR REPLACE FUNCTION handle_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create triggers for updated_at
CREATE TRIGGER handle_profiles_updated_at BEFORE UPDATE ON public.profiles
    FOR EACH ROW EXECUTE FUNCTION handle_updated_at();

CREATE TRIGGER handle_products_updated_at BEFORE UPDATE ON public.products
    FOR EACH ROW EXECUTE FUNCTION handle_updated_at();

CREATE TRIGGER handle_cart_items_updated_at BEFORE UPDATE ON public.cart_items
    FOR EACH ROW EXECUTE FUNCTION handle_updated_at();

CREATE TRIGGER handle_orders_updated_at BEFORE UPDATE ON public.orders
    FOR EACH ROW EXECUTE FUNCTION handle_updated_at();
