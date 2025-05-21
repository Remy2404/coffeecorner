package com.coffeecorner.app.network;

import com.coffeecorner.app.models.CartItem;
import com.coffeecorner.app.models.Order;
import com.coffeecorner.app.models.Product;
import com.coffeecorner.app.models.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * ApiService - Interface defining all API endpoints
 * Used by Retrofit to create HTTP request implementations
 */
public interface ApiService {
        @GET("products/{id}")
        Call<ApiResponse<Product>> getProductDetails(@Path("id") String productId);

        @GET("orders/{id}")
        Call<ApiResponse<Order>> getOrderDetails(@Path("id") String orderId);

        // User API endpoints

        /**
         * Login with email and password
         * 
         * @param email    User email
         * @param password User password
         * @return User data response
         */
        @FormUrlEncoded
        @POST("users/login")
        Call<ApiResponse<User>> login(
                        @Field("email") String email,
                        @Field("password") String password);

        /**
         * Register new user
         * 
         * @param name     User name
         * @param email    User email
         * @param password User password
         * @return User data response
         */
        @FormUrlEncoded
        @POST("users/register")
        Call<ApiResponse<User>> register(
                        @Field("name") String name,
                        @Field("email") String email,
                        @Field("password") String password,
                        @Field("recaptcha_token") String recaptchaToken);

        /**
         * Get user profile by ID
         * 
         * @param userId User ID
         * @return User data response
         */
        @GET("users/{userId}")
        Call<ApiResponse<User>> getUserById(
                        @Path("userId") String userId);

        /**
         * Update user profile
         * 
         * @param userId User ID
         * @param user   Updated user data
         * @return User data response
         */
        @PUT("users/{userId}")
        Call<ApiResponse<User>> updateUser(
                        @Path("userId") String userId,
                        @Body User user);

        /**
         * Request password reset
         * 
         * @param email User email
         * @return API response
         */
        @FormUrlEncoded
        @POST("users/reset-password")
        Call<ApiResponse<Void>> requestPasswordReset(
                        @Field("email") String email);

        /**
         * Change user password
         * 
         * @param userId      User ID
         * @param oldPassword Current password
         * @param newPassword New password
         * @return API response
         */
        @FormUrlEncoded
        @PUT("users/{userId}/change-password")
        Call<ApiResponse<Void>> changePassword(
                        @Path("userId") String userId,
                        @Field("old_password") String oldPassword,
                        @Field("new_password") String newPassword);

        // Product API endpoints

        /**
         * Get all products
         * 
         * @return List of products
         */
        @GET("products")
        Call<ApiResponse<List<Product>>> getProducts();

        /**
         * Get products by category
         * 
         * @param category Category name
         * @return List of products
         */
        @GET("products")
        Call<ApiResponse<List<Product>>> getProductsByCategory(
                        @Query("category") String category);

        /**
         * Search products
         * 
         * @param query Search query
         * @return List of products
         */
        @GET("products/search")
        Call<ApiResponse<List<Product>>> searchProducts(
                        @Query("q") String query);

        /**
         * Get product by ID
         * 
         * @param productId Product ID
         * @return Product data response
         */
        @GET("products/{productId}")
        Call<ApiResponse<Product>> getProductById(
                        @Path("productId") String productId);

        /**
         * Get all product categories
         * 
         * @return List of categories
         */
        @GET("products/categories")
        Call<ApiResponse<List<String>>> getCategories();
        @POST("orders")
        Call<ApiResponse<Order>> createOrder(
                        @Body Map<String, Object> orderData);

        /**
         * Get orders for user
         * 
         * @param userId User ID
         * @return List of orders
         */
        @GET("orders")
        Call<ApiResponse<List<Order>>> getUserOrders(
                        @Query("userId") String userId);

        /**
         * Get order by ID
         * 
         * @param orderId Order ID
         * @return Order data response
         */
        @GET("orders/{orderId}")
        Call<ApiResponse<Order>> getOrderById(
                        @Path("orderId") String orderId);

        /**
         * Cancel an order
         *
         * @param orderId Order ID
         * @return API response
         */
        @PUT("orders/{orderId}/cancel")
        Call<ApiResponse<Void>> cancelOrder( // Assuming Void or a simple success message for cancel
                        @Path("orderId") String orderId);

        // Cart API endpoints

        /**
         * Get the user's current cart
         *
         * @param userId User ID
         * @return Cart data response
         */
        @GET("cart/{userId}")
        Call<ApiResponse<List<CartItem>>> getCart(@Path("userId") String userId);

        /**
         * Add an item to the cart
         *
         * @param userId    User ID
         * @param productId Product ID to add
         * @param quantity  Quantity to add
         * @return Updated cart data response
         */
        @FormUrlEncoded
        @POST("cart/{userId}/items")
        Call<ApiResponse<List<CartItem>>> addToCart(
                        @Path("userId") String userId,
                        @Field("productId") String productId,
                        @Field("quantity") int quantity);

        /**
         * Update quantity of an item in the cart
         *
         * @param userId   User ID
         * @param itemId   Cart Item ID (or Product ID if your API uses that to identify
         *                 items in cart)
         * @param quantity New quantity
         * @return Updated cart data response
         */
        @FormUrlEncoded
        @PUT("cart/{userId}/items/{itemId}")
        Call<ApiResponse<List<CartItem>>> updateCartItem(
                        @Path("userId") String userId,
                        @Path("itemId") String itemId, // This could be productId if items are identified by product ID
                                                       // in the cart
                        @Field("quantity") int quantity);

        /**
         * Remove an item from the cart
         *
         * @param userId User ID
         * @param itemId Cart Item ID (or Product ID)
         * @return Updated cart data response
         */
        @DELETE("cart/{userId}/items/{itemId}")
        Call<ApiResponse<List<CartItem>>> removeFromCart(
                        @Path("userId") String userId,
                        @Path("itemId") String itemId // This could be productId
        );

        /**
         * Clear all items from the user's cart
         *
         * @param userId User ID
         * @return API response (e.g., success message or empty cart)
         */
        @DELETE("cart/{userId}")
        Call<ApiResponse<Void>> clearCart(@Path("userId") String userId);

        Call<User> loginUser(User loginRequest);
}
