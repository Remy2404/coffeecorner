package com.coffeecorner.app.models;

import java.util.Date;
import java.util.List;

public class Order {
    public Order() {
    }

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_CONFIRMED = "CONFIRMED";
    public static final String STATUS_PREPARING = "PREPARING";
    public static final String STATUS_READY = "READY";
    public static final String STATUS_DELIVERING = "DELIVERING";
    public static final String STATUS_DELIVERED = "DELIVERED";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_CANCELLED = "CANCELLED";
    public static final String STATUS_REFUNDED = "REFUNDED";
    private String orderId;
    private String status;
    private Date orderDate;
    // in minutes
    private int estimatedDeliveryTimeMinutes;
    private List<CartItem> items;
    private double subtotal;
    private double deliveryFee;
    private double tax;
    private double discount;
    private double total;
    private String deliveryAddress;
    private String paymentMethod;
    private String deliveryPersonName;
    private String deliveryPersonPhone;
    private String deliveryPersonPhoto; // Constructor with essential fields

    public Order(String orderId, List<CartItem> items, String deliveryAddress) {
        this.orderId = orderId;
        this.items = items;
        this.deliveryAddress = deliveryAddress;
        this.status = STATUS_CONFIRMED;
        this.orderDate = new Date(); // Current time

        // Calculate totals
        calculateTotals();
    }

    /**
     * Constructor matching OrderTrackingActivity usage.
     * 
     * @param orderId                      order identifier
     * @param orderDate                    date/time of order creation
     * @param items                        list of cart items
     * @param deliveryAddress              delivery address
     * @param paymentMethod                payment method description
     * @param deliveryFee                  delivery fee amount
     * @param tax                          tax amount
     * @param discount                     discount amount
     * @param status                       order status constant
     * @param estimatedDeliveryTimeMinutes estimated delivery time in minutes
     */
    public Order(String orderId,
            Date orderDate,
            List<CartItem> items,
            String deliveryAddress,
            String paymentMethod,
            double deliveryFee,
            double tax,
            double discount,
            String status,
            int estimatedDeliveryTimeMinutes) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.items = items;
        this.deliveryAddress = deliveryAddress;
        this.paymentMethod = paymentMethod;
        this.deliveryFee = deliveryFee;
        this.tax = tax;
        this.discount = discount;
        this.status = status;
        this.estimatedDeliveryTimeMinutes = estimatedDeliveryTimeMinutes;
        calculateTotals();
    } // Additional getters and setters

    private void calculateTotals() {
        subtotal = 0;
        for (CartItem item : items) {
            // Use CartItem total price including extra charges
            subtotal += item.getTotalPrice();
        }

        total = subtotal + deliveryFee + tax;
    }

    // Getters and setters
    public String getOrderId() {
        return orderId;
    }

    // ID getter for compatibility
    public String getId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    // Missing methods needed by OrderRepository
    public void setUserId(String userId) {
        // Implementation depends on how user ID is stored
        // This is a placeholder implementation
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    // CreatedAt getter for compatibility
    public Date getCreatedAt() {
        return orderDate;
    }

    /**
     * Returns the estimated delivery time in minutes.
     */
    public int getEstimatedDeliveryTime() {
        return estimatedDeliveryTimeMinutes;
    }

    /**
     * Sets the estimated delivery time in minutes.
     */
    public void setEstimatedDeliveryTime(int minutes) {
        this.estimatedDeliveryTimeMinutes = minutes;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
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

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
        calculateTotals();
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    // Helper method to get status as a string
    public String getStatusText() {
        switch (status) {
            case STATUS_CONFIRMED:
                return "Order Confirmed";
            case STATUS_PREPARING:
                return "Preparing";
            case STATUS_READY:
                return "Ready for Pickup";
            case STATUS_DELIVERING:
                return "On Delivery";
            case STATUS_DELIVERED:
                return "Delivered";
            case STATUS_COMPLETED:
                return "Completed";
            case STATUS_CANCELLED:
                return "Cancelled";
            default:
                return "Unknown Status";
        }
    }
}
