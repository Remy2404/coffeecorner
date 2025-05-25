package com.coffeecorner.app.models;

public class CartItem {
    private Product product;
    private int quantity;
    private String size;
    private String milkOption;
    private double extraCharge;
    private String temperature;
    private String customizations;

    // Default constructor needed by CartManager
    public CartItem() {
        this.quantity = 1;
        this.extraCharge = 0.0;
    }

    // Constructors
    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.size = "Medium";
        this.milkOption = "Whole Milk";
        this.extraCharge = 0.0;
        this.temperature = "Hot";
        this.customizations = "";
    }

    public CartItem(Product product, int quantity, String size, String milkOption) {
        this.product = product;
        this.quantity = quantity;
        this.size = size;
        this.milkOption = milkOption;
        this.extraCharge = calculateExtraCharge(size, milkOption); // Keep internal calculation for this constructor
        this.temperature = "Hot";
        this.customizations = "";
    }

    public CartItem(Product product, int quantity, String size, String milkOption, double extraCharge) {
        this.product = product;
        this.quantity = quantity;
        this.size = size;
        this.milkOption = milkOption;
        this.extraCharge = extraCharge;
        this.temperature = "Hot";
        this.customizations = "";
    }

    public CartItem(Product product, int quantity, String size, String milkOption, String temperature,
            String customizations) {
        this.product = product;
        this.quantity = quantity;
        this.size = size;
        this.milkOption = milkOption;
        this.temperature = temperature;
        this.customizations = customizations;
        this.extraCharge = calculateExtraCharge(size, milkOption);
    }

    // Getters and Setters
    public Product getProduct() {
        return product;
    }

    /**
     * Get the product ID from the associated product
     * 
     * @return product ID or null if product is null
     */
    public String getProductId() {
        return product != null ? product.getId() : null;
    }

    /**
     * Get the price from the associated product
     * 
     * @return product price or 0 if product is null
     */
    public double getPrice() {
        return product != null ? product.getPrice() : 0.0;
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

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getCustomizations() {
        return customizations;
    }

    public void setCustomizations(String customizations) {
        this.customizations = customizations;
    }

    public double getTotalPrice() {
        if (product == null) {
            android.util.Log.w("CartItem", "getTotalPrice: Product is null, returning 0");
            return 0.0;
        }
        return (product.getPrice() + extraCharge) * quantity;
    }

    private double calculateExtraCharge(String size, String milkOption) {
        double charge = 0.0;
        if (size != null && !size.equalsIgnoreCase("Medium") && !size.equalsIgnoreCase("Regular")) { // Assuming
                                                                                                     // "Regular" is
                                                                                                     // also a base
            charge += 0.50; // Example: charge for non-medium size
        }
        if (milkOption != null && !milkOption.equalsIgnoreCase("Whole Milk") && !milkOption.equalsIgnoreCase("None")) { // Assuming
                                                                                                                        // "None"
                                                                                                                        // is
                                                                                                                        // also
                                                                                                                        // a
                                                                                                                        // base
            charge += 0.75; // Example: charge for special milk
        }
        return charge;
    }
}
