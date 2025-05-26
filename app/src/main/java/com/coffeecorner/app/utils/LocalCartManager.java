package com.coffeecorner.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.coffeecorner.app.models.CartItem;
import com.coffeecorner.app.models.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LocalCartManager {
    private static final String TAG = "LocalCartManager";
    private static final String CART_PREFS = "LocalCartPrefs";
    private static final String KEY_CART_ITEMS = "cart_items";

    private final SharedPreferences preferences;
    private final Gson gson;

    public LocalCartManager(Context context) {
        preferences = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public List<CartItem> getCartItems() {
        String cartJson = preferences.getString(KEY_CART_ITEMS, null);
        if (cartJson != null) {
            Type type = new TypeToken<List<CartItem>>() {
            }.getType();
            List<CartItem> items = gson.fromJson(cartJson, type);
            return items != null ? items : new ArrayList<>();
        }
        return new ArrayList<>();
    }

    public synchronized void addToCart(Product product, int quantity) {
        if (product == null) {
            Log.w(TAG, "Attempted to add null product to cart");
            return;
        }

        try {
            List<CartItem> cartItems = getCartItems();

            CartItem existingItem = null;
            for (CartItem item : cartItems) {
                if (item.getProduct() != null &&
                        item.getProduct().getId() != null &&
                        product.getId() != null &&
                        item.getProduct().getId().equals(product.getId())) {
                    existingItem = item;
                    break;
                }
            }

            if (existingItem != null) {
                existingItem.setQuantity(existingItem.getQuantity() + quantity);
            } else {
                CartItem newItem = new CartItem(product, quantity);
                cartItems.add(newItem);
            }

            saveCartItems(cartItems);
            Log.d(TAG, "Added " + quantity + " of " + product.getName() + " to local cart");
        } catch (Exception e) {
            Log.e(TAG, "Error adding product to cart", e);
        }
    }

    public void addToCart(CartItem cartItem) {
        List<CartItem> cartItems = getCartItems();

        // Check if item already exists in cart
        boolean found = false;
        for (CartItem existingItem : cartItems) {
            String existingProductId = existingItem.getProductId();
            String newProductId = cartItem.getProductId();
            String existingSize = existingItem.getSize();
            String newSize = cartItem.getSize();
            String existingMilk = existingItem.getMilkOption();
            String newMilk = cartItem.getMilkOption();

            if (existingProductId != null && existingProductId.equals(newProductId) &&
                    existingSize != null && existingSize.equals(newSize) &&
                    existingMilk != null && existingMilk.equals(newMilk)) {
                // Update quantity
                existingItem.setQuantity(existingItem.getQuantity() + cartItem.getQuantity());
                found = true;
                break;
            }
        }

        // If not found, add new item
        if (!found) {
            cartItems.add(cartItem);
        }

        saveCartItems(cartItems);
    }

    public synchronized void removeFromCart(String productId) {
        if (productId == null) {
            Log.w(TAG, "Attempted to remove null productId from cart");
            return;
        }

        List<CartItem> cartItems = getCartItems();
        List<CartItem> updatedItems = new ArrayList<>();

        for (CartItem item : cartItems) {
            if (item.getProductId() == null || !item.getProductId().equals(productId)) {
                updatedItems.add(item);
            }
        }

        saveCartItems(updatedItems);
        Log.d(TAG, "Removed item " + productId + " from local cart");
    }

    public synchronized void updateQuantity(String productId, int quantity) {
        if (productId == null) {
            Log.w(TAG, "Attempted to update quantity for null productId");
            return;
        }

        try {
            List<CartItem> cartItems = getCartItems();

            if (quantity <= 0) {
                removeFromCart(productId);
                return;
            }

            boolean itemFound = false;
            for (CartItem item : cartItems) {
                if (item.getProductId() != null && item.getProductId().equals(productId)) {
                    item.setQuantity(quantity);
                    itemFound = true;
                    break;
                }
            }

            if (itemFound) {
                saveCartItems(cartItems);
                Log.d(TAG, "Updated item " + productId + " quantity to " + quantity);
            } else {
                Log.w(TAG, "Item with productId " + productId + " not found in cart");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating quantity for productId: " + productId, e);
        }
    }

    public void clearCart() {
        preferences.edit().remove(KEY_CART_ITEMS).apply();
        Log.d(TAG, "Cleared local cart");
    }

    public int getCartItemCount() {
        List<CartItem> items = getCartItems();
        int count = 0;
        for (CartItem item : items) {
            count += item.getQuantity();
        }
        return count;
    }

    public double getCartTotal() {
        List<CartItem> items = getCartItems();
        double total = 0.0;
        for (CartItem item : items) {
            total += item.getTotalPrice(); // Use getTotalPrice() which includes quantity
        }
        return total;
    }

    private synchronized void saveCartItems(List<CartItem> cartItems) {
        try {
            String cartJson = gson.toJson(cartItems);
            preferences.edit().putString(KEY_CART_ITEMS, cartJson).apply();
        } catch (Exception e) {
            Log.e(TAG, "Error saving cart items", e);
        }
    }

    public boolean hasItems() {
        return !getCartItems().isEmpty();
    }

    /**
     * Sync local cart items to server when user logs in
     * 
     * @return List of cart items to be synced
     */
    public List<CartItem> getItemsForSync() {
        return getCartItems();
    }
}
