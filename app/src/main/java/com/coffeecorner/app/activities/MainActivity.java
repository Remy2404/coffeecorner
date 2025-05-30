package com.coffeecorner.app.activities;

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

import com.coffeecorner.app.services.GuestAuthService;

import com.coffeecorner.app.utils.AuthDiagnostic;

import com.coffeecorner.app.utils.PreferencesHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private BottomNavigationView bottomNavigationView;
    private NavController navController;

    private GuestAuthService guestAuthService;  
    @Override
    private AuthDiagnostic authDiagnostic;
    private PreferencesHelper preferencesHelper;
    private int debugClickCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        initializeViews();

        // Set up navigation
        setupNavigation();

        // Initialize debug utilities
        authDiagnostic = new AuthDiagnostic(this);
        preferencesHelper = new PreferencesHelper(this);
        
        // Set up debug functionality
        setupDebugFeatures();
        
        // Log successful initialization
        Log.d(TAG, "MainActivity initialized successfully");
    }

    private void initializeViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
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
                        R.id.homeFragment, R.id.menuFragment, R.id.cartFragment, R.id.orderHistoryFragment,
                        R.id.profileFragment)
                        .build();

                // Setup the bottom navigation view with the nav controller
                NavigationUI.setupWithNavController(bottomNavigationView, navController);

                // Set up custom click listener to override default behavior
                bottomNavigationView.setOnItemSelectedListener(item -> {
                    // Get the selected fragment id from the menu item
                    int selectedFragmentId = item.getItemId();

                    // Navigate directly to the selected fragment without adding to back stack
                    // This enables direct navigation between tabs without requiring Back button
                    navController.popBackStack(navController.getGraph().getStartDestination(), false);

                    // Navigate to the selected destination
                    navController.navigate(selectedFragmentId);

                    return true;
                });

                Log.d(TAG, "Navigation setup completed successfully with custom tab handling");
            } else {
                Log.e(TAG, "navHostFragment is not an instance of NavHostFragment");
                Toast.makeText(this, "Navigation setup failed", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Navigation setup error: " + e.getMessage(), e);
            Toast.makeText(this, "Navigation error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Update cart badge in the bottom navigation
     * 
     * @param itemCount Number of items in cart
     */
    public void updateCartBadge(int itemCount) {
        // Now we'll update the badge on the bottom navigation cart item instead of the
        // FAB
        if (bottomNavigationView != null) {
            if (itemCount > 0) {
                // Set badge on the cart menu item
                bottomNavigationView.getOrCreateBadge(R.id.cartFragment).setNumber(itemCount);
            } else {
                // Remove the badge when cart is empty
                bottomNavigationView.removeBadge(R.id.cartFragment);
            }
        }
        Log.d(TAG, "Cart badge updated: " + itemCount + " items");
    }
    
    /**
     * Set up debug features for authentication testing
     * Tap the app title 7 times to trigger debug menu
     */
    private void setupDebugFeatures() {
        // Set up debug click listener on the action bar (if needed)
        // For now, we'll add a method that can be called programmatically
    }
    
    /**
     * Run authentication diagnostic (for debugging)
     * Call this method when you need to debug auth issues
     */
    public void runAuthDiagnostic() {
        Log.d(TAG, "Running authentication diagnostic...");
        Toast.makeText(this, "Running auth diagnostic - check logs", Toast.LENGTH_LONG).show();
        
        if (authDiagnostic != null) {
            authDiagnostic.runFullDiagnostic();
        }
    }
    
    /**
     * Force re-authentication (for debugging)
     */
    public void forceReauth() {
        Log.d(TAG, "Forcing re-authentication...");
        Toast.makeText(this, "Re-authenticating - check logs", Toast.LENGTH_LONG).show();
        
        if (authDiagnostic != null) {
            authDiagnostic.forceReauth();
        }
    }
    
    /**
     * Clear all authentication data (for debugging)
     */
    public void clearAuthData() {
        Log.d(TAG, "Clearing authentication data...");
        Toast.makeText(this, "Auth data cleared - restart app", Toast.LENGTH_LONG).show();
        
        if (authDiagnostic != null) {
            authDiagnostic.clearAllAuth();
        }
    }
    
    /**
     * Debug method to check current auth state
     */
    public void checkAuthState() {
        if (preferencesHelper != null) {
            boolean isLoggedIn = preferencesHelper.isLoggedIn();
            String authToken = preferencesHelper.getAuthToken();
            String userId = preferencesHelper.getUserId();
            
            String message = "Auth State:\n" +
                           "Logged in: " + isLoggedIn + "\n" +
                           "Token: " + (authToken != null ? "present" : "null") + "\n" +
                           "User ID: " + userId;
            
            Log.d(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    public int getDebugClickCount() {
        return debugClickCount;
    }

    public void setDebugClickCount(int debugClickCount) {
        this.debugClickCount = debugClickCount;
    }
}
