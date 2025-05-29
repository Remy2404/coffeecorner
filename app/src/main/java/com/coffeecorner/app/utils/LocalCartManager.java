package com.coffeecorner.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.coffeecorner.app.models.CartItem;
import com.coffeecorner.app.models.Product;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
        // Create a Gson instance that correctly handles serialization and
        // deserialization
        gson = new GsonBuilder()
                .setLenient()
                .create();
    }

    public List<CartItem> getCartItems() {
        String cartJson = preferences.getString(KEY_CART_ITEMS, null);
        Log.d(TAG, "Raw cart data from SharedPreferences: " + cartJson);

        if (cartJson != null && !cartJson.isEmpty()) {
            try {
                Type type = new TypeToken<List<CartItem>>() {
                }.getType();
                List<CartItem> items = gson.fromJson(cartJson, type);
                if (items != null) {
                    Log.d(TAG, "Successfully loaded " + items.size() + " cart items.");
                    // Check for data integrity
                    List<CartItem> validItems = new ArrayList<>();
                    for (CartItem item : items) {
                        if (item != null && item.getProduct() != null && item.getProduct().getId() != null) {
                            validItems.add(item);
                        } else {
                            Log.w(TAG, "Removed invalid cart item during load");
                        }
                    }
                    return validItems;
                } else {
                    Log.w(TAG, "Deserialized cart items is null");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error deserializing cart items", e);
            }
        } else {
            Log.d(TAG, "Cart is empty in SharedPreferences");
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
            Log.d(TAG, "Current cart has " + cartItems.size() + " items");

            // Find existing item with the same product
            CartItem existingItem = null;
            for (CartItem item : cartItems) {
                if (item.getProduct() != null &&
                        item.getProduct().getId() != null &&
                        product.getId() != null &&
                        item.getProduct().getId().equals(product.getId()) &&
                        ((item.getSize() == null && "Medium".equals("Medium")) || // Default size is Medium
                                (item.getSize() != null && item.getSize().equals("Medium")))
                        &&
                        ((item.getMilkOption() == null && "Whole Milk".equals("Whole Milk")) || // Default milk is Whole
                                (item.getMilkOption() != null && item.getMilkOption().equals("Whole Milk")))) {
                    existingItem = item;
                    break;
                }
            }

            if (existingItem != null) {
                Log.d(TAG, "Found existing item for product: " + product.getName() + ", updating quantity from "
                        + existingItem.getQuantity() + " to " + (existingItem.getQuantity() + quantity));
                existingItem.setQuantity(existingItem.getQuantity() + quantity);
            } else {
                CartItem newItem = new CartItem(product, quantity);
                Log.d(TAG, "Adding new cart item: " + product.getName() + " x" + quantity);
                cartItems.add(newItem);
            }

            saveCartItems(cartItems);
            Log.d(TAG, "Added " + quantity + " of " + product.getName() + " to local cart");
        } catch (Exception e) {
            Log.e(TAG, "Error adding product to cart", e);
        }
    }

    public void addToCart(CartItem cartItem) {
        if (cartItem == null || cartItem.getProduct() == null) {
            Log.w(TAG, "Attempted to add null cart item to cart");
            return;
        }

        try {
            List<CartItem> cartItems = getCartItems();

            // Check if item already exists in cart
            boolean found = false;
            for (CartItem existingItem : cartItems) {
                if (existingItem == null || existingItem.getProduct() == null)
                    continue;

                String existingProductId = existingItem.getProductId();
                String newProductId = cartItem.getProductId();
                String existingSize = existingItem.getSize();
                String newSize = cartItem.getSize();
                String existingMilk = existingItem.getMilkOption();
                String newMilk = cartItem.getMilkOption();

                if (existingProductId != null && existingProductId.equals(newProductId) &&
                        ((existingSize == null && newSize == null) ||
                                (existingSize != null && existingSize.equals(newSize)))
                        &&
                        ((existingMilk == null && newMilk == null) ||
                                (existingMilk != null && existingMilk.equals(newMilk)))) {
                    // Update quantity
                    Log.d(TAG, "Found existing item, updating quantity: " + existingItem.getQuantity() +
                            " + " + cartItem.getQuantity() + " = "
                            + (existingItem.getQuantity() + cartItem.getQuantity()));
                    existingItem.setQuantity(existingItem.getQuantity() + cartItem.getQuantity());
                    found = true;
                    break;
                }
            }

            // If not found, add new item
            if (!found) {
                Log.d(TAG, "Adding new item to cart: " + cartItem.getProduct().getName());
                cartItems.add(cartItem);
            }

            saveCartItems(cartItems);
        } catch (Exception e) {
            Log.e(TAG, "Error adding cart item", e);
        }
    }

    public synchronized void removeFromCart(String productId) {
        if (productId == null) {
            Log.w(TAG, "Attempted to remove null productId from cart");
            return;
        }

        try {
            List<CartItem> cartItems = getCartItems();
            List<CartItem> updatedItems = new ArrayList<>();

            for (CartItem item : cartItems) {
                if (item != null && item.getProduct() != null &&
                        item.getProductId() != null && !item.getProductId().equals(productId)) {
                    updatedItems.add(item);
                }
            }

            saveCartItems(updatedItems);
            Log.d(TAG, "Removed item " + productId + " from local cart");
        } catch (Exception e) {
            Log.e(TAG, "Error removing item from cart", e);
        }
    }

    public synchronized void updateQuantity(String productId, int quantity) {
        if (productId == null) {
            Log.w(TAG, "Attempted to update quantity for null productId");
            return;
        }

        try {
            List<CartItem> cartItems = getCartItems();

            if (quantity <= 0) {
                Log.d(TAG, "Quantity is 0 or less, removing item: " + productId);
                removeFromCart(productId);
                return;
            }

            boolean itemFound = false;
            for (CartItem item : cartItems) {
                if (item != null && item.getProduct() != null &&
                        item.getProductId() != null && item.getProductId().equals(productId)) {
                    Log.d(TAG, "Updating quantity for " + item.getProduct().getName() + " from " +
                            item.getQuantity() + " to " + quantity);
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
        if (cartItems == null) {
            Log.w(TAG, "Attempted to save null cartItems list");
            return;
        }

        // Filter out any null or invalid items before serializing
        List<CartItem> validItems = new ArrayList<>();
        for (CartItem item : cartItems) {
            if (item != null && item.getProduct() != null && item.getProduct().getId() != null) {
                validItems.add(item);
            }
        }

        try {
            String cartJson = gson.toJson(validItems);
            Log.d(TAG, "Saving " + validItems.size() + " items to SharedPreferences");

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_CART_ITEMS, cartJson);
            boolean success = editor.commit(); // Using commit to ensure the write is synchronous

            if (success) {
                Log.d(TAG, "Successfully saved cart items to SharedPreferences");
                // Verify save was successful by reading back
                String savedJson = preferences.getString(KEY_CART_ITEMS, null);
                if (savedJson == null || !savedJson.equals(cartJson)) {
                    Log.w(TAG, "Verification failed - saved data doesn't match what we tried to save");
                }
            } else {
                Log.e(TAG, "Failed to save cart items to SharedPreferences");
            }
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
