package com.coffeecorner.app.models;

public class User {
    public String id;
    public String full_name;
    public String email;
    public String phone;
    public String gender;
    public String profile_image_url;
    public String date_of_birth;

    public User() {}

    public User(String id, String full_name, String email, String phone, String gender, String profile_image_url, String date_of_birth) {
        this.id = id;
        this.full_name = full_name;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.profile_image_url = profile_image_url;
        this.date_of_birth = date_of_birth;
    }
}
