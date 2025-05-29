package com.coffeecorner.app.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.coffeecorner.app.models.Product;
import com.coffeecorner.app.network.ApiResponse;
import com.coffeecorner.app.network.ApiService;
import com.coffeecorner.app.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ProductRepository - Single source of truth for product data
 * Manages product listing, details, filtering, and search
 */
public class ProductRepository {

    private static volatile ProductRepository instance;
    private final ApiService apiService; // Added ApiService

    private ProductRepository() {
        apiService = RetrofitClient.getApiService(); // Initialize ApiService
    }

    public static ProductRepository getInstance() {
        if (instance == null) {
            synchronized (ProductRepository.class) {
                if (instance == null) {
                    instance = new ProductRepository();
                }
            }
        }
        return instance;
    }

    /**
     * Get all products
     *
     * @param callback Callback to handle result
     */
    public void getProducts(@NonNull ProductsCallback callback) {
        apiService.getProducts().enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Product>>> call,
                    @NonNull Response<ApiResponse<List<Product>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Product> products = response.body().getData();
                    Log.d("ProductRepository", "Successfully loaded " + products.size() + " products");

                    // Enhanced debug logging to verify image URLs
                    int nonNullImageCount = 0;
                    for (Product product : products) {
                        if (product.getImageUrl() != null) {
                            nonNullImageCount++;
                        }
                    }
                    Log.d("ProductRepository",
                            "Products with non-null imageUrl: " + nonNullImageCount + " out of " + products.size());

                    // Debug first few products to check image URLs
                    for (int i = 0; i < Math.min(products.size(), 5); i++) {
                        Product product = products.get(i);
                        Log.d("ProductRepository", "Product[" + i + "]: " + product.getName() +
                                ", ImageURL: " + product.getImageUrl());
                    }

                    callback.onProductsLoaded(products);
                } else {
                    String errorMsg = "Failed to load products.";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMsg = response.body().getMessage();
                    }
                    Log.e("ProductRepository", "Get products failed: " + response.code() + " - " + errorMsg);
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Product>>> call, @NonNull Throwable t) {
                Log.e("ProductRepository", "Get products network error", t);
                callback.onError("Network error. Please try again. " + t.getMessage());
            }
        });
    }

    /**
     * Get products filtered by category
     *
     * @param category Category to filter by
     * @param callback Callback to handle result
     */
    public void getProductsByCategory(String category, @NonNull ProductsCallback callback) {
        if (category == null || category.isEmpty() || "All".equalsIgnoreCase(category)) {
            getProducts(callback); // Load all products if category is null, empty or "All"
            return;
        }

        apiService.getProductsByCategory(category).enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Product>>> call,
                    @NonNull Response<ApiResponse<List<Product>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onProductsLoaded(response.body().getData());
                } else {
                    String errorMsg = "Failed to load products for category: " + category;
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMsg = response.body().getMessage();
                    }
                    Log.e("ProductRepository",
                            "Get products by category failed: " + response.code() + " - " + errorMsg);
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Product>>> call, @NonNull Throwable t) {
                Log.e("ProductRepository", "Get products by category network error", t);
                callback.onError("Network error. Please try again. " + t.getMessage());
            }
        });
    }

    /**
     * Get product details by ID
     *
     * @param productId Product ID to fetch
     * @param callback  Callback to handle result
     */
    public void getProductDetails(String productId, @NonNull ProductDetailCallback callback) {
        if (productId == null || productId.isEmpty()) {
            callback.onProductError("Invalid product ID");
            return;
        }
        apiService.getProductById(productId).enqueue(new Callback<ApiResponse<Product>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Product>> call,
                    @NonNull Response<ApiResponse<Product>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onProductLoaded(response.body().getData());
                } else {
                    String errorMsg = "Failed to load product details.";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMsg = response.body().getMessage();
                    }
                    Log.e("ProductRepository", "Get product details failed: " + response.code() + " - " + errorMsg);
                    callback.onProductError(errorMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Product>> call, @NonNull Throwable t) {
                Log.e("ProductRepository", "Get product details network error", t);
                callback.onProductError("Network error. Please try again. " + t.getMessage());
            }
        });
    }

    /**
     * Search products by query
     *
     * @param query    Search query
     * @param callback Callback to handle result
     */
    public void searchProducts(String query, @NonNull ProductsCallback callback) {
        if (query == null || query.isEmpty()) {
            getProducts(callback); // Load all products if query is null or empty
            return;
        }

        apiService.searchProducts(query).enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Product>>> call,
                    @NonNull Response<ApiResponse<List<Product>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onProductsLoaded(response.body().getData());
                } else {
                    String errorMsg = "Failed to search products for query: " + query;
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMsg = response.body().getMessage();
                    }
                    Log.e("ProductRepository", "Search products failed: " + response.code() + " - " + errorMsg);
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Product>>> call, @NonNull Throwable t) {
                Log.e("ProductRepository", "Search products network error", t);
                callback.onError("Network error. Please try again. " + t.getMessage());
            }
        });
    }

    /**
     * Get a product by ID
     *
     * @param productId Product ID
     * @param callback  Callback to handle result
     */
    public void getProductById(String productId, @NonNull ProductDetailCallback callback) {
        if (productId == null || productId.isEmpty()) {
            callback.onProductError("Product ID cannot be null or empty.");
            return;
        }
        apiService.getProductDetails(productId).enqueue(new Callback<ApiResponse<Product>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Product>> call,
                    @NonNull Response<ApiResponse<Product>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onProductLoaded(response.body().getData());
                } else {
                    String errorMsg = "Failed to load product details for ID: " + productId;
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMsg = response.body().getMessage();
                    }
                    Log.e("ProductRepository", "Get product by ID failed: " + response.code() + " - " + errorMsg);
                    callback.onProductError(errorMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Product>> call, @NonNull Throwable t) {
                Log.e("ProductRepository", "Get product by ID network error", t);
                callback.onProductError("Network error. Please try again. " + t.getMessage());
            }
        });
    }

    /**
     * Get all product categories
     *
     * @param callback Callback to handle result
     */
    public void getCategories(@NonNull CategoriesCallback callback) {
        apiService.getCategories().enqueue(new Callback<ApiResponse<List<String>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<String>>> call,
                    @NonNull Response<ApiResponse<List<String>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<String> categoriesWithAll = new ArrayList<>();
                    categoriesWithAll.add("All"); // Add "All" category at the beginning
                    categoriesWithAll.addAll(response.body().getData());
                    callback.onCategoriesLoaded(categoriesWithAll);
                } else {
                    String errorMsg = "Failed to load categories.";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMsg = response.body().getMessage();
                    }
                    Log.e("ProductRepository", "Get categories failed: " + response.code() + " - " + errorMsg);
                    // Provide a default list with "All" if API call fails
                    List<String> defaultCategories = new ArrayList<>();
                    defaultCategories.add("All");
                    callback.onCategoriesLoaded(defaultCategories);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<String>>> call, @NonNull Throwable t) {
                Log.e("ProductRepository", "Get categories network error", t);
                // Provide a default list with "All" if API call fails
                List<String> defaultCategories = new ArrayList<>();
                defaultCategories.add("All");
                callback.onCategoriesLoaded(defaultCategories);
            }
        });
    }

    /**
     * Interface for products callback
     */
    public interface ProductsCallback {
        void onProductsLoaded(List<Product> products);

        void onError(String errorMessage);
    }

    /**
     * Interface for single product callback
     */
    public interface ProductCallback {
        void onProductLoaded(Product product);

        void onError(String errorMessage);
    }

    /**
     * Interface for categories callback
     */
    public interface CategoriesCallback {
        void onCategoriesLoaded(List<String> categories);

        void onError(String errorMessage);
    }

    /**
     * Interface for product detail callback
     */
    public interface ProductDetailCallback {
        void onProductLoaded(Product product);

        void onProductError(String errorMessage);
    }
}
