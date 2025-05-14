package com.coffeecorner.app.models;

public class User {
    private String uid;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private int points;
    private String membershipTier;
    private long createdAt;

    public User() {
        // Default constructor required for Firebase
    }

    public User(String uid, String fullName, String email, String phoneNumber, String avatarUrl) {
        this.uid = uid;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.avatarUrl = avatarUrl;
        this.points = 0;
        this.membershipTier = "Bronze";
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public String getMembershipTier() { return membershipTier; }
    public void setMembershipTier(String membershipTier) { this.membershipTier = membershipTier; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
