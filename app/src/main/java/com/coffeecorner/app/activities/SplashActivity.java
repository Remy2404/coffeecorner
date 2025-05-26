package com.coffeecorner.app.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.widget.Button;
import com.coffeecorner.app.R;
import com.coffeecorner.app.utils.PreferencesHelper;

import androidx.appcompat.app.AppCompatActivity;

/**
 * SplashActivity - Entry point for the Coffee Corner app
 * Displays a welcome screen with the app logo and a continue button
 */
public class SplashActivity extends AppCompatActivity {

    // Splash screen display duration in milliseconds (if auto-transition is
    // enabled)
    private static final long SPLASH_DISPLAY_LENGTH = 3000;
    private boolean isAutoTransition = false; // Set to true to enable automatic transition
    private PreferencesHelper preferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Initialize PreferencesHelper
        preferencesHelper = new PreferencesHelper(this); // Make the activity fullscreen using the latest recommended
                                                         // approach
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            @SuppressWarnings("deprecation")
            Window window = getWindow();
            window.setDecorFitsSystemWindows(false);

            // Additionally, we can control system bar appearance
            window.getInsetsController().setSystemBarsBehavior(
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        } else {
            // For older versions, use the older method
            @SuppressWarnings("deprecation")
            int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            getWindow().getDecorView().setSystemUiVisibility(flags);
        }

        // Set the layout for this activity
        setContentView(R.layout.activity_splash);

        // Initialize the continue button and set its click listener
        Button btnContinue = findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(v -> navigateToNextScreen()); // If auto-transition is enabled, set a timer to
                                                                     // move to the next screen
        // automatically
        if (isAutoTransition) {
            new Handler(Looper.getMainLooper()).postDelayed(this::navigateToNextScreen, SPLASH_DISPLAY_LENGTH);
        }
    }

    /**
     * Navigate to the next screen in the app flow
     * In a complete implementation, this would check if the user has completed
     * onboarding
     * and direct them to either MainActivity or OnboardingActivity
     */
    private void navigateToNextScreen() {
        // Check if user has completed onboarding process
        boolean hasCompletedOnboarding = preferencesHelper.hasCompletedOnboarding();

        Intent intent;
        if (!hasCompletedOnboarding) {
            // Navigate to OnboardingActivity for first-time users
            intent = new Intent(SplashActivity.this, OnboardingActivity.class);
        } else {
            // Check if user is logged in
            boolean isLoggedIn = preferencesHelper.getUserId() != null && !preferencesHelper.getUserId().isEmpty();

            if (isLoggedIn) {
                // User is logged in, go directly to MainActivity
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                // User has seen onboarding but is not logged in, go to LoginActivity
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
        }

        startActivity(intent);
        finish(); // Close this activity so it's not in the back stack
    }
}
