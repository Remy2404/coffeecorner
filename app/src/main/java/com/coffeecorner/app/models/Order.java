package com.coffeecorner.app.models;

import java.util.Date;
import java.util.List;

public class Order {
    public static final int STATUS_CONFIRMED = 1;
    public static final int STATUS_PREPARING = 2;
    public static final int STATUS_ON_DELIVERY = 3;
    public static final int STATUS_DELIVERED = 4;
    
    private String orderId;
    private int status;
    private Date orderDate;
    private Date estimatedDeliveryTime;
    private List<CartItem> items;
    private double subtotal;
    private double deliveryFee;
    private double tax;
    private double total;
    private String deliveryAddress;
    private String deliveryPersonName;
    private String deliveryPersonPhone;
    private String deliveryPersonPhoto;
    
    // Constructor with essential fields
    public Order(String orderId, List<CartItem> items, String deliveryAddress) {
        this.orderId = orderId;
        this.items = items;
        this.deliveryAddress = deliveryAddress;
        this.status = STATUS_CONFIRMED;
        this.orderDate = new Date(); // Current time
        
        // Calculate totals
        calculateTotals();
    }

    // More comprehensive constructor
    public Order(String orderId, int status, Date orderDate, Date estimatedDeliveryTime, 
                List<CartItem> items, double deliveryFee, double tax, String deliveryAddress) {
        this.orderId = orderId;
        this.status = status;
        this.orderDate = orderDate;
        this.estimatedDeliveryTime = estimatedDeliveryTime;
        this.items = items;
        this.deliveryFee = deliveryFee;
        this.tax = tax;
        this.deliveryAddress = deliveryAddress;
        
        // Calculate totals
        calculateTotals();
    }
    
    private void calculateTotals() {
        subtotal = 0;
        for (CartItem item : items) {
            subtotal += item.getPrice() * item.getQuantity();
        }
        
        total = subtotal + deliveryFee + tax;
    }
    
    // Getters and setters
    
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getEstimatedDeliveryTime() {
        return estimatedDeliveryTime;
    }

    public void setEstimatedDeliveryTime(Date estimatedDeliveryTime) {
        this.estimatedDeliveryTime = estimatedDeliveryTime;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
        calculateTotals();
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(double deliveryFee) {
        this.deliveryFee = deliveryFee;
        calculateTotals();
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
        calculateTotals();
    }

    public double getTotal() {
        return total;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getDeliveryPersonName() {
        return deliveryPersonName;
    }

    public void setDeliveryPersonName(String deliveryPersonName) {
        this.deliveryPersonName = deliveryPersonName;
    }

    public String getDeliveryPersonPhone() {
        return deliveryPersonPhone;
    }

    public void setDeliveryPersonPhone(String deliveryPersonPhone) {
        this.deliveryPersonPhone = deliveryPersonPhone;
    }

    public String getDeliveryPersonPhoto() {
        return deliveryPersonPhoto;
    }

    public void setDeliveryPersonPhoto(String deliveryPersonPhoto) {
        this.deliveryPersonPhoto = deliveryPersonPhoto;
    }
    
    // Helper method to get status as a string
    public String getStatusText() {
        switch (status) {
            case STATUS_CONFIRMED:
                return "Order Confirmed";
            case STATUS_PREPARING:
                return "Preparing";
            case STATUS_ON_DELIVERY:
                return "On Delivery";
            case STATUS_DELIVERED:
                return "Delivered";
            default:
                return "Unknown Status";
        }
    }
}
