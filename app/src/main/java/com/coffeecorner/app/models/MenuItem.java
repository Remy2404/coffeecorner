package com.coffeecorner.app.models;

public class MenuItem {
    private String id;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private String category;

    public MenuItem(String id, String name, String description, double price, String imageUrl, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuItem menuItem = (MenuItem) o;
        return Double.compare(menuItem.price, price) == 0 &&
               id.equals(menuItem.id) &&
               name.equals(menuItem.name) &&
               description.equals(menuItem.description) &&
               imageUrl.equals(menuItem.imageUrl) &&
               category.equals(menuItem.category);
    }

    @Override
    public int hashCode() {
        // Simple hash code implementation, can be improved
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + Double.hashCode(price);
        result = 31 * result + imageUrl.hashCode();
        result = 31 * result + category.hashCode();
        return result;
    }

    // TODO: Implement equals() and hashCode() for DiffUtil in Adapter
} 