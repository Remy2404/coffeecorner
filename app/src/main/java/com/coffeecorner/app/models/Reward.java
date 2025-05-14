package com.coffeecorner.app.models;

public class Reward {
    private String title;
    private String description;
    private int pointsRequired;
    private int iconResourceId;

    public Reward(String title, String description, int pointsRequired, int iconResourceId) {
        this.title = title;
        this.description = description;
        this.pointsRequired = pointsRequired;
        this.iconResourceId = iconResourceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPointsRequired() {
        return pointsRequired;
    }

    public void setPointsRequired(int pointsRequired) {
        this.pointsRequired = pointsRequired;
    }

    public int getIconResourceId() {
        return iconResourceId;
    }

    public void setIconResourceId(int iconResourceId) {
        this.iconResourceId = iconResourceId;
    }
}
