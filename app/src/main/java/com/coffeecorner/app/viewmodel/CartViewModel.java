package com.coffeecorner.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.coffeecorner.app.models.CartItem;
import com.coffeecorner.app.models.Product;
import com.coffeecorner.app.repositories.CartRepository;

import java.util.List;

/**
 * CartViewModel - Manages shopping cart operations and data
 * Handles adding items to cart, updating quantities, and calculating totals
 */
public class CartViewModel extends ViewModel {

    private final CartRepository cartRepository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> message = new MutableLiveData<>();

    public CartViewModel(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
        isLoading.setValue(false);
    }

    /**
     * Add product to shopping cart
     * 
     * @param product  Product to add
     * @param quantity Quantity to add
     */
    public void addToCart(Product product, int quantity) {
        isLoading.setValue(true);
        cartRepository.addToCart(product, quantity, new CartRepository.CartItemsCallback() {
            @Override
            public void onCartItemsLoaded(List<CartItem> cartItems) {
                isLoading.postValue(false);
                message.postValue("Item added to cart");
            }

            @Override
            public void onError(String errorMessage) {
                isLoading.postValue(false);
                message.postValue(errorMessage);
            }
        });
    }

    /**
     * Update cart item quantity
     * 
     * @param itemId      Cart item ID
     * @param newQuantity New quantity
     */
    public void updateCartItemQuantity(String itemId, int newQuantity) {
        isLoading.setValue(true);
        cartRepository.updateCartItemQuantity(itemId, newQuantity, new CartRepository.CartOperationCallback() {
            @Override
            public void onSuccess(String successMsg) { // Changed from onSuccess()
                isLoading.postValue(false);
                message.postValue(successMsg); // Use the message from callback
            }

            @Override
            public void onError(String errorMessage) {
                isLoading.postValue(false);
                message.postValue(errorMessage);
            }
        });
    }

    /**
     * Remove item from cart
     * 
     * @param itemId Cart item ID
     */
    public void removeFromCart(String itemId) {
        isLoading.setValue(true);
        cartRepository.removeFromCart(itemId, new CartRepository.CartItemsCallback() {
            @Override
            public void onCartItemsLoaded(List<CartItem> cartItems) {
                isLoading.postValue(false);
                message.postValue("Item removed from cart");
            }

            @Override
            public void onError(String errorMessage) {
                isLoading.postValue(false);
                message.postValue(errorMessage);
            }
        });
    }

    /**
     * Clear all items from cart
     */
    public void clearCart() {
        isLoading.setValue(true);
        cartRepository.clearCart(new CartRepository.CartOperationCallback() { // Changed to CartOperationCallback
            @Override
            public void onSuccess(String successMsg) {
                isLoading.postValue(false);
                message.postValue(successMsg);
            }

            @Override
            public void onError(String errorMessage) {
                isLoading.postValue(false);
                message.postValue(errorMessage);
            }
        });
    }

    /**
     * Get all items in cart
     * 
     * @return LiveData containing list of cart items
     */
    public LiveData<List<CartItem>> getCartItems() {
        isLoading.setValue(true);
        MutableLiveData<List<CartItem>> cartItemsLiveData = new MutableLiveData<>();

        cartRepository.getCartItems(new CartRepository.CartItemsCallback() {
            @Override
            public void onCartItemsLoaded(List<CartItem> cartItems) {
                isLoading.postValue(false);
                cartItemsLiveData.postValue(cartItems);
            }

            @Override
            public void onError(String errorMessage) {
                isLoading.postValue(false);
                message.postValue(errorMessage);
                cartItemsLiveData.postValue(java.util.Collections.emptyList());
            }
        });

        return cartItemsLiveData;
    }

    /**
     * Get cart subtotal
     * 
     * @return LiveData containing cart subtotal
     */
    public LiveData<Double> getCartSubtotal() {
        MutableLiveData<Double> subtotalLiveData = new MutableLiveData<>(0.0);

        cartRepository.getCartItems(new CartRepository.CartItemsCallback() {
            @Override
            public void onCartItemsLoaded(List<CartItem> cartItems) {
                double subtotal = 0.0;
                if (cartItems != null) {
                    for (CartItem item : cartItems) {
                        subtotal += item.getPrice() * item.getQuantity();
                    }
                }
                subtotalLiveData.postValue(subtotal);
            }

            @Override
            public void onError(String errorMessage) {
                message.postValue(errorMessage);
                subtotalLiveData.postValue(0.0);
            }
        });

        return subtotalLiveData;
    }

    /**
     * Get cart item count
     * 
     * @return LiveData containing number of items in cart
     */
    public LiveData<Integer> getCartItemCount() {
        MutableLiveData<Integer> countLiveData = new MutableLiveData<>(0);

        cartRepository.getCartItems(new CartRepository.CartItemsCallback() {
            @Override
            public void onCartItemsLoaded(List<CartItem> cartItems) {
                int count = 0;
                if (cartItems != null) {
                    for (CartItem item : cartItems) {
                        count += item.getQuantity();
                    }
                }
                countLiveData.postValue(count);
            }

            @Override
            public void onError(String errorMessage) {
                message.postValue(errorMessage);
                countLiveData.postValue(0);
            }
        });

        return countLiveData;
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
     * Get operation message
     * 
     * @return LiveData containing success or error message
     */
    public LiveData<String> getMessage() {
        return message;
    }
}
