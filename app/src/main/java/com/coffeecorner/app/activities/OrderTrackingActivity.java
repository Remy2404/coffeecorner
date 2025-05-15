package com.coffeecorner.app.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.coffeecorner.app.R;
import com.coffeecorner.app.models.Order;
import com.coffeecorner.app.models.CartItem;
import com.coffeecorner.app.models.Product;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class OrderTrackingActivity extends AppCompatActivity {

    private TextView tvOrderNumber;
    private TextView tvOrderDate;
    private TextView tvOrderStatus;
    private TextView tvEstimatedDelivery;
    private TextView tvDeliveryAddress;
    private TextView tvPaymentMethod;
    private TextView tvOrderTotal;
    private ProgressBar progressBarTracking;
    private TextView tvProgressStatus;
    private MaterialButton btnContactSupport;

    // Order status views
    private View statusConfirmed;
    private View statusPreparing;
    private View statusOnTheWay;
    private View statusDelivered;
    private TextView tvStatusConfirmed;
    private TextView tvStatusPreparing;
    private TextView tvStatusOnTheWay;
    private TextView tvStatusDelivered;

    private Order currentOrder;
    private Handler handler;
    private int currentProgress = 0;

    // Constants for order status - using String constants to match Order.java
    private static final String STATUS_CONFIRMED = Order.STATUS_CONFIRMED;
    private static final String STATUS_PREPARING = Order.STATUS_PREPARING;
    private static final String STATUS_ON_THE_WAY = Order.STATUS_DELIVERING;
    private static final String STATUS_DELIVERED = Order.STATUS_DELIVERED;

    // Currently hardcoded for demo
    private final String currentOrderStatus = STATUS_PREPARING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_tracking);

        // Initialize views
        initViews();

        // Initialize toolbar
        setupToolbar();

        // Create a sample order (in a real app, we'd get this from intent or database)
        createSampleOrder();

        // Setup order information
        setupOrderInfo();

        // Setup order status timeline
        setupOrderStatus();

        // Setup delivery person info
        setupDeliveryPersonInfo();

        // Setup contact support button
        setupContactButton();

        // Start simulation
        simulateOrderTracking();
    }

    private void initViews() {
        tvOrderNumber = findViewById(R.id.tvOrderNumber);
        tvOrderDate = findViewById(R.id.tvOrderDate);
        tvOrderStatus = findViewById(R.id.tvOrderStatus);
        tvEstimatedDelivery = findViewById(R.id.tvEstimatedDelivery);
        tvDeliveryAddress = findViewById(R.id.tvDeliveryAddress);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        tvOrderTotal = findViewById(R.id.tvOrderTotal);
        progressBarTracking = findViewById(R.id.progressBarTracking);
        tvProgressStatus = findViewById(R.id.tvProgressStatus);
        btnContactSupport = findViewById(R.id.btnContactSupport);

        // Order status timeline views
        statusConfirmed = findViewById(R.id.statusConfirmed);
        statusPreparing = findViewById(R.id.statusPreparing);
        statusOnTheWay = findViewById(R.id.statusOnTheWay);
        statusDelivered = findViewById(R.id.statusDelivered);
        tvStatusConfirmed = findViewById(R.id.tvStatusConfirmed);
        tvStatusPreparing = findViewById(R.id.tvStatusPreparing);
        tvStatusOnTheWay = findViewById(R.id.tvStatusOnTheWay);
        tvStatusDelivered = findViewById(R.id.tvStatusDelivered);

        handler = new Handler(Looper.getMainLooper());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void createSampleOrder() {
        // Create a sample order with items
        List<CartItem> items = new ArrayList<>();

        Product product1 = new Product(
                "1",
                "Cappuccino",
                "Our signature cappuccino with premium espresso",
                4.99,
                "https://example.com/cappuccino.jpg",
                "coffee",
                true);

        Product product2 = new Product(
                "2",
                "Chocolate Croissant",
                "Freshly baked chocolate-filled croissant",
                3.49,
                "https://example.com/croissant.jpg",
                "bakery",
                true);

        items.add(new CartItem(product1, 1, "Medium", "Almond milk", 0.50));
        items.add(new CartItem(product2, 2, "", "", 0.0));

        // Create the order
        currentOrder = new Order(
                "CC-19052",
                new Date(),
                items,
                "123 Main Street, Apt 4B, New York, NY 10001",
                "Credit Card (****4321)",
                4.99,
                1.25,
                0.00,
                currentOrderStatus,
                30); // 30 minutes estimated delivery time
    }

    private void setupOrderInfo() {
        if (currentOrder == null)
            return;

        // Format date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy · h:mm a", Locale.getDefault());
        String formattedDate = dateFormat.format(currentOrder.getOrderDate());

        // Set order details
        tvOrderNumber.setText(getString(R.string.order_number_format, currentOrder.getOrderId()));
        tvOrderDate.setText(formattedDate);
        tvEstimatedDelivery
                .setText(getString(R.string.estimated_delivery_format, currentOrder.getEstimatedDeliveryTime()));
        tvDeliveryAddress.setText(currentOrder.getDeliveryAddress());
        tvPaymentMethod.setText(currentOrder.getPaymentMethod());

        // Calculate and set order total
        double itemsTotal = 0;
        for (CartItem item : currentOrder.getItems()) {
            double itemPrice = item.getProduct().getPrice() + item.getExtraCharge();
            itemsTotal += (itemPrice * item.getQuantity());
        }

        double total = itemsTotal + currentOrder.getDeliveryFee() + currentOrder.getTax() - currentOrder.getDiscount();
        tvOrderTotal.setText("$" + String.format(Locale.US, "%.2f", total));
    }

    private void setupOrderStatus() {
        // Set the current status in the timeline
        updateOrderStatusUI(currentOrder.getStatus());
    }

    private void updateOrderStatusUI(String status) {
        switch (status) {
            case "CONFIRMED":
                tvOrderStatus.setText(R.string.order_confirmed);
                statusConfirmed.setBackgroundResource(R.drawable.circle_background_blue);
                tvStatusConfirmed.setTextColor(getResources().getColor(R.color.color_02, null));
                currentProgress = 25;
                break;

            case "PREPARING":
                tvOrderStatus.setText(R.string.preparing_order);
                statusPreparing.setBackgroundResource(R.drawable.circle_background_blue);
                tvStatusPreparing.setTextColor(getResources().getColor(R.color.color_02, null));
                currentProgress = 50;
                break;

            case "DELIVERING":
                tvOrderStatus.setText(R.string.on_the_way);
                statusOnTheWay.setBackgroundResource(R.drawable.circle_background_blue);
                tvStatusOnTheWay.setTextColor(getResources().getColor(R.color.color_02, null));
                currentProgress = 75;
                break;

            case "DELIVERED":
                tvOrderStatus.setText(R.string.delivered);
                statusDelivered.setBackgroundResource(R.drawable.circle_background_blue);
                tvStatusDelivered.setTextColor(getResources().getColor(R.color.color_02, null));
                currentProgress = 100;
                break;
        }

        progressBarTracking.setProgress(currentProgress);
        updateProgressText();
    }

    private void updateProgressText() {
        if (currentProgress <= 25) {
            tvProgressStatus.setText(R.string.order_confirmed_message);
        } else if (currentProgress <= 50) {
            tvProgressStatus.setText(R.string.order_preparing_message);
        } else if (currentProgress <= 75) {
            tvProgressStatus.setText(getString(R.string.order_on_the_way_message, "John D."));
        } else {
            tvProgressStatus.setText(R.string.order_delivered_message);
        }
    }

    private void setupDeliveryPersonInfo() {
        // In a real app, this would be populated with actual delivery person info
        // This would be shown when the order status reaches "On the Way"
        ImageView ivDeliveryPerson = findViewById(R.id.ivDeliveryPerson);
        TextView tvDeliveryPersonName = findViewById(R.id.tvDeliveryPersonName);

        if (STATUS_ON_THE_WAY.equals(currentOrder.getStatus()) ||
                STATUS_DELIVERED.equals(currentOrder.getStatus())) {
            // Show delivery person info
            ivDeliveryPerson.setVisibility(View.VISIBLE);
            tvDeliveryPersonName.setVisibility(View.VISIBLE);
            tvDeliveryPersonName.setText("John D.");
        } else {
            // Hide delivery person info until order is on the way
            ivDeliveryPerson.setVisibility(View.GONE);
            tvDeliveryPersonName.setVisibility(View.GONE);
        }
    }

    private void setupContactButton() {
        btnContactSupport.setOnClickListener(v -> {
            // In a real app, this would open a contact dialog or activity
            // For now, let's just update the button text to show it works
            btnContactSupport.setText(R.string.support_contacted);
            btnContactSupport.setEnabled(false);
        });
    }

    private void simulateOrderTracking() {
        // This is a simple simulation for demo purposes
        // In a real app, you would connect to a backend service

        // Simulate order preparation after 5 seconds
        handler.postDelayed(() -> {
            currentOrder.setStatus(STATUS_ON_THE_WAY);
            updateOrderStatusUI(STATUS_ON_THE_WAY);
            setupDeliveryPersonInfo(); // Update delivery person info
        }, TimeUnit.SECONDS.toMillis(5));

        // Simulate order is delivered after 10 seconds
        handler.postDelayed(() -> {
            currentOrder.setStatus(STATUS_DELIVERED);
            updateOrderStatusUI(STATUS_DELIVERED);
        }, TimeUnit.SECONDS.toMillis(10));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove any pending callbacks to prevent memory leaks
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
