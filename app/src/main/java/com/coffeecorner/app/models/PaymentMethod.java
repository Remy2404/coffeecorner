package com.coffeecorner.app.models;

public class PaymentMethod {
    private String id;
    private String type;
    private String displayName;
    private String lastFourDigits;
    private int iconResourceId;
    private boolean isDefault;    public PaymentMethod() {
    }

    public PaymentMethod(String id, String type, String displayName, String lastFourDigits, boolean isDefault) {
        this.id = id;
        this.type = type;
        this.displayName = displayName;
        this.lastFourDigits = lastFourDigits;
        this.isDefault = isDefault;
        this.iconResourceId = 0; // Will be set based on type
    }

    public PaymentMethod(String id, String type, String displayName, String lastFourDigits, int iconResourceId, boolean isDefault) {
        this.id = id;
        this.type = type;
        this.displayName = displayName;
        this.lastFourDigits = lastFourDigits;
        this.iconResourceId = iconResourceId;
        this.isDefault = isDefault;
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getLastFourDigits() {
        return lastFourDigits;
    }

    public void setLastFourDigits(String lastFourDigits) {
        this.lastFourDigits = lastFourDigits;
    }

    public int getIconResourceId() {
        return iconResourceId;
    }

    public void setIconResourceId(int iconResourceId) {
        this.iconResourceId = iconResourceId;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
}
