package com.coffeecorner.app.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.coffeecorner.app.models.Product;
import com.coffeecorner.app.repositories.ProductRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * ProductViewModel - Manages and provides product data to the UI
 * Handles product listing, filtering, and details
 */
public class ProductViewModel extends AndroidViewModel {

    private final ProductRepository productRepository;
    private final MutableLiveData<List<Product>> products = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<String>> categories = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> selectedCategory = new MutableLiveData<>("All");
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Product> selectedProduct = new MutableLiveData<>(); // For product details

    public ProductViewModel(@NonNull Application application) {
        super(application);
        productRepository = ProductRepository.getInstance(); // Get repository instance

        // Load initial data
        loadCategories(); // Load categories first, then products based on "All"
        // loadProducts(); // This will be called by loadCategories or explicitly
    }

    /**
     * Load all products from the data source or based on the current
     * selectedCategory
     */
    public void loadProducts() {
        isLoading.setValue(true);
        String categoryToLoad = selectedCategory.getValue();
        if (categoryToLoad == null || "All".equalsIgnoreCase(categoryToLoad)) {
            productRepository.getProducts(new ProductRepository.ProductsCallback() {
                @Override
                public void onProductsLoaded(List<Product> productList) {
                    products.setValue(productList);
                    isLoading.setValue(false);
                    errorMessage.setValue(null);
                }

                @Override
                public void onError(String errorMsg) {
                    products.setValue(new ArrayList<>()); // Set to empty list on error
                    errorMessage.setValue(errorMsg);
                    isLoading.setValue(false);
                }
            });
        } else {
            // This case is handled by filterByCategory, which calls the repository
            // If called directly, it implies loading products for the currently selected
            // category
            filterByCategory(categoryToLoad);
        }
    }

    /**
     * Filter products by category
     * 
     * @param category Category to filter by, or "All" for no filtering
     */
    public void filterByCategory(String category) {
        isLoading.setValue(true);
        selectedCategory.setValue(category); // Update selected category

        productRepository.getProductsByCategory(category, new ProductRepository.ProductsCallback() {
            @Override
            public void onProductsLoaded(List<Product> productList) {
                products.setValue(productList);
                isLoading.setValue(false);
                errorMessage.setValue(null);
            }

            @Override
            public void onError(String errorMsg) {
                products.setValue(new ArrayList<>()); // Set to empty list on error
                errorMessage.setValue(errorMsg);
                isLoading.setValue(false);
            }
        });
    }

    /**
     * Search products by query string
     * 
     * @param query Search query
     */
    public void searchProducts(String query) {
        isLoading.setValue(true);
        selectedCategory.setValue("All"); // Reset category when searching
        productRepository.searchProducts(query, new ProductRepository.ProductsCallback() {
            @Override
            public void onProductsLoaded(List<Product> productList) {
                products.setValue(productList);
                isLoading.setValue(false);
                errorMessage.setValue(null);
            }

            @Override
            public void onError(String errorMsg) {
                products.setValue(new ArrayList<>()); // Set to empty list on error
                errorMessage.setValue(errorMsg);
                isLoading.setValue(false);
            }
        });
    }

    /**
     * Get a specific product by ID and update LiveData for details screen
     * 
     * @param productId Product ID
     */
    public void loadProductById(String productId) {
        isLoading.setValue(true);
        productRepository.getProductById(productId, new ProductRepository.ProductCallback() {
            @Override
            public void onProductLoaded(Product product) {
                selectedProduct.setValue(product);
                isLoading.setValue(false);
                errorMessage.setValue(null);
            }

            @Override
            public void onError(String errorMsg) {
                selectedProduct.setValue(null); // Set to null on error
                errorMessage.setValue(errorMsg);
                isLoading.setValue(false);
            }
        });
    }

    /**
     * Load product categories
     */
    private void loadCategories() {
        isLoading.setValue(true); // Can use a separate isLoadingCategories if needed
        productRepository.getCategories(new ProductRepository.CategoriesCallback() {
            @Override
            public void onCategoriesLoaded(List<String> categoryList) {
                categories.setValue(categoryList);
                // After categories are loaded, load products for the default category (e.g.,
                // "All")
                if (categoryList != null && !categoryList.isEmpty()) {
                    filterByCategory(selectedCategory.getValue() != null ? selectedCategory.getValue() : "All");
                } else {
                    products.setValue(new ArrayList<>()); // No categories, so no products
                    isLoading.setValue(false); // Stop loading if categories fail
                }
                // isLoading.setValue(false); // Loading of products will set this
                errorMessage.setValue(null);
            }

            @Override
            public void onError(String errorMsg) {
                categories.setValue(new ArrayList<>()); // Set to empty list on error
                products.setValue(new ArrayList<>()); // Also clear products
                errorMessage.setValue(errorMsg);
                isLoading.setValue(false);
            }
        });
    }

    // Getters for LiveData
    public LiveData<List<Product>> getProducts() {
        return products;
    }

    public LiveData<List<String>> getCategories() {
        return categories;
    }

    public LiveData<String> getSelectedCategory() {
        return selectedCategory;
    }

    public LiveData<Product> getSelectedProduct() { // For product details
        return selectedProduct;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
}
