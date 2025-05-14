package com.coffeecorner.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import com.coffeecorner.app.R;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity fullscreen
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set the layout for this activity
        setContentView(R.layout.activity_splash);

        // Initialize the continue button and set its click listener
        Button btnContinue = findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToNextScreen();
            }
        });

        // If auto-transition is enabled, set a timer to move to the next screen
        // automatically
        if (isAutoTransition) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    navigateToNextScreen();
                }
            }, SPLASH_DISPLAY_LENGTH);
        }
    }

    /**
     * Navigate to the next screen in the app flow
     * In a complete implementation, this would check if the user has completed
     * onboarding
     * and direct them to either MainActivity or OnboardingActivity
     */
    private void navigateToNextScreen() {
        // TODO: Check if user has completed onboarding process
        boolean hasCompletedOnboarding = false; // This should be retrieved from SharedPreferences

        Intent intent;
        if (!hasCompletedOnboarding) {
            // Navigate to OnboardingActivity for first-time users
            intent = new Intent(SplashActivity.this, OnboardingActivity.class);
        } else {
            // User has already seen onboarding, go to LoginActivity
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        startActivity(intent);
        finish(); // Close this activity so it's not in the back stack
    }
}
