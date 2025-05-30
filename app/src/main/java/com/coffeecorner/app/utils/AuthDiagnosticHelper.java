package com.coffeecorner.app.utils;

import android.content.Context;
import android.util.Log;
import com.coffeecorner.app.network.ApiService;
import com.coffeecorner.app.network.ApiResponse;
import com.coffeecorner.app.network.RetrofitClient;
import com.coffeecorner.app.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * AuthDiagnosticHelper - Helper class to diagnose authentication issues
 */
public class AuthDiagnosticHelper {
    private static final String TAG = "AuthDiagnostic";
    
    private final ApiService apiService;
    private final PreferencesHelper preferencesHelper;
    
    public AuthDiagnosticHelper(Context context) {
        this.apiService = RetrofitClient.getApiService();
        this.preferencesHelper = new PreferencesHelper(context);
    }
    
    /**
     * Test if the current JWT token is valid and can retrieve user profile
     */
    public void testCurrentAuthentication(AuthTestCallback callback) {
        String authToken = preferencesHelper.getAuthToken();
        String userId = preferencesHelper.getUserId();
        String userEmail = preferencesHelper.getUserEmail();
        
        Log.d(TAG, "=== Authentication Diagnostic ===");
        Log.d(TAG, "Auth Token: " + (authToken != null ? authToken.substring(0, Math.min(20, authToken.length())) + "..." : "null"));
        Log.d(TAG, "User ID: " + userId);
        Log.d(TAG, "User Email: " + userEmail);
        Log.d(TAG, "Is Logged In: " + preferencesHelper.isLoggedIn());
        
        if (authToken == null || authToken.isEmpty()) {
            callback.onResult(false, "No auth token found", null);
            return;
        }
        
        // Test the auth/me endpoint to verify JWT token
        apiService.getUserProfile(userId).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<User> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Log.d(TAG, "✅ Authentication test successful");
                        Log.d(TAG, "User profile retrieved: " + apiResponse.getData().getName());
                        callback.onResult(true, "Authentication successful", apiResponse.getData());
                    } else {
                        Log.e(TAG, "❌ API returned error: " + apiResponse.getMessage());
                        callback.onResult(false, "API error: " + apiResponse.getMessage(), null);
                    }
                } else {
                    String errorMsg = "HTTP " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += ": " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    Log.e(TAG, "❌ Authentication test failed: " + errorMsg);
                    callback.onResult(false, errorMsg, null);
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                Log.e(TAG, "❌ Network error during authentication test", t);
                callback.onResult(false, "Network error: " + t.getMessage(), null);
            }
        });
    }
    
    /**
     * Clear authentication and force re-login
     */
    public void clearAuthAndForceRelogin() {
        Log.d(TAG, "Clearing authentication data");
        preferencesHelper.clearUserLogin();
    }
    
    public interface AuthTestCallback {
        void onResult(boolean success, String message, User user);
    }
}
