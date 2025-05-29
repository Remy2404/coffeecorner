package com.coffeecorner.app.models;

public class Address {
    private String id;
    private String type;
    private String title;
    private String fullAddress;
    private String street;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private boolean isDefault;
    private double latitude;
    private double longitude;

    public Address() {
    }

    public Address(String id, String type, String addressLine1, String addressLine2,
            String city, String state, String zipCode, String country, boolean isDefault) {
        this.id = id;
        this.type = type;
        this.title = type;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.country = country;
        this.isDefault = isDefault;
        this.fullAddress = buildFullAddress();
        this.street = addressLine1;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
        this.street = addressLine1;
        this.fullAddress = buildFullAddress();
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
        this.fullAddress = buildFullAddress();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    private String buildFullAddress() {
        StringBuilder address = new StringBuilder();
        if (addressLine1 != null && !addressLine1.isEmpty()) {
            address.append(addressLine1);
        }
        if (addressLine2 != null && !addressLine2.isEmpty()) {
            if (address.length() > 0)
                address.append(", ");
            address.append(addressLine2);
        }
        if (city != null && !city.isEmpty()) {
            if (address.length() > 0)
                address.append(", ");
            address.append(city);
        }
        if (state != null && !state.isEmpty()) {
            if (address.length() > 0)
                address.append(", ");
            address.append(state);
        }
        if (zipCode != null && !zipCode.isEmpty()) {
            if (address.length() > 0)
                address.append(" ");
            address.append(zipCode);
        }
        if (country != null && !country.isEmpty()) {
            if (address.length() > 0)
                address.append(", ");
            address.append(country);
        }
        return address.toString();
    }
}
