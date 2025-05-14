package com.coffeecorner.app.models;

public class FeedbackItem {
    private String userName;
    private String content;
    private float rating;
    private String timeAgo;
    private String userPhotoUrl;

    public FeedbackItem(String userName, String content, float rating, String timeAgo, String userPhotoUrl) {
        this.userName = userName;
        this.content = content;
        this.rating = rating;
        this.timeAgo = timeAgo;
        this.userPhotoUrl = userPhotoUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public void setUserPhotoUrl(String userPhotoUrl) {
        this.userPhotoUrl = userPhotoUrl;
    }
}
