from fastapi import APIRouter, Query, HTTPException, Depends
from typing import List, Optional
from app.models.schemas import ProductResponse, ApiResponse
from app.services.product_service import ProductService
from app.routers.auth import get_current_user, UserResponse

router = APIRouter(prefix="/products", tags=["Products"])


@router.get("/categories", response_model=ApiResponse)
async def get_categories():
    """Get all product categories"""
    categories = await ProductService.get_categories()
    return ApiResponse(
        success=True, message="Categories retrieved successfully", data=categories
    )


@router.get("/search", response_model=ApiResponse)
async def search_products(q: str = Query(..., description="Search query")):
    """Search products by name or description"""
    products = await ProductService.search_products(q)
    return ApiResponse(
        success=True, message="Search completed successfully", data=products
    )


@router.get("/category/{category}", response_model=ApiResponse)
async def get_products_by_category(category: str):
    """Get products by category"""
    products = await ProductService.get_products_by_category(category)
    return ApiResponse(
        success=True, message="Products retrieved successfully", data=products
    )


@router.get("/{product_id}", response_model=ApiResponse)
async def get_product(product_id: str):
    """Get a specific product by ID"""
    product = await ProductService.get_product_by_id(product_id)
    return ApiResponse(
        success=True, message="Product retrieved successfully", data=product
    )


@router.get("", response_model=ApiResponse)
async def get_products(category: Optional[str] = Query(None)):
    """Get all products, optionally filtered by category"""
    products = await ProductService.get_all_products(category)
    return ApiResponse(
        success=True, message="Products retrieved successfully", data=products
    )
