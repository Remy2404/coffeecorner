package com.coffeecorner.app.services;

import android.content.Context;
import android.util.Log;

import com.coffeecorner.app.network.ApiService;
import com.coffeecorner.app.network.RetrofitClient;
import com.coffeecorner.app.network.ApiResponse;
import com.coffeecorner.app.models.User;
import com.coffeecorner.app.utils.PreferencesHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * GuestAuthService - Handles guest user authentication
 * Automatically creates and logs in a guest user for cart functionality
 */
public class GuestAuthService {
    private static final String TAG = "GuestAuthService";
    private static final String GUEST_EMAIL = "guest@coffeecorner.app";
    private static final String GUEST_PASSWORD = "guestpassword123";
    private static final String GUEST_NAME = "Guest User";

    private final Context context;
    private final PreferencesHelper preferencesHelper;
    private final ApiService apiService;

    public interface AuthCallback {
        void onSuccess(String token, String userId);

        void onError(String error);
    }

    public GuestAuthService(Context context) {
        this.context = context;
        this.preferencesHelper = new PreferencesHelper(context);
        this.apiService = RetrofitClient.getApiService();
    }

    /**
     * Authenticate guest user - creates guest user if needed and logs them in
     */
    public void authenticateGuest(AuthCallback callback) {
        Log.d(TAG, "Starting guest authentication");

        // Check if we already have a valid guest token
        String existingToken = preferencesHelper.getAuthToken();
        if (existingToken != null && !existingToken.isEmpty()) {
            Log.d(TAG, "Guest already authenticated with existing token");
            callback.onSuccess(existingToken, preferencesHelper.getUserId());
            return;
        }

        // Try to login with guest credentials first
        loginGuest(callback);
    }

    /**
     * Login guest user
     */
    private void loginGuest(AuthCallback callback) {
        Log.d(TAG, "Attempting guest login");

        apiService.login(GUEST_EMAIL, GUEST_PASSWORD).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    // Guest login successful
                    User user = response.body().getData();
                    String token = generateGuestToken(user.getId());

                    saveGuestCredentials(token, user.getId());
                    Log.d(TAG, "Guest login successful");
                    callback.onSuccess(token, user.getId());
                } else {
                    // Guest user doesn't exist, try to register
                    Log.d(TAG, "Guest login failed, attempting registration");
                    registerGuest(callback);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                Log.e(TAG, "Guest login network error", t);
                // Try to register guest user
                registerGuest(callback);
            }
        });
    }

    /**
     * Register guest user
     */
    private void registerGuest(AuthCallback callback) {
        Log.d(TAG, "Attempting guest registration");

        apiService.register(GUEST_NAME, GUEST_EMAIL, GUEST_PASSWORD).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    // Guest registration successful
                    User user = response.body().getData();
                    String token = generateGuestToken(user.getId());

                    saveGuestCredentials(token, user.getId());
                    Log.d(TAG, "Guest registration successful");
                    callback.onSuccess(token, user.getId());
                } else {
                    // Both login and registration failed, use fallback
                    Log.w(TAG, "Guest registration failed, using fallback authentication");
                    useFallbackAuthentication(callback);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                Log.e(TAG, "Guest registration network error", t);
                useFallbackAuthentication(callback);
            }
        });
    }

    /**
     * Use fallback authentication when API is not available
     */
    private void useFallbackAuthentication(AuthCallback callback) {
        Log.d(TAG, "Using fallback guest authentication");

        // Generate a temporary guest token and user ID
        String fallbackUserId = "guest_" + System.currentTimeMillis();
        String fallbackToken = generateGuestToken(fallbackUserId);

        saveGuestCredentials(fallbackToken, fallbackUserId);
        callback.onSuccess(fallbackToken, fallbackUserId);
    }

    /**
     * Generate a simple guest token
     */
    private String generateGuestToken(String userId) {
        return "guest_token_" + userId + "_" + System.currentTimeMillis();
    }

    /**
     * Save guest credentials to preferences
     */
    private void saveGuestCredentials(String token, String userId) {
        preferencesHelper.saveAuthToken(token);
        preferencesHelper.saveUserId(userId);
        preferencesHelper.saveUserLogin(userId, GUEST_NAME, GUEST_EMAIL, token);
        Log.d(TAG, "Guest credentials saved - User ID: " + userId);
    }

    /**
     * Check if current user is guest
     */
    public boolean isGuestUser() {
        String email = preferencesHelper.getUserEmail();
        return GUEST_EMAIL.equals(email);
    }

    /**
     * Clear guest authentication
     */
    public void clearGuestAuth() {
        preferencesHelper.clearUserLogin();
        Log.d(TAG, "Guest authentication cleared");
    }
}
