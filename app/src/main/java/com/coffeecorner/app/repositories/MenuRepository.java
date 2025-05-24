package com.coffeecorner.app.repositories;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.coffeecorner.app.network.ApiResponse;
import com.coffeecorner.app.network.ApiService;
import com.coffeecorner.app.models.MenuItem;
import com.coffeecorner.app.models.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuRepository {

    private ApiService apiService;

    public LiveData<List<String>> getCategories() {
        MutableLiveData<List<String>> categoriesLiveData = new MutableLiveData<>();

        // Call getCategories with callback
        getCategories(new CategoriesCallback() {
            @Override
            public void onCategoriesLoaded(List<String> categories) {
                categoriesLiveData.postValue(categories);
            }

            @Override
            public void onCategoriesError(String errorMessage) {
                // Handle error, maybe post empty list or log error
                android.util.Log.e("MenuRepository", errorMessage);
            }
        });

        return categoriesLiveData;
    }

    // Callback interfaces
    public interface CategoriesCallback {
        void onCategoriesLoaded(List<String> categories);

        void onCategoriesError(String errorMessage);
    }

    public interface ProductsCallback {
        void onSuccess(List<Product> products);

        void onError(String errorMessage);
    }

    public MenuRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<List<MenuItem>> getMenuItems() {
        MutableLiveData<List<MenuItem>> data = new MutableLiveData<>();
        // TODO: Implement API call to fetch menu items and update the LiveData
        // apiService.getMenuItems(...)
        // If your API returns List<Product>, you should use List<Product> here, or
        // convert to MenuItem if needed.
        apiService.getProducts().enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call,
                    Response<ApiResponse<List<Product>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    // If you need MenuItem, convert Product to MenuItem here
                    // For now, just log or ignore
                } else {
                    android.util.Log.e("MenuRepository", "Failed to fetch menu items: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                android.util.Log.e("MenuRepository", "Network error fetching menu items", t);
                t.printStackTrace();
            }
        });
        return data;
    }

    /**
     * Get all product categories
     *
     * @param callback Callback to handle result
     */
    public void getCategories(@NonNull CategoriesCallback callback) {
        apiService.getCategories().enqueue(new Callback<ApiResponse<List<String>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<String>>> call, Response<ApiResponse<List<String>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onCategoriesLoaded(response.body().getData());
                } else {
                    // Handle API error response
                    android.util.Log.e("MenuRepository", "Failed to fetch categories: " + response.message());
                    callback.onCategoriesError("Failed to load categories");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<String>>> call, Throwable t) {
                // Handle network failure
                android.util.Log.e("MenuRepository", "Network error fetching categories", t);
                callback.onCategoriesError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Get products by category
     *
     * @param category Category to filter by, or null for all products
     * @param callback Callback to handle results
     */
    public void getProductsByCategory(String category, @NonNull ProductsCallback callback) {
        // Make API call with the category filter
        apiService.getProductsByCategory(category).enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call,
                    Response<ApiResponse<List<Product>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    // Handle API error response
                    android.util.Log.e("MenuRepository", "Failed to fetch products by category: " + response.message());
                    callback.onError("Failed to load products");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                // Handle network failure
                android.util.Log.e("MenuRepository", "Network error fetching products by category", t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Get all products
     *
     * @param callback Callback to handle results
     */
    public void getAllProducts(@NonNull ProductsCallback callback) {
        apiService.getProducts().enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call,
                    Response<ApiResponse<List<Product>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    // Handle API error response
                    android.util.Log.e("MenuRepository", "Failed to fetch all products: " + response.message());
                    callback.onError("Failed to load products");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                // Handle network failure
                android.util.Log.e("MenuRepository", "Network error fetching all products", t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Search products
     *
     * @param query    Search query
     * @param callback Callback to handle results
     */
    public void searchProducts(String query, @NonNull ProductsCallback callback) {
        apiService.searchProducts(query).enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call,
                    Response<ApiResponse<List<Product>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    // Handle API error response
                    android.util.Log.e("MenuRepository", "Failed to search products: " + response.message());
                    callback.onError("Failed to search products");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                // Handle network failure
                android.util.Log.e("MenuRepository", "Network error searching products", t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
}