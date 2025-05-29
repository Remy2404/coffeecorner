package com.coffeecorner.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.coffeecorner.app.R;
import com.coffeecorner.app.adapters.CheckoutItemAdapter;
import com.coffeecorner.app.models.CartItem;
import com.coffeecorner.app.models.Order;
import com.coffeecorner.app.utils.SupabaseClientManager;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CheckoutActivity extends AppCompatActivity {

    private RecyclerView rvCheckoutItems;
    private TextView tvSubtotal, tvDeliveryFee, tvTotalAmount;
    private TextView tvDeliveryAddress;
    private Button btnPlaceOrder;
    private ProgressBar progressBar;
    private CheckoutItemAdapter adapter;
    private List<CartItem> cartItems;
    private double deliveryFee = 2.0; // Default delivery fee

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        setupToolbar();
        initializeViews();
        setupRecyclerView();
        loadCartItems();
        setupListeners();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        }
    }

    private void initializeViews() {
        rvCheckoutItems = findViewById(R.id.rvCheckoutItems);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvDeliveryFee = findViewById(R.id.tvDeliveryFee);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvDeliveryAddress = findViewById(R.id.tvDeliveryAddress);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupRecyclerView() {
        cartItems = new ArrayList<>();
        adapter = new CheckoutItemAdapter(this, cartItems);
        rvCheckoutItems.setLayoutManager(new LinearLayoutManager(this));
        rvCheckoutItems.setAdapter(adapter);
    }

    private void loadCartItems() {
        // In a real app, load cart items from local storage or Supabase
        // For demo, we'll use sample data
        calculateTotals();
    }

    private void calculateTotals() {
        double subtotal = 0;
        for (CartItem item : cartItems) {
            subtotal += item.getTotalPrice();
        }

        tvSubtotal.setText(String.format("$%.2f", subtotal));
        tvDeliveryFee.setText(String.format("$%.2f", deliveryFee));
        double total = subtotal + deliveryFee;
        tvTotalAmount.setText(String.format("$%.2f", total));
    }

    private void setupListeners() {
        btnPlaceOrder.setOnClickListener(v -> placeOrder());
    }

    private void placeOrder() {
        if (cartItems.isEmpty()) {
            showToast("Your cart is empty");
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String orderId = UUID.randomUUID().toString();
        String deliveryAddress = tvDeliveryAddress.getText().toString();
        progressBar.setVisibility(View.VISIBLE);
        btnPlaceOrder.setEnabled(false); // Create a new Order with all required parameters
        Order newOrder = new Order(
                orderId,
                new Date(),
                cartItems,
                deliveryAddress,
                "Cash on Delivery",
                deliveryFee,
                0.0, // tax
                0.0, // discount
                Order.STATUS_CONFIRMED,
                30 // estimated delivery time in minutes
        );

        try {
            // Use our simplified SupabaseClientManager
            SupabaseClientManager.getInstance()
                    .from("orders")
                    .insert(newOrder)
                    .executeAsync(response -> runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        btnPlaceOrder.setEnabled(true);

                        if (response.getError() == null) {
                            showToast("Order placed successfully!");
                            clearCart();
                            navigateToOrderTracking(orderId);
                        } else {
                            showToast("Failed to place order: " + response.getError().getMessage());
                        }
                    }));
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            btnPlaceOrder.setEnabled(true);
            showToast("Error: " + e.getMessage());
        }
    }

    private void navigateToOrderTracking(String orderId) {
        Intent intent = new Intent(this, OrderTrackingActivity.class);
        intent.putExtra("orderId", orderId);
        startActivity(intent);
        finish();
    }

    private void clearCart() {
        // Clear cart items from local storage or Supabase
        cartItems.clear();
        adapter.notifyDataSetChanged();
        calculateTotals();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
