package com.coffeecorner.app.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.coffeecorner.app.models.Product;
import com.coffeecorner.app.repositories.ProductRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // Add cache maps
    private final Map<String, List<Product>> categoryProductCache = new HashMap<>();
    private long lastCacheTime = 0;
    private static final long CACHE_DURATION = 5 * 60 * 1000; // 5 minutes cache

    // Store original list for filtering/sorting
    private List<Product> originalProducts = new ArrayList<>();

    public ProductViewModel(@NonNull Application application) {
        super(application);
        productRepository = ProductRepository.getInstance(); // Get repository instance

        // Load initial data
        loadCategories();

    }

    /**
     * Load all products from cache or data source
     */
    public void loadProducts() {
        String categoryToLoad = selectedCategory.getValue();
        if (categoryToLoad == null || "All".equalsIgnoreCase(categoryToLoad)) {
            // Check cache first
            if (isCacheValid() && categoryProductCache.containsKey("All")) {
                products.setValue(categoryProductCache.get("All"));
                return;
            }

            isLoading.setValue(true);
            productRepository.getProducts(new ProductRepository.ProductsCallback() {
                @Override
                public void onProductsLoaded(List<Product> productList) {
                    products.setValue(productList);
                    // Cache the results
                    categoryProductCache.put("All", productList);
                    lastCacheTime = System.currentTimeMillis();
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

    public void filterByCategory(String categoryParam) {
        String finalCategory = categoryParam;
        if (finalCategory == null) {
            finalCategory = "All";
        }

        // Check cache first
        if (isCacheValid() && categoryProductCache.containsKey(finalCategory)) {
            products.setValue(categoryProductCache.get(finalCategory));
            selectedCategory.setValue(finalCategory);
            return;
        }

        isLoading.setValue(true);
        selectedCategory.setValue(finalCategory); // Update selected category

        final String categoryToFetch = finalCategory; // Effectively final for use in inner class
        productRepository.getProductsByCategory(categoryToFetch, new ProductRepository.ProductsCallback() {
            @Override
            public void onProductsLoaded(List<Product> productList) {
                products.setValue(productList);
                // Cache the results
                categoryProductCache.put(categoryToFetch, productList);
                lastCacheTime = System.currentTimeMillis();
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
        productRepository.getProductById(productId, new ProductRepository.ProductDetailCallback() {
            @Override
            public void onProductLoaded(Product product) {
                selectedProduct.setValue(product);
                isLoading.setValue(false);
                errorMessage.setValue(null);
            }

            @Override
            public void onProductError(String errorMsg) {
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

    /**
     * Check if the cache is still valid
     */
    private boolean isCacheValid() {
        return System.currentTimeMillis() - lastCacheTime < CACHE_DURATION;
    }

    /**
     * Clear the cache (call this when needed, e.g., pull-to-refresh)
     */
    public void clearCache() {
        categoryProductCache.clear();
        lastCacheTime = 0;
        loadProducts(); // Reload from network
    }

    /**
     * Sort products by price ascending
     */
    public void sortByPriceAscending() {
        List<Product> currentProducts = products.getValue();
        if (currentProducts != null && !currentProducts.isEmpty()) {
            List<Product> sortedProducts = new ArrayList<>(currentProducts);
            Collections.sort(sortedProducts, new Comparator<Product>() {
                @Override
                public int compare(Product p1, Product p2) {
                    return Double.compare(p1.getPrice(), p2.getPrice());
                }
            });
            products.setValue(sortedProducts);
        }
    }

    /**
     * Sort products by price descending
     */
    public void sortByPriceDescending() {
        List<Product> currentProducts = products.getValue();
        if (currentProducts != null && !currentProducts.isEmpty()) {
            List<Product> sortedProducts = new ArrayList<>(currentProducts);
            Collections.sort(sortedProducts, new Comparator<Product>() {
                @Override
                public int compare(Product p1, Product p2) {
                    return Double.compare(p2.getPrice(), p1.getPrice());
                }
            });
            products.setValue(sortedProducts);
        }
    }    /**
     * Sort products by popularity (rating descending as proxy for popularity)
     */
    public void sortByPopularity() {
        List<Product> currentProducts = products.getValue();
        if (currentProducts != null && !currentProducts.isEmpty()) {
            List<Product> sortedProducts = new ArrayList<>(currentProducts);
            Collections.sort(sortedProducts, new Comparator<Product>() {
                @Override
                public int compare(Product p1, Product p2) {
                    // Use rating as proxy for popularity since orderCount doesn't exist
                    return Float.compare(p2.getRating(), p1.getRating());
                }
            });
            products.setValue(sortedProducts);
        }
    }

    /**
     * Sort products by newest (creation date descending)
     */
    public void sortByNewest() {
        List<Product> currentProducts = products.getValue();
        if (currentProducts != null && !currentProducts.isEmpty()) {
            List<Product> sortedProducts = new ArrayList<>(currentProducts);
            Collections.sort(sortedProducts, new Comparator<Product>() {
                @Override
                public int compare(Product p1, Product p2) {
                    // Assuming newer products have higher ID or creation timestamp
                    return p2.getId().compareTo(p1.getId());
                }
            });
            products.setValue(sortedProducts);
        }
    }

    /**
     * Sort products by rating descending
     */
    public void sortByRating() {
        List<Product> currentProducts = products.getValue();
        if (currentProducts != null && !currentProducts.isEmpty()) {
            List<Product> sortedProducts = new ArrayList<>(currentProducts);
            Collections.sort(sortedProducts, new Comparator<Product>() {
                @Override
                public int compare(Product p1, Product p2) {
                    return Double.compare(p2.getRating(), p1.getRating());
                }
            });
            products.setValue(sortedProducts);
        }
    }

    /**
     * Filter to show only available products
     */
    public void filterAvailableOnly() {
        List<Product> currentProducts = products.getValue();
        if (currentProducts != null && !currentProducts.isEmpty()) {
            List<Product> availableProducts = new ArrayList<>();
            for (Product product : currentProducts) {
                if (product.isAvailable()) {
                    availableProducts.add(product);
                }
            }
            products.setValue(availableProducts);
        }
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
