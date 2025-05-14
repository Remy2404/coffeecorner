package com.coffeecorner.app.models;

public class Location {
    private String name;
    private String address;
    private String hours;
    private String phone;
    private String coordinates; // Lat,Long format

    public Location(String name, String address, String hours, String phone, String coordinates) {
        this.name = name;
        this.address = address;
        this.hours = hours;
        this.phone = phone;
        this.coordinates = coordinates;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }
}
