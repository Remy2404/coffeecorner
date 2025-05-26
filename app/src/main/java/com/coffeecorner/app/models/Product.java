package com.coffeecorner.app.models;

import java.util.ArrayList;
import java.util.List;

public class Product implements Cloneable {
    private String id;
    private String name;
    private String description;
    private double price;
    private String category;
    private String imageUrl;
    private boolean isFeatured;
    private boolean isAvailable;
    private List<String> availableSizes;
    private List<String> availableAddons;
    private float rating;
    private int calories; // Added calories field

    // Default constructor for GSON
    public Product() {
    }

    // Constructor for basic products
    public Product(String id, String name, String description, double price, String category, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
        this.isAvailable = true;
        this.availableSizes = new ArrayList<>();
        this.availableAddons = new ArrayList<>();
    }

    // Full constructor
    public Product(String id, String name, String description, double price, String category,
            String imageUrl, boolean isFeatured, boolean isAvailable,
            List<String> availableSizes, List<String> availableAddons) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
        this.isFeatured = isFeatured;
        this.isAvailable = isAvailable;
        this.availableSizes = availableSizes;
        this.availableAddons = availableAddons;
    }

    // Constructor for OrderTrackingActivity
    public Product(String id, String name, String description, double price, String imageUrl, String category,
            boolean isFeatured) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
        this.isFeatured = isFeatured;
        this.isAvailable = true; // Default value
        this.availableSizes = new ArrayList<>(); // Default value
        this.availableAddons = new ArrayList<>(); // Default value
    }

    // Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isFeatured() {
        return isFeatured;
    }

    public void setFeatured(boolean featured) {
        isFeatured = featured;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public List<String> getAvailableSizes() {
        return availableSizes;
    }

    public void setAvailableSizes(List<String> availableSizes) {
        this.availableSizes = availableSizes;
    }

    public List<String> getAvailableAddons() {
        return availableAddons;
    }

    public void setAvailableAddons(List<String> availableAddons) {
        this.availableAddons = availableAddons;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    // Added calories getter and setter
    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    // Helper methods

    public void addSize(String size) {
        if (availableSizes == null) {
            availableSizes = new ArrayList<>();
        }
        availableSizes.add(size);
    }

    public void addAddon(String addon) {
        if (availableAddons == null) {
            availableAddons = new ArrayList<>();
        }
        availableAddons.add(addon);
    }

    public boolean hasMultipleSizes() {
        return availableSizes != null && availableSizes.size() > 0;
    }

    public boolean hasAddons() {
        return availableAddons != null && availableAddons.size() > 0;
    }

    @Override
    public Product clone() {
        try {
            Product cloned = (Product) super.clone();

            // Deep copy of list fields
            if (this.availableSizes != null) {
                cloned.availableSizes = new ArrayList<>(this.availableSizes);
            }

            if (this.availableAddons != null) {
                cloned.availableAddons = new ArrayList<>(this.availableAddons);
            }

            return cloned;
        } catch (CloneNotSupportedException e) {
            // This should never happen since we implement Cloneable
            throw new RuntimeException("Clone not supported", e);
        }
    }
}
