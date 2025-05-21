package com.coffeecorner.app.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.coffeecorner.app.network.ApiResponse;
import com.coffeecorner.app.network.ApiService;
import com.coffeecorner.app.models.MenuItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuRepository {

    private ApiService apiService;

    public MenuRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<List<MenuItem>> getMenuItems() {
        MutableLiveData<List<MenuItem>> data = new MutableLiveData<>();
        // TODO: Implement API call to fetch menu items and update the LiveData
        // apiService.getMenuItems(...)
        apiService.getProducts(null).enqueue(new Callback<ApiResponse<List<MenuItem>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<MenuItem>>> call, Response<ApiResponse<List<MenuItem>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    data.setValue(response.body().getData());
                } else {
                    // Handle API error response
                    android.util.Log.e("MenuRepository", "Failed to fetch menu items: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<MenuItem>>> call, Throwable t) {
                // Handle network failure
                android.util.Log.e("MenuRepository", "Network error fetching menu items", t);
                t.printStackTrace();
            }
        });
        return data;
    }

    public LiveData<List<String>> getCategories() {
        MutableLiveData<List<String>> data = new MutableLiveData<>();
        apiService.getCategories().enqueue(new Callback<ApiResponse<List<String>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<String>>> call, Response<ApiResponse<List<String>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    data.setValue(response.body().getData());
                } else {
                    // Handle API error response
                    android.util.Log.e("MenuRepository", "Failed to fetch categories: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<String>>> call, Throwable t) {
                // Handle network failure
                android.util.Log.e("MenuRepository", "Network error fetching categories", t);
                t.printStackTrace();
            }
        });
        return data;
    }

    // TODO: Add methods for fetching categories or filtering based on category from API if needed
} 