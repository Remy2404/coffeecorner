package com.coffeecorner.app.models;

import com.google.gson.annotations.SerializedName;

/**
 * CartAddRequest - Request model for adding items to cart
 * Matches the backend CartItemAdd schema
 */
public class CartAddRequest {
    @SerializedName("product_id")
    private String productId;

    @SerializedName("quantity")
    private int quantity;

    // Default constructor
    public CartAddRequest() {
    }

    // Constructor
    public CartAddRequest(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    // Getters and Setters
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
