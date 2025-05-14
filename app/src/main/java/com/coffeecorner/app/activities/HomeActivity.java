package com.coffeecorner.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.coffeecorner.app.R;
import com.coffeecorner.app.fragments.CartFragment;
import com.coffeecorner.app.fragments.HomeFragment;
import com.coffeecorner.app.fragments.OrderHistoryFragment;
import com.coffeecorner.app.fragments.ProfileFragment;
import com.coffeecorner.app.fragments.LoyaltyFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fabCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // Initialize UI elements
        initializeViews();

        // Set up bottom navigation
        setupBottomNavigation();

        // Load default fragment (Home)
        loadFragment(new HomeFragment());

        // Set up click listeners
        setupClickListeners();
    }

    private void initializeViews() {
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        fabCart = findViewById(R.id.fabCart);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.navigation_rewards) {
                selectedFragment = new LoyaltyFragment();
            } else if (itemId == R.id.navigation_orders) {
                selectedFragment = new OrderHistoryFragment();
            } else if (itemId == R.id.navigation_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    private void setupClickListeners() {
        // Open cart when FAB is clicked
        fabCart.setOnClickListener(v -> {
            loadFragment(new CartFragment());
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    public void navigateToCheckout() {
        Intent intent = new Intent(this, CheckoutActivity.class);
        startActivity(intent);
    }

    public void navigateToOrderTracking() {
        Intent intent = new Intent(this, OrderTrackingActivity.class);
        startActivity(intent);
    }

    public void navigateToRewards() {
        Intent intent = new Intent(this, RewardsActivity.class);
        startActivity(intent);
    }

    public void navigateToSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}