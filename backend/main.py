from fastapi import FastAPI, Form, Body, Path, Query, HTTPException, status
from typing import List, Dict, Any
from pydantic import BaseModel

# Mock data for development
MOCK_PRODUCTS = [
    {
        "id": "prod1",
        "name": "Caffe Mocha",
        "price": 374.00,
        "description": "A cappuccino is approximately 150 ml (5 oz) beverage, with 25 ml of espresso coffee and 85ml steamed milk topped with milk foam.",
        "imageUrl": "https://images.unsplash.com/photo-1517701550928-30aac5df30c8?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8Y2FmZSUyMG1vY2hhfGVufDB8fDB8fHww&auto=format&fit=crop&w=500&q=60",
        "category": "Coffee",
        "rating": 4.8,
        "reviewCount": 230,
    },
    {
        "id": "prod2",
        "name": "Caffe Latte",
        "price": 332.00,
        "description": "Smooth and creamy latte with rich espresso and steamed milk.",
        "imageUrl": "https://images.unsplash.com/photo-1517701550928-30aac5df30c8?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8Y2FmZSUyMG1vY2hhfGVufDB8fDB8fHww&auto=format&fit=crop&w=500&q=60",
        "category": "Coffee",
        "rating": 4.6,
        "reviewCount": 180,
    },
    {
        "id": "prod3",
        "name": "Cappuccino",
        "price": 311.25,
        "description": "Classic cappuccino with perfect balance of espresso, steamed milk and foam.",
        "imageUrl": "https://images.unsplash.com/photo-1551033406-611cf9a28f67?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8Y2FwcHVjY2lub3xlbnwwfHwwfHx8MA%3D%3D&auto=format&fit=crop&w=500&q=60",
        "category": "Coffee",
        "rating": 4.7,
        "reviewCount": 156,
    },
    {
        "id": "prod4",
        "name": "Green Tea",
        "price": 207.50,
        "description": "Fresh and refreshing green tea with natural antioxidants.",
        "imageUrl": "https://images.unsplash.com/photo-1547981609-4b6bfe67ca0b?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8NHx8Z3JlZW4lMjB0ZWF8ZW58MHx8MHx8fDA%3D&auto=format&fit=crop&w=500&q=60",
        "category": "Tea",
        "rating": 4.3,
        "reviewCount": 95,
    },
    {
        "id": "prod5",
        "name": "Earl Grey",
        "price": 228.25,
        "description": "Classic Earl Grey tea with bergamot oil flavor.",
        "imageUrl": "https://images.unsplash.com/photo-1534171472159-edb6d1a81ec6?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8NXx8ZWFybCUyMGdyZXl8ZW58MHx8MHx8fDA%3D&auto=format&fit=crop&w=500&q=60",
        "category": "Tea",
        "rating": 4.5,
        "reviewCount": 120,
    },
    {
        "id": "prod6",
        "name": "Chocolate Croissant",
        "price": 269.75,
        "description": "Buttery, flaky croissant filled with rich chocolate.",
        "imageUrl": "https://images.unsplash.com/photo-1563805042-7684c019e1cb?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8Y2hvY29sYXRlJTIwY3JvaXNzYW50fGVufDB8fDB8fHww&auto=format&fit=crop&w=500&q=60",
        "category": "Pastries",
        "rating": 4.4,
        "reviewCount": 78,
    },
]

MOCK_CATEGORIES = ["Coffee", "Tea", "Pastries", "Sandwiches", "Desserts"]

MOCK_USERS = {}
MOCK_ORDERS = {}
MOCK_CART = {}


# Placeholder models for request bodies where needed
class UserUpdate(BaseModel):
    full_name: str | None = None
    email: str | None = None
    phone: str | None = None
    gender: str | None = None
    profile_image_url: str | None = None
    date_of_birth: str | None = None


class CartItemAdd(BaseModel):
    productId: str
    quantity: int


class CartItemUpdate(BaseModel):
    quantity: int


# Response models
class UserResponse(BaseModel):
    id: str | None = None
    name: str | None = None
    email: str | None = None


class ProductResponse(BaseModel):
    id: str | None = None
    name: str | None = None
    price: float | None = None
    description: str | None = None
    imageUrl: str | None = None
    category: str | None = None
    rating: float | None = None
    reviewCount: int | None = None


class OrderResponse(BaseModel):
    id: str | None = None
    user_id: str | None = None
    total: float | None = None
    status: str | None = None


class ApiResponse(BaseModel):
    success: bool = True
    message: str | None = None
    data: Any | None = None


app = FastAPI()

# --- User Endpoints ---


@app.post("/users/register")
async def register_user(
    name: str = Form(...),
    email: str = Form(...),
    password: str = Form(...),
    recaptcha_token: str = Form(""),
) -> ApiResponse:
    print(f"Received registration data:")
    print(f"Name: {name}")
    print(f"Email: {email}")
    print(f"Password: {password}")  # DO NOT log passwords in production
    print(f"reCAPTCHA Token: {recaptcha_token}")

    # TODO: Implement reCAPTCHA verification, password hashing, and database save

    # Placeholder response
    return ApiResponse(
        message="User registered (placeholder)",
        data=UserResponse(id="placeholder_id", name=name, email=email),
    )


@app.post("/users/login")
async def login_user(
    email: str = Form(...),
    password: str = Form(...),
) -> ApiResponse:
    print(f"Received login data:")
    print(f"Email: {email}")
    print(f"Password: {password}")  # DO NOT log passwords in production

    # TODO: Implement user authentication and JWT generation

    # Placeholder response (assuming success returns user data)
    return ApiResponse(
        message="Login successful (placeholder)",
        data=UserResponse(id="placeholder_id", name="Placeholder User", email=email),
    )


@app.get("/users/{userId}")
async def get_user_by_id(userId: str = Path(...)) -> ApiResponse:
    print(f"Received request for user ID: {userId}")

    # TODO: Implement fetching user from database by ID
    # If user not found, return 404:
    # raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="User not found")

    # Placeholder response
    return ApiResponse(
        message="User data (placeholder)",
        data=UserResponse(
            id=userId, name="Placeholder User", email="placeholder@example.com"
        ),
    )


@app.put("/users/{userId}")
async def update_user_profile(
    userId: str = Path(...), user_update: UserUpdate = Body(...)
) -> ApiResponse:
    print(
        f"Received update request for user ID: {userId} with data: {user_update.model_dump_json()}"
    )

    # TODO: Implement updating user in database

    # Placeholder response (assuming the updated user is returned)
    updated_user = UserResponse(
        id=userId, name=user_update.full_name, email=user_update.email
    )
    return ApiResponse(message="Profile updated (placeholder)", data=updated_user)


@app.post("/users/reset-password")
async def request_password_reset(email: str = Form(...)) -> ApiResponse:
    print(f"Received password reset request for email: {email}")

    # TODO: Implement password reset request logic (e.g., sending email)

    # Placeholder response
    return ApiResponse(
        message="Password reset request received (placeholder)", data=None
    )


@app.put("/users/{userId}/change-password")
async def change_password(
    userId: str = Path(...),
    old_password: str = Form(...),
    new_password: str = Form(...),
) -> ApiResponse:
    print(f"Received change password request for user ID: {userId}")
    print(f"Old Password: {old_password}")  # DO NOT log passwords
    print(f"New Password: {new_password}")  # DO NOT log passwords

    # TODO: Implement password change logic

    # Placeholder response
    return ApiResponse(message="Password changed (placeholder)", data=None)


# --- Product Endpoints ---


@app.get("/products")
async def get_products(
    category: str | None = Query(None, description="Filter by category")
) -> ApiResponse:
    print(f"Received get products request. Category filter: {category}")

    # Use MOCK_PRODUCTS instead of placeholder data
    products = MOCK_PRODUCTS
    if category:
        products = [
            p for p in MOCK_PRODUCTS if p["category"].lower() == category.lower()
        ]

    return ApiResponse(message="Product list retrieved successfully", data=products)


@app.get("/products/categories")
async def get_categories() -> ApiResponse:
    print("Received get categories request.")

    # Use MOCK_CATEGORIES instead of placeholder
    return ApiResponse(
        message="Product categories fetched successfully",
        data=MOCK_CATEGORIES,
    )


@app.get("/products/search")
async def search_products(
    q: str = Query(..., description="Search query")
) -> ApiResponse:
    print(f"Received search products request. Query: {q}")

    # Search in MOCK_PRODUCTS
    results = [
        product
        for product in MOCK_PRODUCTS
        if q.lower() in product["name"].lower()
        or q.lower() in product["description"].lower()
        or q.lower() in product["category"].lower()
    ]

    return ApiResponse(message="Search results retrieved successfully", data=results)


@app.get("/products/{productId}")
async def get_product_by_id(productId: str = Path(...)) -> ApiResponse:
    print(f"Received get product by ID request. Product ID: {productId}")

    # Find product in MOCK_PRODUCTS
    product = next((p for p in MOCK_PRODUCTS if p["id"] == productId), None)

    if not product:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="Product not found"
        )

    return ApiResponse(
        message="Product details retrieved successfully",
        data=product,
    )


# --- Order Endpoints ---


@app.post("/orders")
async def create_order(order_data: Dict[str, Any] = Body(...)) -> ApiResponse:
    print(f"Received create order request with data: {order_data}")

    # TODO: Implement order creation in database

    # Placeholder response
    return ApiResponse(
        message="Order created (placeholder)",
        data=OrderResponse(
            id="order_placeholder_id",
            user_id=order_data.get("userId"),
            total=order_data.get("total"),
        ),
    )


@app.get("/orders")
async def get_user_orders(
    userId: str = Query(..., description="User ID")
) -> ApiResponse:
    print(f"Received get user orders request for user ID: {userId}")

    # TODO: Implement fetching orders for a user from database

    # Placeholder response
    placeholder_orders = [
        {"id": "order1", "user_id": userId, "total": 15.0, "status": "completed"},
        {"id": "order2", "user_id": userId, "total": 22.5, "status": "pending"},
    ]
    return ApiResponse(message="User orders (placeholder)", data=placeholder_orders)


@app.get("/orders/{orderId}")
async def get_order_by_id(orderId: str = Path(...)) -> ApiResponse:
    print(f"Received get order by ID request. Order ID: {orderId}")

    # TODO: Implement fetching order from database by ID
    # If not found: raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Order not found")

    # Placeholder response
    return ApiResponse(
        message="Order details (placeholder)",
        data=OrderResponse(
            id=orderId, user_id="placeholder_user_id", total=30.0, status="confirmed"
        ),
    )


@app.put("/orders/{orderId}/cancel")
async def cancel_order(orderId: str = Path(...)) -> ApiResponse:
    print(f"Received cancel order request for Order ID: {orderId}")

    # TODO: Implement order cancellation logic in database

    # Placeholder response (assuming Void response on success, but APIResponse<Void> wrapper)
    return ApiResponse(message="Order cancelled (placeholder)", data=None)


# --- Cart Endpoints ---


@app.get("/cart/{userId}")
async def get_user_cart(userId: str = Path(...)) -> ApiResponse:
    print(f"Received get user cart request for User ID: {userId}")

    # TODO: Implement fetching user's cart from database

    # Placeholder response
    placeholder_cart_items = [
        {"productId": "prod1", "quantity": 2, "name": "Coffee A"},
    ]
    return ApiResponse(message="User cart (placeholder)", data=placeholder_cart_items)


@app.post("/cart/{userId}/items")
async def add_to_cart(
    userId: str = Path(...),
    product_id: str = Form(..., alias="productId"),
    quantity: int = Form(...),
) -> ApiResponse:
    print(f"Received add to cart request for User ID: {userId}")
    print(f"Product ID: {product_id}, Quantity: {quantity}")

    # TODO: Implement adding item to user's cart in database

    # Placeholder response (assuming updated cart is returned)
    placeholder_cart_items = [
        {"productId": product_id, "quantity": quantity, "name": "Added Item"},
    ]
    return ApiResponse(
        message="Item added to cart (placeholder)", data=placeholder_cart_items
    )


@app.put("/cart/{userId}/items/{itemId}")
async def update_cart_item(
    userId: str = Path(...),
    itemId: str = Path(...),
    quantity: int = Form(...),
) -> ApiResponse:
    print(f"Received update cart item request for User ID: {userId}, Item ID: {itemId}")
    print(f"New Quantity: {quantity}")

    # TODO: Implement updating item quantity in user's cart in database

    # Placeholder response (assuming updated cart is returned)
    placeholder_cart_items = [
        {"productId": "some_prod_id", "quantity": quantity, "name": "Updated Item"},
    ]
    return ApiResponse(
        message="Cart item updated (placeholder)", data=placeholder_cart_items
    )


@app.delete("/cart/{userId}/items/{itemId}")
async def remove_from_cart(
    userId: str = Path(...), itemId: str = Path(...)
) -> ApiResponse:
    print(f"Received remove from cart request for User ID: {userId}, Item ID: {itemId}")

    # TODO: Implement removing item from user's cart in database

    # Placeholder response (assuming updated cart is returned)
    placeholder_cart_items: List[Dict[str, Any]] = (
        []
    )  # Assuming removing leaves an empty cart for this example
    return ApiResponse(
        message="Item removed from cart (placeholder)", data=placeholder_cart_items
    )


@app.delete("/cart/{userId}")
async def clear_cart(userId: str = Path(...)) -> ApiResponse:
    print(f"Received clear cart request for User ID: {userId}")

    # TODO: Implement clearing user's cart in database

    # Placeholder response (assuming empty cart is returned)
    placeholder_cart_items: List[Dict[str, Any]] = []
    return ApiResponse(
        message="Cart cleared (placeholder)", data=placeholder_cart_items
    )
