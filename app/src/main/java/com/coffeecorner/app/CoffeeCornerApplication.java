package com.coffeecorner.app;

import android.app.Application;
import android.util.Log;

import com.coffeecorner.app.utils.SupabaseClientManager;

/**
 * Custom Application class to initialize app-level dependencies
 */
public class CoffeeCornerApplication extends Application {

    private static final String TAG = "CoffeeCornerApp";

    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize Supabase client
        initializeSupabase();
    }
    
    private void initializeSupabase() {
        try {
            // Load credentials from properties file in development
            SupabaseClientManager.loadFromProperties(this);
            Log.d(TAG, "Supabase initialized from properties");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize Supabase: " + e.getMessage());
        }
    }
}
