package com.coffeecorner.app.models;

public class CartItem {
    private Product product;
    private int quantity;
    private String specialInstructions;
    private String selectedSize;
    
    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }
    
    public CartItem(Product product, int quantity, String selectedSize, String specialInstructions) {
        this.product = product;
        this.quantity = quantity;
        this.selectedSize = selectedSize;
        this.specialInstructions = specialInstructions;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public String getSpecialInstructions() {
        return specialInstructions;
    }
    
    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }
    
    public String getSelectedSize() {
        return selectedSize;
    }
    
    public void setSelectedSize(String selectedSize) {
        this.selectedSize = selectedSize;
    }
    
    public double getPrice() {
        // Apply size price adjustments if needed
        double basePrice = product.getPrice();
        
        if (selectedSize != null) {
            switch (selectedSize) {
                case "Medium":
                    basePrice += 0.5;
                    break;
                case "Large":
                    basePrice += 1.0;
                    break;
                case "Extra Large":
                    basePrice += 1.5;
                    break;
            }
        }
        
        return basePrice;
    }
    
    public double getTotalPrice() {
        return getPrice() * quantity;
    }
}
