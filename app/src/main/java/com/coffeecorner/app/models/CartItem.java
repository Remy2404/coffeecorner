package com.coffeecorner.app.models;

public class CartItem {
    private Product product;
    private int quantity;
    private String size; // e.g., "Medium", "Large"
    private String milkOption; // e.g., "Whole Milk", "Almond Milk"
    private double extraCharge; // For options like special milk, etc.

    // Constructors
    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.size = "Medium"; // Default size
        this.milkOption = "Whole Milk"; // Default milk
        this.extraCharge = 0.0;
    }

    public CartItem(Product product, int quantity, String size, String milkOption) {
        this.product = product;
        this.quantity = quantity;
        this.size = size;
        this.milkOption = milkOption;
        this.extraCharge = calculateExtraCharge(size, milkOption); // Keep internal calculation for this constructor
    }

    public CartItem(Product product, int quantity, String size, String milkOption, double extraCharge) {
        this.product = product;
        this.quantity = quantity;
        this.size = size;
        this.milkOption = milkOption;
        this.extraCharge = extraCharge;
    }

    // Getters and Setters
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

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getMilkOption() {
        return milkOption;
    }

    public void setMilkOption(String milkOption) {
        this.milkOption = milkOption;
    }

    public double getExtraCharge() {
        return extraCharge;
    }

    public void setExtraCharge(double extraCharge) {
        this.extraCharge = extraCharge;
    }

    public double getTotalPrice() {
        return (product.getPrice() + extraCharge) * quantity;
    }

    private double calculateExtraCharge(String size, String milkOption) {
        double charge = 0.0;
        if (size != null && !size.equalsIgnoreCase("Medium") && !size.equalsIgnoreCase("Regular")) { // Assuming "Regular" is also a base
            charge += 0.50; // Example: charge for non-medium size
        }
        if (milkOption != null && !milkOption.equalsIgnoreCase("Whole Milk") && !milkOption.equalsIgnoreCase("None")) { // Assuming "None" is also a base
            charge += 0.75; // Example: charge for special milk
        }
        return charge;
    }
}
