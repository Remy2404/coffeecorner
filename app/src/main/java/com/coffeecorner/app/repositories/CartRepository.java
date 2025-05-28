package com.coffeecorner.app.repositories;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.coffeecorner.app.models.CartItem;
import com.coffeecorner.app.models.Product;
import com.coffeecorner.app.network.ApiService;
import com.coffeecorner.app.network.ApiResponse;
import com.coffeecorner.app.network.RetrofitClient;
import com.coffeecorner.app.utils.PreferencesHelper;
import com.coffeecorner.app.utils.LocalCartManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * CartRepository - Single source of truth for cart data
 * Manages cart operations like adding, removing, updating items by interacting
 * with the backend API.
 */
public class CartRepository {
    private static final String TAG = "CartRepository";
    private static volatile CartRepository instance;
    private final ApiService apiService;
    private final PreferencesHelper preferencesHelper;
    private final LocalCartManager localCartManager;

    private CartRepository(Context context) {
        apiService = RetrofitClient.getApiService();
        preferencesHelper = new PreferencesHelper(context.getApplicationContext());
        localCartManager = new LocalCartManager(context.getApplicationContext());
    }

    public static CartRepository getInstance(Context context) {
        if (instance == null) {
            synchronized (CartRepository.class) {
                if (instance == null) {
                    instance = new CartRepository(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    /**
     * Get all items in the cart from the backend.
     *
     * @param callback Callback to handle the list of cart items or an error.
     */
    public void getCartItems(@NonNull CartItemsCallback callback) {
        String authToken = preferencesHelper.getAuthToken();
        if (authToken == null || authToken.isEmpty()) {
            Log.d(TAG, "getCartItems: User not logged in, using local cart");
            List<CartItem> localItems = localCartManager.getCartItems();
            callback.onCartItemsLoaded(localItems);
            return;
        }

        Log.d(TAG, "getCartItems: Fetching cart with JWT authentication");
        apiService.getCart().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<CartItem>>> call,
                    @NonNull Response<ApiResponse<List<CartItem>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Log.d(TAG, "getCartItems onSuccess: Received "
                            + (response.body().getData() != null ? response.body().getData().size() : 0) + " items.");
                    callback.onCartItemsLoaded(response.body().getData());
                } else {
                    String errorMsg = "Failed to load cart items.";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMsg = response.body().getMessage();
                    } else if (response.errorBody() != null) {
                        try {
                            errorMsg = response.errorBody().string();
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error body", e);
                        }
                    }
                    Log.e(TAG, "getCartItems failed: " + errorMsg + " Code: " + response.code());
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<CartItem>>> call, @NonNull Throwable t) {
                Log.e(TAG, "getCartItems network error", t);
                callback.onError("Network error while fetching cart: " + t.getMessage());
            }
        });
    }

    /**
     * Add a product to the cart on the backend.
     *
     * @param product  Product to add (used for product ID).
     * @param quantity Quantity to add.
     * @param callback Callback to handle the updated list of cart items or an
     *                 error.
     */
    public void addToCart(Product product, int quantity, @NonNull CartItemsCallback callback) {
        String authToken = preferencesHelper.getAuthToken();
        if (authToken == null || authToken.isEmpty()) {
            Log.d(TAG, "addToCart: User not logged in, using local cart");
            if (product == null || product.getId() == null || quantity <= 0) {
                Log.w(TAG, "addToCart: Invalid product or quantity.");
                callback.onError("Invalid product or quantity.");
                return;
            }

            try {
                // Log product details before adding to cart
                Log.d(TAG, "Product details before adding to cart: " +
                        "ID=" + product.getId() + ", " +
                        "Name=" + product.getName() + ", " +
                        "Price=" + product.getPrice());

                // Add to local cart
                localCartManager.addToCart(product, quantity);

                // Get updated cart and verify the item was added
                List<CartItem> updatedCart = localCartManager.getCartItems();
                boolean found = false;
                for (CartItem item : updatedCart) {
                    if (item != null && item.getProduct() != null &&
                            product.getId().equals(item.getProduct().getId())) {
                        found = true;
                        Log.d(TAG, "Product verified in cart: " + item.getProduct().getName() +
                                " with quantity " + item.getQuantity());
                        break;
                    }
                }

                if (!found) {
                    Log.e(TAG, "Failed to find product in cart after adding it!");
                }

                // Return updated cart
                callback.onCartItemsLoaded(updatedCart);
            } catch (Exception e) {
                Log.e(TAG, "Exception while adding to local cart", e);
                callback.onError("Failed to add item to cart: " + e.getMessage());
            }
            return;
        }

        if (product == null || product.getId() == null || quantity <= 0) {
            Log.w(TAG, "addToCart: Invalid product or quantity.");
            callback.onError("Invalid product or quantity.");
            return;
        }
        Log.d(TAG, "addToCart: Adding productId: " + product.getId() + " quantity: " + quantity
                + " with JWT authentication");

        // Create CartAddRequest for the JWT-authenticated JSON endpoint
        com.coffeecorner.app.models.CartAddRequest cartAddRequest = new com.coffeecorner.app.models.CartAddRequest(
                product.getId(), quantity);

        // Use the JWT-authenticated addToCart endpoint with JSON body
        apiService.addToCart(cartAddRequest).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<CartItem>>> call,
                    @NonNull Response<ApiResponse<List<CartItem>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Log.d(TAG, "addToCart onSuccess: Item added. New cart size: "
                            + (response.body().getData() != null ? response.body().getData().size() : 0));
                    callback.onCartItemsLoaded(response.body().getData());
                } else {
                    String errorMsg = "Failed to add item to cart.";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMsg = response.body().getMessage();
                    } else if (response.errorBody() != null) {
                        try {
                            errorMsg = response.errorBody().string();
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error body", e);
                        }
                    }
                    Log.e(TAG, "addToCart failed: " + errorMsg + " Code: " + response.code());
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<CartItem>>> call, @NonNull Throwable t) {
                Log.e(TAG, "addToCart network error", t);
                callback.onError("Network error while adding to cart: " + t.getMessage());
            }
        });
    }

    /**
     * Remove an item from the cart on the backend.
     *
     * @param itemId   ID of the cart item (or product ID if API uses that).
     * @param callback Callback to handle the updated list of cart items or an
     *                 error.
     */
    public void removeFromCart(String itemId, @NonNull CartItemsCallback callback) {
        String authToken = preferencesHelper.getAuthToken();
        if (authToken == null || authToken.isEmpty()) {
            Log.d(TAG, "removeFromCart: User not logged in, using local cart");
            if (itemId == null || itemId.isEmpty()) {
                Log.w(TAG, "removeFromCart: Invalid item ID.");
                callback.onError("Invalid item ID.");
                return;
            }

            localCartManager.removeFromCart(itemId);
            List<CartItem> updatedCart = localCartManager.getCartItems();
            callback.onCartItemsLoaded(updatedCart);
            return;
        }

        if (itemId == null || itemId.isEmpty()) {
            Log.w(TAG, "removeFromCart: Invalid item ID.");
            callback.onError("Invalid item ID.");
            return;
        }

        Log.d(TAG, "removeFromCart: Removing itemId: " + itemId + " with JWT authentication");
        apiService.removeFromCart(itemId).enqueue(new Callback<ApiResponse<List<CartItem>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<CartItem>>> call,
                    @NonNull Response<ApiResponse<List<CartItem>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Log.d(TAG, "removeFromCart onSuccess: Item removed. New cart size: "
                            + (response.body().getData() != null ? response.body().getData().size() : 0));
                    callback.onCartItemsLoaded(response.body().getData());
                } else {
                    String errorMsg = "Failed to remove item from cart.";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMsg = response.body().getMessage();
                    } else if (response.errorBody() != null) {
                        try {
                            errorMsg = response.errorBody().string();
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error body", e);
                        }
                    }
                    Log.e(TAG, "removeFromCart failed: " + errorMsg + " Code: " + response.code());
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<CartItem>>> call, @NonNull Throwable t) {
                Log.e(TAG, "removeFromCart network error", t);
                callback.onError("Network error while removing from cart: " + t.getMessage());
            }
        });
    }

    /**
     * Update the quantity of an item in the cart on the backend.
     *
     * @param itemId   ID of the cart item (or product ID).
     * @param quantity New quantity.
     * @param callback Callback to handle the updated list of cart items or an
     *                 error.
     */
    public void updateQuantity(String itemId, int quantity, @NonNull CartItemsCallback callback) {
        String authToken = preferencesHelper.getAuthToken();
        if (authToken == null || authToken.isEmpty()) {
            Log.d(TAG, "updateQuantity: User not logged in, using local cart");
            if (itemId == null || itemId.isEmpty()) {
                Log.w(TAG, "updateQuantity: Invalid item ID.");
                callback.onError("Invalid item ID.");
                return;
            }

            if (quantity <= 0) {
                Log.d(TAG, "updateQuantity: Quantity is 0 or less, removing item: " + itemId);
                removeFromCart(itemId, callback);
                return;
            }

            localCartManager.updateQuantity(itemId, quantity);
            List<CartItem> updatedCart = localCartManager.getCartItems();
            callback.onCartItemsLoaded(updatedCart);
            return;
        }

        if (itemId == null || itemId.isEmpty()) {
            Log.w(TAG, "updateQuantity: Invalid item ID.");
            callback.onError("Invalid item ID.");
            return;
        }

        if (quantity <= 0) {
            Log.d(TAG, "updateQuantity: Quantity is 0 or less, removing item: " + itemId);
            removeFromCart(itemId, callback);
            return;
        }

        // Create a CartItem object for the JWT endpoint
        CartItem cartItem = new CartItem();
        cartItem.setId(itemId);
        cartItem.setQuantity(quantity);

        Log.d(TAG, "updateQuantity: Updating itemId: " + itemId + " to quantity: " + quantity
                + " with JWT authentication");
        apiService.updateCartItem(cartItem).enqueue(new Callback<ApiResponse<List<CartItem>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<CartItem>>> call,
                    @NonNull Response<ApiResponse<List<CartItem>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Log.d(TAG, "updateQuantity onSuccess: Item updated. New cart size: "
                            + (response.body().getData() != null ? response.body().getData().size() : 0));
                    callback.onCartItemsLoaded(response.body().getData());
                } else {
                    String errorMsg = "Failed to update cart item quantity.";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMsg = response.body().getMessage();
                    } else if (response.errorBody() != null) {
                        try {
                            errorMsg = response.errorBody().string();
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error body", e);
                        }
                    }
                    Log.e(TAG, "updateQuantity failed: " + errorMsg + " Code: " + response.code());
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<CartItem>>> call, @NonNull Throwable t) {
                Log.e(TAG, "updateQuantity network error", t);
                callback.onError("Network error while updating cart quantity: " + t.getMessage());
            }
        });
    }

    /**
     * Update quantity of an item in the cart
     *
     * @param itemId   ID of the cart item
     * @param quantity New quantity
     * @param callback Callback to handle result
     */
    public void updateCartItemQuantity(String itemId, int quantity, @NonNull CartOperationCallback callback) {
        String authToken = preferencesHelper.getAuthToken();
        if (authToken == null || authToken.isEmpty()) {
            Log.w(TAG, "updateCartItemQuantity: User not logged in.");
            callback.onError("User not logged in. Cannot update cart.");
            return;
        }
        if (itemId == null || itemId.isEmpty() || quantity < 0) {
            Log.w(TAG, "updateCartItemQuantity: Invalid item ID or quantity.");
            callback.onError("Invalid item ID or quantity.");
            return;
        }

        // Create a CartItem object for the JWT endpoint
        CartItem cartItem = new CartItem();
        cartItem.setId(itemId);
        cartItem.setQuantity(quantity);

        Log.d(TAG, "updateCartItemQuantity: Updating itemId: " + itemId + " quantity: " + quantity
                + " with JWT authentication");
        apiService.updateCartItem(cartItem).enqueue(new Callback<ApiResponse<List<CartItem>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<CartItem>>> call,
                    @NonNull Response<ApiResponse<List<CartItem>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Log.d(TAG, "updateCartItemQuantity onSuccess: Item quantity updated");
                    callback.onSuccess("Item quantity updated successfully.");
                } else {
                    String errorMsg = "Failed to update item quantity.";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMsg = response.body().getMessage();
                    } else if (response.errorBody() != null) {
                        try {
                            errorMsg = response.errorBody().string();
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error body", e);
                        }
                    }
                    Log.e(TAG, "updateCartItemQuantity failed: " + errorMsg + " Code: " + response.code());
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<CartItem>>> call, @NonNull Throwable t) {
                Log.e(TAG, "updateCartItemQuantity network error", t);
                callback.onError("Network error while updating item quantity: " + t.getMessage());
            }
        });
    }

    /**
     * Clear all items from the cart on the backend.
     *
     * @param callback Callback to handle success or error.
     */
    public void clearCart(@NonNull CartOperationCallback callback) {
        String authToken = preferencesHelper.getAuthToken();
        if (authToken == null || authToken.isEmpty()) {
            Log.d(TAG, "clearCart: User not logged in, clearing local cart");
            localCartManager.clearCart();
            callback.onSuccess("Cart cleared successfully.");
            return;
        }

        Log.d(TAG, "clearCart: Clearing cart with JWT authentication");
        apiService.clearCart().enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Void>> call,
                    @NonNull Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getMessage()); // Or a generic success message
                } else {
                    String errorMsg = "Failed to clear cart";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMsg = response.body().getMessage();
                    } else if (response.errorBody() != null) {
                        try {
                            errorMsg = response.errorBody().string();
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error body", e);
                        }
                    }
                    Log.e(TAG, "clearCart failed: " + errorMsg + " Code: " + response.code());
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                Log.e(TAG, "clearCart network error", t);
                callback.onError("Network error while clearing cart: " + t.getMessage());
            }
        });
    }

    /**
     * Sync local cart with server when user logs in
     * 
     * @param callback Callback to handle result
     */
    public void syncLocalCartWithServer(@NonNull CartItemsCallback callback) {
        String authToken = preferencesHelper.getAuthToken();
        if (authToken == null || authToken.isEmpty()) {
            Log.w(TAG, "syncLocalCartWithServer: User not logged in.");
            callback.onError("User not logged in. Cannot sync cart.");
            return;
        }

        List<CartItem> localItems = localCartManager.getCartItems();
        if (localItems.isEmpty()) {
            Log.d(TAG, "syncLocalCartWithServer: No local items to sync");
            getCartItems(callback);
            return;
        }
        Log.d(TAG, "syncLocalCartWithServer: Syncing " + localItems.size() + " local items with JWT authentication");
        for (CartItem item : localItems) {
            Product product = item.getProduct();
            int quantity = item.getQuantity();

            // Create CartAddRequest for the JWT-authenticated JSON endpoint
            com.coffeecorner.app.models.CartAddRequest cartAddRequest = new com.coffeecorner.app.models.CartAddRequest(
                    product.getId(), quantity);

            apiService.addToCart(cartAddRequest)
                    .enqueue(new Callback<ApiResponse<List<CartItem>>>() {
                        @Override
                        public void onResponse(@NonNull Call<ApiResponse<List<CartItem>>> call,
                                @NonNull Response<ApiResponse<List<CartItem>>> response) {
                            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                Log.d(TAG, "syncLocalCartWithServer: Item synced successfully");
                            } else {
                                Log.w(TAG, "syncLocalCartWithServer: Failed to sync item: " + product.getName());
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<ApiResponse<List<CartItem>>> call, @NonNull Throwable t) {
                            Log.e(TAG, "syncLocalCartWithServer: Network error syncing item", t);
                        }
                    });
        }
        localCartManager.clearCart();

        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> getCartItems(callback), 2000);
    }

    /**
     * Get cart item count for badge display
     * 
     * @return Number of items in cart
     */
    public int getCartItemCount() {
        String authToken = preferencesHelper.getAuthToken();
        if (authToken == null || authToken.isEmpty()) {
            return localCartManager.getCartItemCount();
        }

        return 0;
    }

    /**
     * Get cart total for quick display
     * 
     * @return Total cart value
     */
    public double getCartTotal() {
        String authToken = preferencesHelper.getAuthToken();
        if (authToken == null || authToken.isEmpty()) {
            return localCartManager.getCartTotal();
        }

        return 0.0;
    }

    /**
     * Callback interface for operations that return a list of cart items.
     */
    public interface CartItemsCallback {
        void onCartItemsLoaded(List<CartItem> cartItems);

        void onError(String errorMessage);
    }

    /**
     * Callback interface for cart modification operations (like clear) that might
     * not return a list.
     */
    public interface CartModificationCallback {
        void onSuccess(String message);

        void onError(String errorMessage);
    }

    /**
     * Callback interface for generic cart operations (add, remove, update) that
     * return no data.
     */
    public interface CartOperationCallback {
        void onSuccess(String successMsg); // Changed from void onSuccess();

        void onError(String errorMessage);
    }
}
