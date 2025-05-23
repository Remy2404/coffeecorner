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

    private CartRepository(Context context) {
        apiService = RetrofitClient.getApiService();
        preferencesHelper = new PreferencesHelper(context.getApplicationContext());
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
        String userId = preferencesHelper.getUserId();
        if (userId == null || userId.isEmpty()) {
            Log.w(TAG, "getCartItems: User not logged in.");
            callback.onError("User not logged in. Please login to view your cart.");
            return;
        }

        Log.d(TAG, "getCartItems: Fetching cart for userId: " + userId);
        apiService.getCart(userId).enqueue(new Callback<ApiResponse<List<CartItem>>>() {
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
        String userId = preferencesHelper.getUserId();
        if (userId == null || userId.isEmpty()) {
            Log.w(TAG, "addToCart: User not logged in.");
            callback.onError("User not logged in. Cannot add to cart.");
            return;
        }
        if (product == null || product.getId() == null || quantity <= 0) {
            Log.w(TAG, "addToCart: Invalid product or quantity.");
            callback.onError("Invalid product or quantity.");
            return;
        }

        Log.d(TAG, "addToCart: Adding productId: " + product.getId() + " quantity: " + quantity + " for userId: "
                + userId);
        apiService.addToCart(userId, product.getId(), quantity).enqueue(new Callback<ApiResponse<List<CartItem>>>() {
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
        String userId = preferencesHelper.getUserId();
        if (userId == null || userId.isEmpty()) {
            Log.w(TAG, "removeFromCart: User not logged in.");
            callback.onError("User not logged in. Cannot remove from cart.");
            return;
        }
        if (itemId == null || itemId.isEmpty()) {
            Log.w(TAG, "removeFromCart: Invalid item ID.");
            callback.onError("Invalid item ID.");
            return;
        }

        Log.d(TAG, "removeFromCart: Removing itemId: " + itemId + " for userId: " + userId);
        apiService.removeFromCart(userId, itemId).enqueue(new Callback<ApiResponse<List<CartItem>>>() {
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
        String userId = preferencesHelper.getUserId();
        if (userId == null || userId.isEmpty()) {
            Log.w(TAG, "updateQuantity: User not logged in.");
            callback.onError("User not logged in. Cannot update cart quantity.");
            return;
        }
        if (itemId == null || itemId.isEmpty()) {
            Log.w(TAG, "updateQuantity: Invalid item ID.");
            callback.onError("Invalid item ID.");
            return;
        }

        if (quantity <= 0) {
            Log.d(TAG, "updateQuantity: Quantity is 0 or less, removing item: " + itemId);
            removeFromCart(itemId, callback); // If quantity is 0, remove the item
            return;
        }

        Log.d(TAG,
                "updateQuantity: Updating itemId: " + itemId + " to quantity: " + quantity + " for userId: " + userId);
        apiService.updateCartItem(userId, itemId, quantity).enqueue(new Callback<ApiResponse<List<CartItem>>>() {
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
        String userId = preferencesHelper.getUserId();
        if (userId == null || userId.isEmpty()) {
            Log.w(TAG, "updateCartItemQuantity: User not logged in.");
            callback.onError("User not logged in. Cannot update cart.");
            return;
        }
        if (itemId == null || itemId.isEmpty() || quantity < 0) {
            Log.w(TAG, "updateCartItemQuantity: Invalid item ID or quantity.");
            callback.onError("Invalid item ID or quantity.");
            return;
        }

        Log.d(TAG, "updateCartItemQuantity: Updating itemId: " + itemId + " quantity: " + quantity + " for userId: "
                + userId);
        apiService.updateCartItemQuantity(userId, itemId, quantity).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Void>> call,
                    @NonNull Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Log.d(TAG, "updateCartItemQuantity onSuccess: Item quantity updated");
                    callback.onSuccess("Item quantity updated successfully."); // Provide message
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
            public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
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
    public void clearCart(@NonNull CartOperationCallback callback) { // Changed to CartOperationCallback
        String userId = preferencesHelper.getUserId();
        if (userId == null || userId.isEmpty()) {
            Log.w(TAG, "clearCart: User not logged in.");
            callback.onError("User not logged in. Cannot clear cart.");
            return;
        }

        Log.d(TAG, "clearCart: Clearing cart for userId: " + userId);
        apiService.clearCart(userId).enqueue(new Callback<ApiResponse<Void>>() { // Change to ApiResponse<Void>
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
