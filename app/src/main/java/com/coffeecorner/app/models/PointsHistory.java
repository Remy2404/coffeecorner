package com.coffeecorner.app.models;

public class PointsHistory {
    private String title;
    private String date;
    private int points;
    private boolean isEarned; // true for earned points, false for spent points

    public PointsHistory(String title, String date, int points, boolean isEarned) {
        this.title = title;
        this.date = date;
        this.points = points;
        this.isEarned = isEarned;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public boolean isEarned() {
        return isEarned;
    }

    public void setEarned(boolean earned) {
        isEarned = earned;
    }
    
    public String getFormattedPoints() {
        return (isEarned ? "+" : "-") + points;
    }
}
