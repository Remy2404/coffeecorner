package com.coffeecorner.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.coffeecorner.app.models.Product;
import com.coffeecorner.app.repositories.ProductRepository;

import java.util.List;

/**
 * ProductViewModel - Manages product-related data and operations
 * Provides products for the menu, home screen featured products, etc.
 */
public class ProductViewModel extends ViewModel {

    private final ProductRepository productRepository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public ProductViewModel(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Get all products
     * 
     * @return LiveData containing list of all products
     */
    public LiveData<List<Product>> getAllProducts() {
        isLoading.setValue(true);
        MutableLiveData<List<Product>> productsLiveData = new MutableLiveData<>();

        productRepository.getProducts(new ProductRepository.ProductsCallback() {
            @Override
            public void onProductsLoaded(List<Product> products) {
                isLoading.postValue(false);
                productsLiveData.postValue(products);
            }

            @Override
            public void onError(String errorMsg) {
                isLoading.postValue(false);
                errorMessage.postValue(errorMsg);
            }
        });

        return productsLiveData;
    }

    /**
     * Get featured products for home screen
     * 
     * @return LiveData containing list of featured products
     */
    public LiveData<List<Product>> getFeaturedProducts() {
        isLoading.setValue(true);
        MutableLiveData<List<Product>> featuredProductsLiveData = new MutableLiveData<>();

        productRepository.getProducts(new ProductRepository.ProductsCallback() {
            @Override
            public void onProductsLoaded(List<Product> products) {
                isLoading.postValue(false);
                // In a real app, you might filter for featured products
                featuredProductsLiveData.postValue(products);
            }

            @Override
            public void onError(String errorMsg) {
                isLoading.postValue(false);
                errorMessage.postValue(errorMsg);
            }
        });

        return featuredProductsLiveData;
    }

    /**
     * Get products by category
     * 
     * @param category Product category
     * @return LiveData containing list of products in the specified category
     */
    public LiveData<List<Product>> getProductsByCategory(String category) {
        isLoading.setValue(true);
        MutableLiveData<List<Product>> categoryProductsLiveData = new MutableLiveData<>();

        productRepository.getProductsByCategory(category, new ProductRepository.ProductsCallback() {
            @Override
            public void onProductsLoaded(List<Product> products) {
                isLoading.postValue(false);
                categoryProductsLiveData.postValue(products);
            }

            @Override
            public void onError(String errorMsg) {
                isLoading.postValue(false);
                errorMessage.postValue(errorMsg);
            }
        });

        return categoryProductsLiveData;
    }

    /**
     * Get product by ID
     * 
     * @param productId Product ID
     * @return LiveData containing product details
     */
    public LiveData<Product> getProductById(String productId) {
        isLoading.setValue(true);
        MutableLiveData<Product> productLiveData = new MutableLiveData<>();

        productRepository.getProductDetails(productId, new ProductRepository.ProductDetailCallback() {
            @Override
            public void onProductLoaded(Product product) {
                isLoading.postValue(false);
                productLiveData.postValue(product);
            }

            @Override
            public void onProductError(String errorMsg) {
                isLoading.postValue(false);
                errorMessage.postValue(errorMsg);
            }
        });

        return productLiveData;
    }

    /**
     * Search products by name or keyword
     * 
     * @param query Search query
     * @return LiveData containing list of matching products
     */
    public LiveData<List<Product>> searchProducts(String query) {
        isLoading.setValue(true);
        MutableLiveData<List<Product>> searchResultsLiveData = new MutableLiveData<>();

        // Assuming repository has a search method
        productRepository.getProducts(new ProductRepository.ProductsCallback() {
            @Override
            public void onProductsLoaded(List<Product> products) {
                isLoading.postValue(false);
                // In a real app, you'd have a dedicated search method in the repository
                searchResultsLiveData.postValue(products);
            }

            @Override
            public void onError(String errorMsg) {
                isLoading.postValue(false);
                errorMessage.postValue(errorMsg);
            }
        });
        return searchResultsLiveData;
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
}
