package com.coffeecorner.app.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.coffeecorner.app.utils.PreferencesHelper;

/**
 * BaseActivity - Base class for all activities in the Coffee Corner app
 * Handles common functionality like theme application
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected PreferencesHelper preferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme before calling super.onCreate()
        preferencesHelper = new PreferencesHelper(this);
        applyTheme();
        
        super.onCreate(savedInstanceState);
    }

    /**
     * Apply the current theme preference
     */
    private void applyTheme() {
        if (preferencesHelper.isDarkModeEnabled()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
