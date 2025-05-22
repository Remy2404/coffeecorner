package com.coffeecorner.app.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.coffeecorner.app.models.Product;
import com.coffeecorner.app.repositories.MenuRepository;
import com.coffeecorner.app.network.RetrofitClient;
import com.coffeecorner.app.models.MenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * MenuViewModel - Manages menu-related data and operations
 * Handles product categories, filtering, and sorting
 */
public class MenuViewModel extends ViewModel {

    private final MenuRepository menuRepository;
    private final MutableLiveData<List<String>> categories = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> filteredProducts = new MutableLiveData<>();
    private final MutableLiveData<String> currentCategory = new MutableLiveData<>();

    public MenuViewModel() {
        // Default constructor for ViewModelProvider
        this.menuRepository = new MenuRepository(RetrofitClient.getApiService());
        isLoading.setValue(false);
        loadCategories();
        currentCategory.setValue("All"); // Default category
    }

    public MenuViewModel(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
        isLoading.setValue(false);
        loadCategories();
        currentCategory.setValue("All"); // Default category
    }

    /**
     * Load product categories
     */
    private void loadCategories() {
        isLoading.setValue(true);
        menuRepository.getCategories(new MenuRepository.CategoriesCallback() {
            @Override
            public void onCategoriesLoaded(List<String> categoryList) {
                categories.postValue(categoryList);
                isLoading.postValue(false);
            }

            @Override
            public void onCategoriesError(String message) {
                errorMessage.postValue(message);
                isLoading.postValue(false);
            }
        });
    }

    /**
     * Load products based on selected category
     * 
     * @param category Product category (use "All" for all products)
     */
    public void loadProductsByCategory(String category) {
        isLoading.setValue(true);
        currentCategory.setValue(category);
        if (category.equals("All")) {
            menuRepository.getAllProducts(new MenuRepository.ProductsCallback() {
                @Override
                public void onSuccess(List<Product> productList) {
                    filteredProducts.postValue(productList);
                    isLoading.postValue(false);
                }

                @Override
                public void onError(String message) {
                    errorMessage.postValue(message);
                    isLoading.postValue(false);
                }
            });
        } else {
            menuRepository.getProductsByCategory(category, new MenuRepository.ProductsCallback() {
                @Override
                public void onSuccess(List<Product> productList) {
                    filteredProducts.postValue(productList);
                    isLoading.postValue(false);
                }

                @Override
                public void onError(String message) {
                    errorMessage.postValue(message);
                    isLoading.postValue(false);
                }
            });
        }
    }

    /**
     * Search products by query
     * 
     * @param query Search query
     */
    public void searchProducts(String query) {
        isLoading.setValue(true);
        menuRepository.searchProducts(query, new MenuRepository.ProductsCallback() {
            @Override
            public void onSuccess(List<Product> productList) {
                filteredProducts.postValue(productList);
                isLoading.postValue(false);
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
                isLoading.postValue(false);
            }
        });
    }

    /**
     * Get product categories
     * 
     * @return LiveData containing list of product categories
     */
    public LiveData<List<String>> getCategories() {
        return categories;
    }

    /**
     * Get filtered products
     * 
     * @return LiveData containing list of filtered products
     */
    public LiveData<List<Product>> getFilteredProducts() {
        return filteredProducts;
    }

    /**
     * Get current category
     * 
     * @return LiveData containing current category
     */
    public LiveData<String> getCurrentCategory() {
        return currentCategory;
    }

    /**
     * Get loading state
     * 
     * @return LiveData indicating if an operation is in progress
     */
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    /**
     * Get error message
     * 
     * @return LiveData containing error message if any
     */
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<List<Object>> getFilteredMenuItems() {
        MutableLiveData<List<Object>> filteredMenuItems = new MutableLiveData<>();
        List<Object> items = new ArrayList<>();

        // Add categories to the list
        List<String> categoryList = categories.getValue();
        if (categoryList != null && !categoryList.isEmpty()) {
            items.addAll(categoryList);
        }

        // Add products to the list
        List<Product> productList = filteredProducts.getValue();
        if (productList != null && !productList.isEmpty()) {
            items.addAll(productList);
        }

        filteredMenuItems.setValue(items);
        return filteredMenuItems;
    }
}