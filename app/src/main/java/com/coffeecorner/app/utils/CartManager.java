package com.coffeecorner.app.utils;

import com.coffeecorner.app.models.CartItem;
import com.coffeecorner.app.models.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class to manage the shopping cart operations.
 */
public class CartManager {
    private static CartManager instance;
    private List<CartItem> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    /**
     * Add a product to the cart.
     * 
     * @param product The product to add
     * @param quantity Quantity of the product
     * @param size Size of the product (e.g., Small, Medium, Large)
     * @param temperature Temperature of the product (e.g., Hot, Iced)
     * @param customizations Any customizations for the product
     * @return The added CartItem
     */
    public CartItem addToCart(Product product, int quantity, String size, 
                           String temperature, String customizations) {
        // Check if the product with the same options already exists in the cart
        for (CartItem item : cartItems) {
            if (item.getProduct().getId().equals(product.getId()) &&
                ((size == null && item.getSize() == null) || 
                 (size != null && size.equals(item.getSize()))) &&
                ((temperature == null && item.getTemperature() == null) || 
                 (temperature != null && temperature.equals(item.getTemperature()))) &&
                ((customizations == null && item.getCustomizations() == null) || 
                 (customizations != null && customizations.equals(item.getCustomizations())))) {
                
                // Product with same options exists, increment quantity
                item.setQuantity(item.getQuantity() + quantity);
                return item;
            }
        }
        
        // Create a new cart item
        CartItem newItem = new CartItem();
        newItem.setProduct(product);
        newItem.setQuantity(quantity);
        newItem.setSize(size);
        newItem.setTemperature(temperature);
        newItem.setCustomizations(customizations);
        
        cartItems.add(newItem);
        return newItem;
    }
    
    /**
     * Get all cart items.
     * 
     * @return List of cart items
     */
    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems); // Return a copy to prevent external modification
    }
    
    /**
     * Get the total number of items in the cart.
     * 
     * @return Total item count
     */
    public int getItemCount() {
        int count = 0;
        for (CartItem item : cartItems) {
            count += item.getQuantity();
        }
        return count;
    }
    
    /**
     * Get the total value of the cart.
     * 
     * @return Total cart value
     */
    public double getCartTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        return total;
    }
    
    /**
     * Remove an item from the cart.
     * 
     * @param cartItem The cart item to remove
     */
    public void removeFromCart(CartItem cartItem) {
        cartItems.remove(cartItem);
    }
    
    /**
     * Update the quantity of a cart item.
     * 
     * @param cartItem The cart item to update
     * @param newQuantity The new quantity
     */
    public void updateCartItemQuantity(CartItem cartItem, int newQuantity) {
        if (newQuantity <= 0) {
            removeFromCart(cartItem);
            return;
        }
        
        for (CartItem item : cartItems) {
            if (item.equals(cartItem)) {
                item.setQuantity(newQuantity);
                break;
            }
        }
    }
    
    /**
     * Clear all items from the cart.
     */
    public void clearCart() {
        cartItems.clear();
    }
}
