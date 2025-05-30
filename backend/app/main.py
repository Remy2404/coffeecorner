from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.core.config import settings
from app.routers import auth, products, cart, orders, favorites, debug, user
from app.services.product_service import ProductService
import logging
import uvicorn
import os

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Create FastAPI app
app = FastAPI(
    title=settings.app_name, version=settings.app_version, debug=settings.debug
)

# Add CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Include routers
app.include_router(auth.router)
app.include_router(products.router)
app.include_router(cart.router)
app.include_router(orders.router)
app.include_router(favorites.router)
app.include_router(debug.router)
app.include_router(user.router)


@app.on_event("startup")
async def startup_event():
    """Initialize application on startup"""
    logger.info("Starting Coffee Shop API")
    logger.info(f"Environment: {'Development' if settings.debug else 'Production'}")
    logger.info(f"API Version: {settings.app_version}")

    # Seed products if needed
    try:
        await ProductService.seed_products()
        logger.info("Product seeding completed")
    except Exception as e:
        logger.error(f"Product seeding failed: {e}")
        # Don't fail startup if product seeding fails
        logger.warning("Continuing startup despite product seeding failure")


@app.get("/")
async def root():
    """Root endpoint"""
    return {
        "message": "Coffee Shop API",
        "version": settings.app_version,
        "docs": "/docs",
    }


@app.head("/health")
async def health_check():
    """Health check endpoint"""
    return {"status": "healthy", "message": "API is running"}


@app.get("/healthz")
async def health_check_z():
    """Health check endpoint"""
    return {"status": "healthy", "message": "API is running"}


if __name__ == "__main__":
    port = int(os.environ.get("PORT", 8000))
    host = "0.0.0.0"

    uvicorn.run("app.main:app", host=host, port=port, reload=settings.debug)
