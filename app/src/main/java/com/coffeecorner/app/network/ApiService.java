package com.coffeecorner.app.network;

import com.coffeecorner.app.models.CartItem;
import com.coffeecorner.app.models.Order;
import com.coffeecorner.app.models.Product;
import com.coffeecorner.app.models.User;
import com.coffeecorner.app.models.ProductResponse;

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

public interface ApiService {

  // Product endpoints
  @GET("products/{productId}")
  Call<ApiResponse<Product>> getProductById(@Path("productId") String productId);

  @GET("products/{productId}")
  Call<ApiResponse<Product>> getProductDetails(@Path("productId") String productId);

  @GET("products")
  Call<ApiResponse<List<Product>>> getProducts();

  @GET("products/categories")
  Call<ApiResponse<List<String>>> getCategories();

  @GET("products/search")
  Call<ApiResponse<List<Product>>> searchProducts(@Query("q") String query);

  @GET("products/category/{category}")
  Call<ApiResponse<List<Product>>> getProductsByCategory(@Path("category") String category);

  // User authentication endpoints
  @FormUrlEncoded
  @POST("auth/login")
  Call<ApiResponse<User>> login(
      @Field("email") String email,
      @Field("password") String password);

  @FormUrlEncoded
  @POST("auth/register")
  Call<ApiResponse<User>> register(
      @Field("name") String name,
      @Field("email") String email,
      @Field("password") String password);

  @FormUrlEncoded
  @POST("auth/forgot-password")
  Call<ApiResponse<String>> forgotPassword(@Field("email") String email);

  @FormUrlEncoded
  @POST("auth/forgot-password")
  Call<ApiResponse<Void>> requestPasswordReset(@Field("email") String email);

  @FormUrlEncoded
  @POST("auth/firebase-auth")
  Call<ApiResponse<User>> authenticateWithFirebase(@Field("firebase_token") String firebaseToken);

  // User profile endpoints
  @GET("users/profile/{userId}")
  Call<ApiResponse<User>> getUserProfile(@Path("userId") String userId);

  @GET("users/{userId}")
  Call<ApiResponse<User>> getUserById(@Path("userId") String userId);

  @PUT("users/profile")
  Call<ApiResponse<User>> updateUserProfile(@Body User user);

  @PUT("users/{userId}")
  Call<ApiResponse<User>> updateUser(@Path("userId") String userId, @Body User user);

  @FormUrlEncoded
  @POST("users/change-password")
  Call<ApiResponse<Void>> changePassword(
      @Field("userId") String userId,
      @Field("oldPassword") String oldPassword,
      @Field("newPassword") String newPassword);

  // Cart endpoints
  @GET("cart")
  Call<ApiResponse<List<CartItem>>> getCart();

  @GET("cart/{userId}")
  Call<ApiResponse<List<CartItem>>> getCart(@Path("userId") String userId);

  @POST("cart/add")
  Call<ApiResponse<List<CartItem>>> addToCart(@Body CartItem cartItem);
  @FormUrlEncoded
  @POST("cart/add")
  Call<ApiResponse<List<CartItem>>> addToCart(
      @Field("product_id") String productId,
      @Field("quantity") int quantity);

  @FormUrlEncoded
  @POST("cart/{userId}/add")
  Call<ApiResponse<List<CartItem>>> addToCart(
      @Path("userId") String userId,
      @Field("product_id") String productId,
      @Field("quantity") int quantity);

  @PUT("cart/update")
  Call<ApiResponse<List<CartItem>>> updateCartItem(@Body CartItem cartItem);

  @FormUrlEncoded
  @PUT("cart/{userId}/update/{itemId}")
  Call<ApiResponse<List<CartItem>>> updateCartItem(
      @Path("userId") String userId,
      @Path("itemId") String itemId,
      @Field("quantity") int quantity);

  @FormUrlEncoded
  @PUT("cart/{userId}/update-quantity/{itemId}")
  Call<ApiResponse<Void>> updateCartItemQuantity(
      @Path("userId") String userId,
      @Path("itemId") String itemId,
      @Field("quantity") int quantity);

  @DELETE("cart/{itemId}")
  Call<ApiResponse<List<CartItem>>> removeFromCart(@Path("itemId") String itemId);

  @DELETE("cart/{userId}/remove/{itemId}")
  Call<ApiResponse<List<CartItem>>> removeFromCart(
      @Path("userId") String userId,
      @Path("itemId") String itemId);

  @DELETE("cart/clear")
  Call<ApiResponse<Void>> clearCart();

  @DELETE("cart/{userId}/clear")
  Call<ApiResponse<Void>> clearCart(@Path("userId") String userId);

  // Order endpoints
  @GET("orders")
  Call<ApiResponse<List<Order>>> getOrders();

  @GET("orders/{userId}")
  Call<ApiResponse<List<Order>>> getUserOrders(@Path("userId") String userId);

  @GET("orders/history/{userId}")
  Call<ApiResponse<List<Order>>> getOrderHistory(@Path("userId") String userId);

  @GET("orders/{orderId}")
  Call<ApiResponse<Order>> getOrderById(@Path("orderId") String orderId);

  @GET("orders/{orderId}/track")
  Call<ApiResponse<String>> trackOrder(@Path("orderId") String orderId);

  @POST("orders")
  Call<ApiResponse<Order>> createOrder(@Body Map<String, Object> orderData);

  @PUT("orders/{orderId}/status")
  Call<ApiResponse<Order>> updateOrderStatus(
      @Path("orderId") String orderId,
      @Field("status") String status);

  @DELETE("orders/{orderId}/cancel")
  Call<ApiResponse<Void>> cancelOrder(@Path("orderId") String orderId);

  // Favorites endpoints
  @GET("favorites")
  Call<ApiResponse<List<Product>>> getFavorites();

  @POST("favorites/{productId}")
  Call<ApiResponse<String>> addToFavorites(@Path("productId") String productId);

  @DELETE("favorites/{productId}")
  Call<ApiResponse<String>> removeFromFavorites(@Path("productId") String productId);

  // Notifications endpoints
  @GET("notifications")
  Call<ApiResponse<List<String>>> getNotifications();

  @POST("notifications/mark-read/{notificationId}")
  Call<ApiResponse<String>> markNotificationAsRead(@Path("notificationId") String notificationId);
}
