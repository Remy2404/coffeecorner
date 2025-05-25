from typing import List, Optional
from fastapi import HTTPException, status
from app.database.supabase import supabase
from app.models.schemas import ProductResponse, ProductCreate
import logging

logger = logging.getLogger(__name__)


class ProductService:
    
    @staticmethod
    async def get_all_products(category: Optional[str] = None) -> List[ProductResponse]:
        """Get all products, optionally filtered by category"""
        try:
            query = supabase.table("products").select("*")
            
            if category:
                query = query.eq("category", category)
            
            result = query.execute()
            
            return [ProductResponse(**product) for product in result.data]
            
        except Exception as e:
            logger.error(f"Error fetching products: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to fetch products"
            )
    
    @staticmethod
    async def get_product_by_id(product_id: str) -> ProductResponse:
        """Get a product by its ID"""
        try:
            result = supabase.table("products").select("*").eq("id", product_id).execute()
            
            if not result.data:
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="Product not found"
                )
            
            return ProductResponse(**result.data[0])
            
        except HTTPException:
            raise
        except Exception as e:
            logger.error(f"Error fetching product {product_id}: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to fetch product"
            )
    
    @staticmethod
    async def search_products(query: str) -> List[ProductResponse]:
        """Search products by name or description"""
        try:
            # Use ilike for case-insensitive search
            result = supabase.table("products").select("*").or_(
                f"name.ilike.%{query}%,description.ilike.%{query}%"
            ).execute()
            
            return [ProductResponse(**product) for product in result.data]
            
        except Exception as e:
            logger.error(f"Error searching products: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to search products"
            )
    
    @staticmethod
    async def get_products_by_category(category: str) -> List[ProductResponse]:
        """Get products by category"""
        try:
            result = supabase.table("products").select("*").eq("category", category).execute()
            
            return [ProductResponse(**product) for product in result.data]
            
        except Exception as e:
            logger.error(f"Error fetching products by category {category}: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to fetch products by category"
            )
    
    @staticmethod
    async def get_categories() -> List[str]:
        """Get all unique product categories"""
        try:
            result = supabase.table("products").select("category").execute()
            
            # Extract unique categories
            categories = list(set(product["category"] for product in result.data))
            return sorted(categories)
            
        except Exception as e:
            logger.error(f"Error fetching categories: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to fetch categories"
            )
    
    @staticmethod
    async def create_product(product_data: ProductCreate) -> ProductResponse:
        """Create a new product (admin only)"""
        try:
            # Convert to dict and add fields
            product_dict = product_data.dict()
            
            result = supabase.table("products").insert(product_dict).execute()
            
            if not result.data:
                raise HTTPException(
                    status_code=status.HTTP_400_BAD_REQUEST,
                    detail="Failed to create product"
                )
            
            return ProductResponse(**result.data[0])
            
        except HTTPException:
            raise
        except Exception as e:
            logger.error(f"Error creating product: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to create product"
            )
    
    @staticmethod
    async def seed_products():
        """Seed database with initial products"""
        try:
            # Check if products already exist
            existing = supabase.table("products").select("id").limit(1).execute()
            if existing.data:
                logger.info("Products already seeded")
                return
            
            # Seed data
            products = [
                {
                    "id": "prod1",
                    "name": "Caffe Mocha",
                    "price": 374.00,
                    "description": "A cappuccino is approximately 150 ml (5 oz) beverage, with 25 ml of espresso coffee and 85ml steamed milk topped with milk foam.",
                    "image_url": "https://images.unsplash.com/photo-1579992357154-faf4bde95b3d?w=500&h=500&fit=crop&auto=format",
                    "category": "Coffee",
                    "rating": 4.8,
                    "review_count": 230,
                    "calories": 350,
                },
                {
                    "id": "prod2",
                    "name": "Caffe Latte",
                    "price": 332.00,
                    "description": "Smooth and creamy latte with rich espresso and steamed milk.",
                    "image_url": "https://images.unsplash.com/photo-1570968915860-54d5c301fa9f?w=500&h=500&fit=crop&auto=format",
                    "category": "Coffee",
                    "rating": 4.6,
                    "review_count": 180,
                    "calories": 290,
                },
                {
                    "id": "prod3",
                    "name": "Cappuccino",
                    "price": 311.25,
                    "description": "Classic cappuccino with perfect balance of espresso, steamed milk and foam.",
                    "image_url": "https://images.unsplash.com/photo-1541167760496-1628856ab772?w=500&h=500&fit=crop&auto=format",
                    "category": "Coffee",
                    "rating": 4.7,
                    "review_count": 156,
                    "calories": 320,
                },
                {
                    "id": "prod4",
                    "name": "Green Tea",
                    "price": 207.50,
                    "description": "Fresh and refreshing green tea with natural antioxidants.",
                    "image_url": "https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=500&h=500&fit=crop&auto=format",
                    "category": "Tea",
                    "rating": 4.3,
                    "review_count": 95,
                    "calories": 5,
                },
                {
                    "id": "prod5",
                    "name": "Earl Grey",
                    "price": 228.25,
                    "description": "Classic Earl Grey tea with bergamot oil flavor.",
                    "image_url": "https://images.unsplash.com/photo-1597318374138-9c91c8b2b3f5?w=500&h=500&fit=crop&auto=format",
                    "category": "Tea",
                    "rating": 4.5,
                    "review_count": 128,
                    "calories": 8,
                },
                {
                    "id": "prod6",
                    "name": "Americano",
                    "price": 249.75,
                    "description": "Rich and bold coffee with hot water added to espresso.",
                    "image_url": "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=500&h=500&fit=crop&auto=format",
                    "category": "Coffee",
                    "rating": 4.4,
                    "review_count": 112,
                    "calories": 15,
                },
                {
                    "id": "prod7",
                    "name": "Iced Coffee",
                    "price": 289.50,
                    "description": "Refreshing cold coffee perfect for hot days.",
                    "image_url": "https://images.unsplash.com/photo-1517556582217-7d7c2b2e7db4?w=500&h=500&fit=crop&auto=format",
                    "category": "Coffee",
                    "rating": 4.2,
                    "review_count": 87,
                    "calories": 160,
                },
                {
                    "id": "prod8",
                    "name": "Chai Latte",
                    "price": 342.00,
                    "description": "Spiced tea latte with aromatic blend of cinnamon, cardamom, and ginger.",
                    "image_url": "https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=500&h=500&fit=crop&auto=format",
                    "category": "Tea",
                    "rating": 4.6,
                    "review_count": 203,
                    "calories": 240,
                },
            ]
            
            result = supabase.table("products").insert(products).execute()
            logger.info(f"Seeded {len(result.data)} products")
            
        except Exception as e:
            logger.error(f"Error seeding products: {e}")
            # Don't raise exception for seeding failures