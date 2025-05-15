package com.coffeecorner.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.coffeecorner.app.R;

public class OrderCompleteActivity extends AppCompatActivity {

    private TextView tvOrderId;
    private TextView tvEstimatedTime;
    private Button btnViewOrder;
    private Button btnContinueShopping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_complete);

        // Initialize views
        initViews();
        
        // Get order details from intent
        String orderId = getIntent().getStringExtra("orderId");
        int estimatedTime = getIntent().getIntExtra("estimatedTime", 30);
        
        // Set order details
        tvOrderId.setText(getString(R.string.order_id_format, orderId));
        tvEstimatedTime.setText(getString(R.string.estimated_delivery_format, estimatedTime));
        
        // Set click listeners
        setupClickListeners(orderId);
    }

    private void initViews() {
        tvOrderId = findViewById(R.id.tvOrderId);
        tvEstimatedTime = findViewById(R.id.tvEstimatedTime);
        btnViewOrder = findViewById(R.id.btnViewOrder);
        btnContinueShopping = findViewById(R.id.btnContinueShopping);
        
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> navigateToHome());
    }
    
    private void setupClickListeners(String orderId) {
        btnViewOrder.setOnClickListener(v -> {
            // Navigate to order tracking screen
            Intent intent = new Intent(this, OrderTrackingActivity.class);
            intent.putExtra("orderId", orderId);
            startActivity(intent);
            finish();
        });
        
        btnContinueShopping.setOnClickListener(v -> {
            navigateToHome();
        });
    }
    
    private void navigateToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    
    @Override
    public void onBackPressed() {
        // Override back button to go to home instead of checkout
        super.onBackPressed();
        navigateToHome();
    }
}
