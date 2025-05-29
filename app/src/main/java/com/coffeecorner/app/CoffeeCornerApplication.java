package com.coffeecorner.app;

import android.app.Application;
import android.util.Log;

import com.coffeecorner.app.services.GuestAuthService;
import com.coffeecorner.app.utils.SupabaseClientManager;

/**
 * Custom Application class to initialize app-level dependencies
 */
public class CoffeeCornerApplication extends Application {

    private static final String TAG = "CoffeeCornerApp";
    private static CoffeeCornerApplication instance;
    private GuestAuthService guestAuthService;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Initialize Supabase client
        initializeSupabase();

        // Initialize guest authentication early for cart functionality
        initializeGuestAuth();
    }

    /**
     * Get singleton instance of the application
     * 
     * @return Application instance
     */
    public static CoffeeCornerApplication getInstance() {
        return instance;
    }

    /**
     * Get guest authentication service
     * 
     * @return GuestAuthService instance
     */
    public GuestAuthService getGuestAuthService() {
        if (guestAuthService == null) {
            guestAuthService = new GuestAuthService(this);
        }
        return guestAuthService;
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

    /**
     * Initialize guest authentication for immediate cart functionality
     */
    private void initializeGuestAuth() {
        guestAuthService = new GuestAuthService(this);
        guestAuthService.authenticateGuest(new GuestAuthService.AuthCallback() {
            @Override
            public void onSuccess(String token, String userId) {
                Log.d(TAG, "Guest authentication initialized successfully - User ID: " + userId);
            }

            @Override
            public void onError(String error) {
                Log.w(TAG, "Guest authentication initialization failed: " + error);
            }
        });
    }
}
