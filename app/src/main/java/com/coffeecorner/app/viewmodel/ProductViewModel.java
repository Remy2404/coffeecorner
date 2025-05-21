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
        return productRepository.getAllProducts(new ProductRepository.ProductCallback() {
            @Override
            public void onComplete() {
                isLoading.postValue(false);
            }

            @Override
            public void onError(String message) {
                isLoading.postValue(false);
                errorMessage.postValue(message);
            }
        });
    }

    /**
     * Get featured products for home screen
     * 
     * @return LiveData containing list of featured products
     */
    public LiveData<List<Product>> getFeaturedProducts() {
        isLoading.setValue(true);
        return productRepository.getFeaturedProducts(new ProductRepository.ProductCallback() {
            @Override
            public void onComplete() {
                isLoading.postValue(false);
            }

            @Override
            public void onError(String message) {
                isLoading.postValue(false);
                errorMessage.postValue(message);
            }
        });
    }

    /**
     * Get products by category
     * 
     * @param category Product category
     * @return LiveData containing list of products in the specified category
     */
    public LiveData<List<Product>> getProductsByCategory(String category) {
        isLoading.setValue(true);
        return productRepository.getProductsByCategory(category, new ProductRepository.ProductCallback() {
            @Override
            public void onComplete() {
                isLoading.postValue(false);
            }

            @Override
            public void onError(String message) {
                isLoading.postValue(false);
                errorMessage.postValue(message);
            }
        });
    }

    /**
     * Get product by ID
     * 
     * @param productId Product ID
     * @return LiveData containing product details
     */
    public LiveData<Product> getProductById(String productId) {
        isLoading.setValue(true);
        return productRepository.getProductById(productId, new ProductRepository.ProductDetailCallback() {
            @Override
            public void onComplete() {
                isLoading.postValue(false);
            }

            @Override
            public void onError(String message) {
                isLoading.postValue(false);
                errorMessage.postValue(message);
            }
        });
    }

    /**
     * Search products by name or keyword
     * 
     * @param query Search query
     * @return LiveData containing list of matching products
     */
    public LiveData<List<Product>> searchProducts(String query) {
        isLoading.setValue(true);
        return productRepository.searchProducts(query, new ProductRepository.ProductCallback() {
            @Override
            public void onComplete() {
                isLoading.postValue(false);
            }

            @Override
            public void onError(String message) {
                isLoading.postValue(false);
                errorMessage.postValue(message);
            }
        });
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
