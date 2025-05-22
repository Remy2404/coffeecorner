package com.coffeecorner.app.models;

public class User {
    private String id;
    private String full_name;
    private String email;
    private String phone;
    private String gender;
    private String profile_image_url;
    private String date_of_birth;

    // Loyalty points for rewards
    private Integer loyaltyPoints;
    // Total number of orders
    private Integer totalOrders;
    // Member since date (e.g., registration date)
    private String memberSince;

    public User() {
    }

    public User(String id, String full_name, String email, String phone, String gender, String profile_image_url,
            String date_of_birth) {
        this.id = id;
        this.full_name = full_name;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.profile_image_url = profile_image_url;
        this.date_of_birth = date_of_birth;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return full_name;
    }

    public String getName() {
        return getFullName();
    }

    public void setFullName(String fullName) {
        this.full_name = fullName;
    }

    public void setName(String name) {
        setFullName(name);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhotoUrl() {
        return profile_image_url;
    }

    public void setPhotoUrl(String photoUrl) {
        this.profile_image_url = photoUrl;
    }

    public String getDateOfBirth() {
        return date_of_birth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.date_of_birth = dateOfBirth;
    }

    public Integer getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public Integer getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }

    public String getMemberSince() {
        return memberSince;
    }

    public void setMemberSince(String memberSince) {
        this.memberSince = memberSince;
    }
}
