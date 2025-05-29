from pydantic import BaseModel, Field
from typing import List, Dict, Any, Optional
from datetime import datetime
from uuid import UUID


class UserCreate(BaseModel):
    name: str = Field(..., min_length=2, max_length=100)
    email: str = Field(..., pattern=r"^[^@]+@[^@]+\.[^@]+$")
    password: str = Field(..., min_length=6)


class UserLogin(BaseModel):
    email: str = Field(..., pattern=r"^[^@]+@[^@]+\.[^@]+$")
    password: str = Field(..., min_length=6)


class UserResponse(BaseModel):
    id: str
    name: Optional[str] = None
    email: str
    phone: Optional[str] = ""
    gender: Optional[str] = ""
    profile_image_url: Optional[str] = ""
    date_of_birth: Optional[str] = ""
    created_at: Optional[datetime] = None
    updated_at: Optional[datetime] = None


class UserUpdate(BaseModel):
    name: Optional[str] = None
    email: Optional[str] = None
    phone: Optional[str] = None
    gender: Optional[str] = None
    profile_image_url: Optional[str] = None
    date_of_birth: Optional[str] = None


class ProductResponse(BaseModel):
    id: str
    name: str
    description: str
    price: float
    category: str
    image_url: Optional[str] = None
    calories: Optional[int] = None
    rating: Optional[float] = None
    review_count: Optional[int] = None
    created_at: Optional[datetime] = None
    updated_at: Optional[datetime] = None


class ProductCreate(BaseModel):
    name: str = Field(..., min_length=2, max_length=200)
    description: str = Field(..., min_length=10, max_length=1000)
    price: float = Field(..., gt=0)
    category: str
    image_url: str
    calories: Optional[int] = None


class CartItemAdd(BaseModel):
    product_id: str
    quantity: int = Field(..., gt=0)


class CartItemUpdate(BaseModel):
    quantity: int = Field(..., gt=0)


class CartItemResponse(BaseModel):
    id: str
    user_id: str
    product_id: str
    quantity: int
    product: Optional[ProductResponse] = None
    created_at: Optional[datetime] = None
    updated_at: Optional[datetime] = None


class OrderCreate(BaseModel):
    items: List[Dict[str, Any]]
    total: float = Field(..., gt=0)
    delivery_address: Optional[str] = None
    notes: Optional[str] = None


class OrderResponse(BaseModel):
    id: str
    user_id: str
    items: List[Dict[str, Any]]
    total: float
    status: str
    delivery_address: Optional[str] = None
    notes: Optional[str] = None
    order_date: datetime
    created_at: Optional[datetime] = None
    updated_at: Optional[datetime] = None


class FavoriteResponse(BaseModel):
    id: str
    user_id: str
    product_id: str
    product: Optional[ProductResponse] = None
    created_at: Optional[datetime] = None


class NotificationResponse(BaseModel):
    id: str
    user_id: str
    title: str
    message: str
    type: str
    is_read: bool = False
    created_at: Optional[datetime] = None


class ApiResponse(BaseModel):
    success: bool = True
    message: str = ""
    data: Any = None


class AuthResponse(BaseModel):
    success: bool = True
    message: str = ""
    user: Optional[UserResponse] = None
    access_token: Optional[str] = None
    token_type: str = "bearer"
