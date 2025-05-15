package com.coffeecorner.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.coffeecorner.app.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fabCart;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        initializeViews();

        // Set up navigation
        setupNavigation();
    }

    private void initializeViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fabCart = findViewById(R.id.fab_cart);

        fabCart.setOnClickListener(v -> navController.navigate(R.id.cartFragment));
    }

    private void setupNavigation() {
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        // Configure the bottom navigation with the nav controller
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeFragment, R.id.menuFragment, R.id.orderHistoryFragment, R.id.profileFragment)
                .build();

        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    // Navigation helper methods
    public void navigateToCheckout() {
        startActivity(new Intent(this, CheckoutActivity.class));
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
