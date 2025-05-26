package com.coffeecorner.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.coffeecorner.app.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fabCart;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set theme to follow user preference (light/dark)
        android.content.SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean isDark = prefs.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(isDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        initializeViews();

        // Set up navigation
        setupNavigation();

        // Log successful initialization
        Log.d(TAG, "MainActivity initialized successfully");
    }

    private void initializeViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fabCart = findViewById(R.id.fab_cart);

        fabCart.setOnClickListener(v -> {
            if (navController != null) {
                navController.navigate(R.id.cartFragment);
            } else {
                Toast.makeText(this, "Navigation not ready", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupNavigation() {
        try {
            // Get NavHostFragment and NavController
            Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

            if (navHostFragment instanceof NavHostFragment) {
                navController = ((NavHostFragment) navHostFragment).getNavController();

                // Configure the bottom navigation with the nav controller
                // Use the fragment IDs from nav_graph.xml that match the bottom_nav_menu.xml
                AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                        R.id.homeFragment, R.id.menuFragment, R.id.cartFragment, R.id.orderHistoryFragment, R.id.profileFragment)
                        .build();

                // Setup the bottom navigation view with the nav controller
                NavigationUI.setupWithNavController(bottomNavigationView, navController);
                Log.d(TAG, "Navigation setup completed successfully");
            } else {
                Log.e(TAG, "navHostFragment is not an instance of NavHostFragment");
                Toast.makeText(this, "Navigation setup failed", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Navigation setup error: " + e.getMessage(), e);
            Toast.makeText(this, "Navigation error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Navigation helper methods
    public void navigateToCheckout() {
        startActivity(new Intent(this, CheckoutActivity.class));
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Update cart badge on the floating action button
     * @param itemCount Number of items in cart
     */
    public void updateCartBadge(int itemCount) {
        // For now, we'll use a simple approach by updating the FAB's content description
        // In a more advanced implementation, you could use Material Design Badge API
        if (fabCart != null) {
            if (itemCount > 0) {
                fabCart.setContentDescription("Cart (" + itemCount + " items)");
                // You could also change the FAB appearance here
                // For example, change color or add an overlay
            } else {
                fabCart.setContentDescription("Cart (empty)");
            }
        }
        Log.d(TAG, "Cart badge updated: " + itemCount + " items");
    }
}
