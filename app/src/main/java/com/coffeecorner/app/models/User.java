package com.coffeecorner.app.models;

import java.util.Date;

public class User {
    private String uid;
    private String id; // Added for Supabase compatibility
    private String fullName;
    private String email;
    private String phoneNumber;
    private String phone; // Added for compatibility
    private String avatarUrl;
    private String profileImageUrl; // Added for compatibility
    private String gender;
    private Date dateOfBirth;
    private int points;
    private int loyaltyPoints; // Added for compatibility
    private String membershipTier;
    private long createdAt;

    public User() {
        // Default constructor required for Firebase
    }

    public User(String uid, String fullName, String email, String phoneNumber, String avatarUrl) {
        this.uid = uid;
        this.id = uid; // Initialize id with uid for compatibility
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.phone = phoneNumber; // Initialize phone with phoneNumber for compatibility
        this.avatarUrl = avatarUrl;
        this.profileImageUrl = avatarUrl; // Initialize profileImageUrl with avatarUrl for compatibility
        this.points = 0;
        this.loyaltyPoints = 0;
        this.membershipTier = "Bronze";
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    // ID getter and setter for Supabase compatibility
    public String getId() {
        return id != null ? id : uid;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // Name getter and setter (aliases to fullName for compatibility)
    public String getName() {
        return fullName;
    }

    public void setName(String name) {
        this.fullName = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.phone = phoneNumber; // Keep both in sync
    }

    // Phone getter and setter (aliases to phoneNumber for compatibility)
    public String getPhone() {
        return phone != null ? phone : phoneNumber;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        this.phoneNumber = phone; // Keep both in sync
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        this.profileImageUrl = avatarUrl; // Keep both in sync
    }

    // ProfileImageUrl getter and setter (aliases to avatarUrl for compatibility)
    public String getProfileImageUrl() {
        return profileImageUrl != null ? profileImageUrl : avatarUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
        this.avatarUrl = profileImageUrl; // Keep both in sync
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
        this.loyaltyPoints = points; // Keep both in sync
    }

    // LoyaltyPoints getter and setter (aliases to points for compatibility)
    public int getLoyaltyPoints() {
        return loyaltyPoints != 0 ? loyaltyPoints : points;
    }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
        this.points = loyaltyPoints; // Keep both in sync
    }

    public String getMembershipTier() {
        return membershipTier;
    }

    public void setMembershipTier(String membershipTier) {
        this.membershipTier = membershipTier;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
