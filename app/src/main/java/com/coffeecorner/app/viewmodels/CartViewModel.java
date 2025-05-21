package com.coffeecorner.app.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.coffeecorner.app.models.CartItem;
import com.coffeecorner.app.models.Product;
import com.coffeecorner.app.repositories.CartRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * CartViewModel - Manages and provides cart data to the UI
 * Handles adding, removing, updating cart items by interacting with
 * CartRepository.
 */
public class CartViewModel extends AndroidViewModel {

    private static final String TAG = "CartViewModel";

    private final CartRepository cartRepository;
    private final MutableLiveData<List<CartItem>> cartItems = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Double> cartTotal = new MutableLiveData<>(0.0);
    private final MutableLiveData<Integer> cartItemCount = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>(); // For success messages like "Cart
                                                                                    // cleared"

    public CartViewModel(@NonNull Application application) {
        super(application);
        cartRepository = CartRepository.getInstance(application.getApplicationContext());
        loadCartItems(); // Load initial cart data from the repository
    }

    /**
     * Load cart items from the repository (which fetches from backend).
     */
    public void loadCartItems() {
        isLoading.setValue(true);
        Log.d(TAG, "loadCartItems: Requesting cart items from repository.");
        cartRepository.getCartItems(new CartRepository.CartItemsCallback() {
            @Override
            public void onCartItemsLoaded(List<CartItem> items) {
                cartItems.setValue(items != null ? items : new ArrayList<>());
                updateCartSummary(items);
                isLoading.setValue(false);
                errorMessage.setValue(null); // Clear previous errors
                Log.d(TAG, "loadCartItems onSuccess: Loaded " + (items != null ? items.size() : 0) + " items.");
            }

            @Override
            public void onError(String errorMsg) {
                cartItems.setValue(new ArrayList<>()); // Clear cart on error
                updateCartSummary(new ArrayList<>());
                errorMessage.setValue("Failed to load cart: " + errorMsg);
                isLoading.setValue(false);
                Log.e(TAG, "loadCartItems onError: " + errorMsg);
            }
        });
    }

    /**
     * Add a product to the cart via the repository.
     *
     * @param product  Product to add.
     * @param quantity Quantity to add.
     */
    public void addToCart(Product product, int quantity) {
        if (product == null || quantity <= 0) {
            errorMessage.setValue("Invalid product or quantity for adding to cart.");
            Log.w(TAG, "addToCart: Invalid product or quantity.");
            return;
        }
        isLoading.setValue(true);
        Log.d(TAG, "addToCart: Adding productId: " + product.getId() + " quantity: " + quantity);
        cartRepository.addToCart(product, quantity, new CartRepository.CartItemsCallback() {
            @Override
            public void onCartItemsLoaded(List<CartItem> items) {
                cartItems.setValue(items != null ? items : new ArrayList<>());
                updateCartSummary(items);
                isLoading.setValue(false);
                successMessage.setValue(product.getName() + " added to cart.");
                errorMessage.setValue(null);
                Log.d(TAG, "addToCart onSuccess: Item added. Cart size: " + (items != null ? items.size() : 0));
            }

            @Override
            public void onError(String errorMsg) {
                errorMessage.setValue("Failed to add to cart: " + errorMsg);
                isLoading.setValue(false);
                Log.e(TAG, "addToCart onError: " + errorMsg);
                // Optionally, reload cart to ensure UI consistency if add failed partially
                // loadCartItems();
            }
        });
    }

    /**
     * Remove an item from the cart via the repository.
     *
     * @param cartItem Item to remove (used for its ID).
     */
    public void removeFromCart(CartItem cartItem) {
        if (cartItem == null || cartItem.getProductId() == null) { // Assuming ProductId is the key used as itemId
            errorMessage.setValue("Invalid item to remove from cart.");
            Log.w(TAG, "removeFromCart: Invalid cart item or product ID.");
            return;
        }
        isLoading.setValue(true);
        // Assuming cartItem.getProductId() is the `itemId` the repository expects.
        // If your CartItem has a unique `id` field distinct from `productId`, use that.
        String itemIdToRemove = cartItem.getProductId();
        Log.d(TAG, "removeFromCart: Removing itemId: " + itemIdToRemove);

        cartRepository.removeFromCart(itemIdToRemove, new CartRepository.CartItemsCallback() {
            @Override
            public void onCartItemsLoaded(List<CartItem> items) {
                cartItems.setValue(items != null ? items : new ArrayList<>());
                updateCartSummary(items);
                isLoading.setValue(false);
                successMessage.setValue("Item removed from cart.");
                errorMessage.setValue(null);
                Log.d(TAG, "removeFromCart onSuccess. Cart size: " + (items != null ? items.size() : 0));
            }

            @Override
            public void onError(String errorMsg) {
                errorMessage.setValue("Failed to remove from cart: " + errorMsg);
                isLoading.setValue(false);
                Log.e(TAG, "removeFromCart onError: " + errorMsg);
            }
        });
    }

    /**
     * Update the quantity of an item in the cart via the repository.
     *
     * @param cartItem    Item to update (used for its ID).
     * @param newQuantity New quantity.
     */
    public void updateCartItemQuantity(CartItem cartItem, int newQuantity) {
        if (cartItem == null || cartItem.getProductId() == null || newQuantity < 0) {
            errorMessage.setValue("Invalid item or quantity for update.");
            Log.w(TAG, "updateCartItemQuantity: Invalid cart item, product ID, or new quantity.");
            return;
        }
        isLoading.setValue(true);
        // Assuming cartItem.getProductId() is the `itemId`.
        String itemIdToUpdate = cartItem.getProductId();
        Log.d(TAG, "updateCartItemQuantity: Updating itemId: " + itemIdToUpdate + " to quantity: " + newQuantity);

        cartRepository.updateQuantity(itemIdToUpdate, newQuantity, new CartRepository.CartItemsCallback() {
            @Override
            public void onCartItemsLoaded(List<CartItem> items) {
                cartItems.setValue(items != null ? items : new ArrayList<>());
                updateCartSummary(items);
                isLoading.setValue(false);
                successMessage.setValue("Cart updated.");
                errorMessage.setValue(null);
                Log.d(TAG, "updateCartItemQuantity onSuccess. Cart size: " + (items != null ? items.size() : 0));
            }

            @Override
            public void onError(String errorMsg) {
                errorMessage.setValue("Failed to update cart: " + errorMsg);
                isLoading.setValue(false);
                Log.e(TAG, "updateCartItemQuantity onError: " + errorMsg);
            }
        });
    }

    /**
     * Clear all items from the cart via the repository.
     */
    public void clearCart() {
        isLoading.setValue(true);
        Log.d(TAG, "clearCart: Requesting to clear cart.");
        cartRepository.clearCart(new CartRepository.CartModificationCallback() {
            @Override
            public void onSuccess(String message) {
                cartItems.setValue(new ArrayList<>()); // Clear local list
                updateCartSummary(new ArrayList<>());
                isLoading.setValue(false);
                successMessage.setValue(message != null ? message : "Cart cleared successfully.");
                errorMessage.setValue(null);
                Log.d(TAG, "clearCart onSuccess: " + message);
            }

            @Override
            public void onError(String errorMsg) {
                errorMessage.setValue("Failed to clear cart: " + errorMsg);
                isLoading.setValue(false);
                Log.e(TAG, "clearCart onError: " + errorMsg);
            }
        });
    }

    /**
     * Calculate and update the cart total and item count based on current
     * cartItems.
     * 
     * @param currentItems The list of cart items to summarize.
     */
    private void updateCartSummary(List<CartItem> currentItems) {
        if (currentItems == null) {
            cartTotal.setValue(0.0);
            cartItemCount.setValue(0);
            return;
        }

        double total = 0;
        int count = 0;
        for (CartItem item : currentItems) {
            total += item.getPrice() * item.getQuantity();
            count += item.getQuantity();
        }
        cartTotal.setValue(total);
        cartItemCount.setValue(count);
        Log.d(TAG, "updateCartSummary: Total = " + total + ", Count = " + count);
    }

    // Getters for LiveData
    public LiveData<List<CartItem>> getCartItems() {
        return cartItems;
    }

    public LiveData<Double> getCartTotal() {
        return cartTotal;
    }

    public LiveData<Integer> getCartItemCount() {
        return cartItemCount;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "onCleared: CartViewModel is cleared");
    }
}
