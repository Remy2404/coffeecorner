package com.coffeecorner.app.models;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class Product implements Cloneable {
    private String id;
    private String name;
    private String description;
    private double price;
    private String category;

    @SerializedName("image_url")
    private String imageUrl;

    private boolean isFeatured;
    private boolean isAvailable;
    private List<String> availableSizes;
    private List<String> availableAddons;
    private float rating;
    private int calories; // Added calories field

    // Working backup image URLs by category
    private static final String DEFAULT_IMAGE_URL = "https://images.unsplash.com/photo-1509042239860-f550ce710b93?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60";
    private static final String[] COFFEE_IMAGES = {
        "https://images.unsplash.com/photo-1509042239860-f550ce710b93?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60",
        "https://images.unsplash.com/photo-1517701550927-30cf4ba1dba5?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60",
        "https://images.unsplash.com/photo-1497935586351-b67a49e012bf?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60"
    };
    private static final String[] FOOD_IMAGES = {
        "https://images.unsplash.com/photo-1525351484163-7529414344d8?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60",
        "https://images.unsplash.com/photo-1506354666786-959d6d497f1a?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60"
    };
    private static final String[] DESSERT_IMAGES = {
        "https://images.unsplash.com/photo-1495147466023-ac5c588e2e94?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60",
        "https://images.unsplash.com/photo-1488477181946-6428a0291777?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60"
    };

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
        // Enhanced image URL handling with fallbacks
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return getFallbackImageUrl();
        }
        
        // Check if the URL ends with a typical image extension
        String lowerUrl = imageUrl.toLowerCase();
        if (!lowerUrl.endsWith(".jpg") && !lowerUrl.endsWith(".jpeg") && 
            !lowerUrl.endsWith(".png") && !lowerUrl.endsWith(".webp") && 
            !lowerUrl.endsWith(".gif") && !lowerUrl.startsWith("http")) {
                
            return getFallbackImageUrl();
        }
        
        return imageUrl;
    }
    
    /**
     * Get a fallback image URL based on the product category
     * @return A working image URL
     */
    private String getFallbackImageUrl() {
        if (category == null) {
            return DEFAULT_IMAGE_URL;
        }
        
        String lowercaseCategory = category.toLowerCase();
        
        if (lowercaseCategory.contains("coffee") || 
            lowercaseCategory.contains("espresso") ||
            lowercaseCategory.contains("latte") ||
            lowercaseCategory.contains("cappuccino") ||
            lowercaseCategory.contains("mocha")) {
            
            // Pick a random coffee image
            int index = Math.abs(name != null ? name.hashCode() : 0) % COFFEE_IMAGES.length;
            return COFFEE_IMAGES[index];
        } 
        else if (lowercaseCategory.contains("food") ||
                lowercaseCategory.contains("sandwich") || 
                lowercaseCategory.contains("breakfast") ||
                lowercaseCategory.contains("lunch")) {
            
            // Pick a random food image
            int index = Math.abs(name != null ? name.hashCode() : 0) % FOOD_IMAGES.length;
            return FOOD_IMAGES[index];
        }
        else if (lowercaseCategory.contains("dessert") ||
                lowercaseCategory.contains("cake") ||
                lowercaseCategory.contains("pastry") ||
                lowercaseCategory.contains("sweet")) {
            
            // Pick a random dessert image
            int index = Math.abs(name != null ? name.hashCode() : 0) % DESSERT_IMAGES.length;
            return DESSERT_IMAGES[index];
        }
        
        // Default fallback image
        return DEFAULT_IMAGE_URL;
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
