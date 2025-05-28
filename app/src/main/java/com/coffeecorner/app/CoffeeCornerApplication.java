package com.coffeecorner.app;

import android.app.Application;
import android.util.Log;

import com.coffeecorner.app.utils.SupabaseClientManager;

/**
 * Custom Application class to initialize app-level dependencies
 */
public class CoffeeCornerApplication extends Application {

    private static final String TAG = "CoffeeCornerApp";
    private static CoffeeCornerApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Initialize Supabase client
        initializeSupabase();
    }

    /**
     * Get singleton instance of the application
     * 
     * @return Application instance
     */
    public static CoffeeCornerApplication getInstance() {
        return instance;
    }

    private void initializeSupabase() {
        try {
            // Initialize with BuildConfig values from local.properties
            SupabaseClientManager.initialize(BuildConfig.SUPABASE_URL, BuildConfig.SUPABASE_ANON_KEY);
            Log.d(TAG, "Supabase initialized from BuildConfig");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize Supabase: " + e.getMessage());
        }
    }
}
