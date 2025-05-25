from fastapi import FastAPI, Form, Body, Path, Query, HTTPException, status
from typing import List, Dict, Any, Optional
from pydantic import BaseModel
import uuid
from datetime import datetime

app = FastAPI()


# Pydantic Models
class UserResponse(BaseModel):
    id: str
    name: str
    email: str
    phone: str = ""
    gender: str = ""
    profile_image_url: str = ""
    date_of_birth: str = ""


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
    imageUrl: str
    calories: Optional[int] = None
    rating: Optional[float] = None
    reviewCount: Optional[int] = None


class OrderResponse(BaseModel):
    id: str
    userId: str
    items: List[Dict[str, Any]]
    total: float
    status: str
    orderDate: str


class CartItemAdd(BaseModel):
    productId: str
    quantity: int


class CartItemUpdate(BaseModel):
    quantity: int


class ApiResponse(BaseModel):
    success: bool = True
    message: str = ""
    data: Any = None


# Mock data for development
MOCK_PRODUCTS = [
    {
        "id": "prod1",
        "name": "Caffe Mocha",
        "price": 374.00,
        "description": "A cappuccino is approximately 150 ml (5 oz) beverage, with 25 ml of espresso coffee and 85ml steamed milk topped with milk foam.",
        "imageUrl": "https://images.unsplash.com/photo-1579992357154-faf4bde95b3d?w=500&h=500&fit=crop&auto=format",
        "category": "Coffee",
        "rating": 4.8,
        "reviewCount": 230,
        "calories": 350,
    },
    {
        "id": "prod2",
        "name": "Caffe Latte",
        "price": 332.00,
        "description": "Smooth and creamy latte with rich espresso and steamed milk.",
        "imageUrl": "https://images.unsplash.com/photo-1570968915860-54d5c301fa9f?w=500&h=500&fit=crop&auto=format",
        "category": "Coffee",
        "rating": 4.6,
        "reviewCount": 180,
        "calories": 290,
    },
    {
        "id": "prod3",
        "name": "Cappuccino",
        "price": 311.25,
        "description": "Classic cappuccino with perfect balance of espresso, steamed milk and foam.",
        "imageUrl": "https://images.unsplash.com/photo-1541167760496-1628856ab772?w=500&h=500&fit=crop&auto=format",
        "category": "Coffee",
        "rating": 4.7,
        "reviewCount": 156,
        "calories": 320,
    },
    {
        "id": "prod4",
        "name": "Green Tea",
        "price": 207.50,
        "description": "Fresh and refreshing green tea with natural antioxidants.",
        "imageUrl": "https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=500&h=500&fit=crop&auto=format",
        "category": "Tea",
        "rating": 4.3,
        "reviewCount": 95,
        "calories": 5,
    },
    {
        "id": "prod5",
        "name": "Earl Grey",
        "price": 228.25,
        "description": "Classic Earl Grey tea with bergamot oil flavor.",
        "imageUrl": "https://images.unsplash.com/photo-1597318374138-9c91c8b2b3f5?w=500&h=500&fit=crop&auto=format",
        "category": "Tea",
        "rating": 4.5,
        "reviewCount": 128,
        "calories": 8,
    },
    {
        "id": "prod6",
        "name": "Americano",
        "price": 249.75,
        "description": "Rich and bold coffee with hot water added to espresso.",
        "imageUrl": "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=500&h=500&fit=crop&auto=format",
        "category": "Coffee",
        "rating": 4.4,
        "reviewCount": 112,
        "calories": 15,
    },
    {
        "id": "prod7",
        "name": "Iced Coffee",
        "price": 289.50,
        "description": "Refreshing cold coffee perfect for hot days.",
        "imageUrl": "https://images.unsplash.com/photo-1517556582217-7d7c2b2e7db4?w=500&h=500&fit=crop&auto=format",
        "category": "Coffee",
        "rating": 4.2,
        "reviewCount": 87,
        "calories": 160,
    },
    {
        "id": "prod8",
        "name": "Chai Latte",
        "price": 342.00,
        "description": "Spiced tea latte with aromatic blend of cinnamon, cardamom, and ginger.",
        "imageUrl": "https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=500&h=500&fit=crop&auto=format",
        "category": "Tea",
        "rating": 4.6,
        "reviewCount": 203,
        "calories": 240,
    },
]

MOCK_CATEGORIES = ["Coffee", "Tea", "Pastries", "Sandwiches"]

MOCK_USERS = {}
MOCK_ORDERS = {}
MOCK_CART = {}
MOCK_FAVORITES = {}


# Product endpoints
@app.get("/products")
def get_all_products(category: str = Query(None)):
    products = MOCK_PRODUCTS
    if category:
        products = [p for p in MOCK_PRODUCTS if p["category"] == category]

    return ApiResponse(success=True, data=products)


@app.get("/products/categories")
def get_categories():
    return ApiResponse(success=True, data=MOCK_CATEGORIES)


@app.get("/products/search")
def search_products(q: str = Query(...)):
    query = q.lower()
    results = [
        p
        for p in MOCK_PRODUCTS
        if query in p["name"].lower() or query in p["description"].lower()
    ]
    return ApiResponse(success=True, data=results)


@app.get("/products/category/{category}")
def get_products_by_category(category: str):
    products = [p for p in MOCK_PRODUCTS if p["category"] == category]
    return ApiResponse(success=True, data=products)


@app.get("/products/{productId}")
def get_product_by_id(productId: str):
    for product in MOCK_PRODUCTS:
        if product["id"] == productId:
            return ApiResponse(success=True, data=ProductResponse(**product))

    raise HTTPException(status_code=404, detail="Product not found")


# User authentication endpoints
@app.post("/users/register")
def register_user(
    name: str = Form(...),
    email: str = Form(...),
    password: str = Form(...),
):
    if email in MOCK_USERS:
        raise HTTPException(status_code=400, detail="User already exists")

    user_id = str(uuid.uuid4())
    user_data = {
        "id": user_id,
        "name": name,
        "email": email,
        "phone": "",
        "gender": "",
        "profile_image_url": "",
        "date_of_birth": "",
    }
    MOCK_USERS[email] = user_data

    return ApiResponse(success=True, data=UserResponse(**user_data))


@app.post("/users/login")
def login_user(
    email: str = Form(...),
    password: str = Form(...),
):
    if email not in MOCK_USERS:
        raise HTTPException(status_code=401, detail="Invalid credentials")

    user_data = MOCK_USERS[email]
    return ApiResponse(success=True, data=UserResponse(**user_data))


@app.post("/users/forgot-password")
def forgot_password(email: str = Form(...)):
    if email not in MOCK_USERS:
        raise HTTPException(status_code=404, detail="User not found")

    return ApiResponse(success=True, message="Password reset email sent")


# User profile endpoints
@app.get("/users/profile")
def get_user_profile():
    # In a real app, this would get user from JWT token
    # For now, return first user if exists
    if MOCK_USERS:
        user_data = list(MOCK_USERS.values())[0]
        return ApiResponse(success=True, data=UserResponse(**user_data))

    raise HTTPException(status_code=404, detail="User not found")


@app.put("/users/profile")
def update_user_profile(user_update: UserUpdate):
    # In a real app, this would get user from JWT token
    # For now, update first user if exists
    if MOCK_USERS:
        email = list(MOCK_USERS.keys())[0]
        user_data = MOCK_USERS[email]
        update_data = user_update.dict(exclude_unset=True)
        user_data.update(update_data)
        return ApiResponse(success=True, data=UserResponse(**user_data))

    raise HTTPException(status_code=404, detail="User not found")


# Cart endpoints
@app.get("/cart")
def get_cart():
    # In a real app, this would get user from JWT token
    user_id = "user1"  # Mock user ID
    user_cart = MOCK_CART.get(user_id, [])
    return ApiResponse(success=True, data=user_cart)


@app.post("/cart/add")
def add_to_cart(cart_item: CartItemAdd):
    user_id = "user1"  # Mock user ID
    if user_id not in MOCK_CART:
        MOCK_CART[user_id] = []

    cart_item_data = {
        "id": str(uuid.uuid4()),
        "productId": cart_item.productId,
        "quantity": cart_item.quantity,
    }
    MOCK_CART[user_id].append(cart_item_data)

    return ApiResponse(success=True, data=cart_item_data)


@app.put("/cart/update")
def update_cart_item(cart_item: Dict[str, Any]):
    user_id = "user1"  # Mock user ID
    if user_id not in MOCK_CART:
        raise HTTPException(status_code=404, detail="Cart not found")

    item_id = cart_item.get("id")
    for item in MOCK_CART[user_id]:
        if item["id"] == item_id:
            item["quantity"] = cart_item.get("quantity", item["quantity"])
            return ApiResponse(success=True, data=item)

    raise HTTPException(status_code=404, detail="Cart item not found")


@app.delete("/cart/{itemId}")
def remove_from_cart(itemId: str):
    user_id = "user1"  # Mock user ID
    if user_id not in MOCK_CART:
        raise HTTPException(status_code=404, detail="Cart not found")

    MOCK_CART[user_id] = [item for item in MOCK_CART[user_id] if item["id"] != itemId]
    return ApiResponse(success=True, message="Item removed from cart")


@app.delete("/cart/clear")
def clear_cart():
    user_id = "user1"  # Mock user ID
    MOCK_CART[user_id] = []
    return ApiResponse(success=True, message="Cart cleared successfully")


# Order endpoints
@app.get("/orders")
def get_orders():
    user_id = "user1"  # Mock user ID
    user_orders = [
        order for order in MOCK_ORDERS.values() if order["userId"] == user_id
    ]
    return ApiResponse(
        success=True, data=[OrderResponse(**order) for order in user_orders]
    )


@app.get("/orders/{orderId}")
def get_order_by_id(orderId: str):
    if orderId not in MOCK_ORDERS:
        raise HTTPException(status_code=404, detail="Order not found")

    order = MOCK_ORDERS[orderId]
    return ApiResponse(success=True, data=OrderResponse(**order))


@app.post("/orders")
def create_order(order_data: Dict[str, Any]):
    order_id = str(uuid.uuid4())
    order = {
        "id": order_id,
        "userId": order_data.get("userId", "user1"),
        "items": order_data.get("items", []),
        "total": order_data.get("total", 0.0),
        "status": "pending",
        "orderDate": datetime.now().isoformat(),
    }
    MOCK_ORDERS[order_id] = order

    return ApiResponse(success=True, data=OrderResponse(**order))


@app.put("/orders/{orderId}/status")
def update_order_status(orderId: str, status: str = Form(...)):
    if orderId not in MOCK_ORDERS:
        raise HTTPException(status_code=404, detail="Order not found")

    MOCK_ORDERS[orderId]["status"] = status
    order = MOCK_ORDERS[orderId]
    return ApiResponse(success=True, data=OrderResponse(**order))


# Favorites endpoints
@app.get("/favorites")
def get_favorites():
    user_id = "user1"  # Mock user ID
    user_favorites = MOCK_FAVORITES.get(user_id, [])
    favorite_products = [p for p in MOCK_PRODUCTS if p["id"] in user_favorites]
    return ApiResponse(success=True, data=favorite_products)


@app.post("/favorites/{productId}")
def add_to_favorites(productId: str):
    user_id = "user1"  # Mock user ID
    if user_id not in MOCK_FAVORITES:
        MOCK_FAVORITES[user_id] = []

    if productId not in MOCK_FAVORITES[user_id]:
        MOCK_FAVORITES[user_id].append(productId)

    return ApiResponse(success=True, message="Product added to favorites")


@app.delete("/favorites/{productId}")
def remove_from_favorites(productId: str):
    user_id = "user1"  # Mock user ID
    if user_id in MOCK_FAVORITES:
        MOCK_FAVORITES[user_id] = [
            pid for pid in MOCK_FAVORITES[user_id] if pid != productId
        ]

    return ApiResponse(success=True, message="Product removed from favorites")


# Notifications endpoints
@app.get("/notifications")
def get_notifications():
    mock_notifications = [
        "Your order #12345 has been confirmed",
        "New offer: 20% off on all beverages",
        "Your loyalty points have been updated",
    ]
    return ApiResponse(success=True, data=mock_notifications)


@app.post("/notifications/mark-read/{notificationId}")
def mark_notification_as_read(notificationId: str):
    return ApiResponse(success=True, message="Notification marked as read")


if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host="0.0.0.0", port=8000)
