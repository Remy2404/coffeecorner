package com.coffeecorner.app.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.coffeecorner.app.models.CartItem;
import com.coffeecorner.app.models.Order;
import com.coffeecorner.app.models.User;
import com.coffeecorner.app.network.ApiResponse;
import com.coffeecorner.app.network.ApiService;
import com.coffeecorner.app.network.AuthResponse;
import com.coffeecorner.app.network.RetrofitClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Utility class to diagnose JWT authentication issues
 * This helps identify where the authentication flow is breaking
 */
public class AuthDiagnostic {
    private static final String TAG = "AuthDiagnostic";

    private final PreferencesHelper preferencesHelper;
    private final ApiService apiService;
    private final FirebaseAuth firebaseAuth;

    public AuthDiagnostic(Context context) {
        this.preferencesHelper = new PreferencesHelper(context);
        this.apiService = RetrofitClient.getApiService();
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    /**
     * Run complete authentication diagnostic
     */
    public void runFullDiagnostic() {
        Log.d(TAG, "========== AUTH DIAGNOSTIC START ==========");

        // Step 1: Check Firebase authentication
        checkFirebaseAuth();

        // Step 2: Check stored tokens and user data
        checkStoredData();

        // Step 3: Test backend authentication
        testBackendAuth();

        // Step 4: Test JWT endpoints
        testJwtEndpoints();

        Log.d(TAG, "========== AUTH DIAGNOSTIC END ==========");
    }

    /**
     * Check Firebase authentication status
     */
    private void checkFirebaseAuth() {
        Log.d(TAG, "--- FIREBASE AUTH CHECK ---");

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "✓ Firebase user logged in: " + currentUser.getEmail());
            Log.d(TAG, "✓ Firebase UID: " + currentUser.getUid());

            // Get fresh ID token
            currentUser.getIdToken(true)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();
                            Log.d(TAG, "✓ Firebase ID token obtained (length: " +
                                    (idToken != null ? idToken.length() : "null") + ")");
                            if (idToken != null) {
                                Log.d(TAG, "✓ Token preview: " + idToken.substring(0, Math.min(50, idToken.length()))
                                        + "...");
                            }
                        } else {
                            Log.e(TAG, "✗ Failed to get Firebase ID token: " + task.getException());
                        }
                    });
        } else {
            Log.e(TAG, "✗ No Firebase user logged in");
        }
    }

    /**
     * Check stored authentication data
     */
    private void checkStoredData() {
        Log.d(TAG, "--- STORED DATA CHECK ---");

        // Check login status
        boolean isLoggedIn = preferencesHelper.isLoggedIn();
        Log.d(TAG, "Login status: " + isLoggedIn);

        // Check stored auth token
        String authToken = preferencesHelper.getAuthToken();
        Log.d(TAG,
                "Stored auth token: " + (authToken != null ? "present (length: " + authToken.length() + ")" : "null"));
        if (authToken != null && authToken.length() > 50) {
            Log.d(TAG, "Token preview: " + authToken.substring(0, 50) + "...");
        }

        // Check user data
        String userId = preferencesHelper.getUserId();
        String userName = preferencesHelper.getUserName();
        String userEmail = preferencesHelper.getUserEmail();

        Log.d(TAG, "User ID: " + userId);
        Log.d(TAG, "User Name: " + userName);
        Log.d(TAG, "User Email: " + userEmail);
    }

    /**
     * Test backend authentication endpoint
     */
    private void testBackendAuth() {
        Log.d(TAG, "--- BACKEND AUTH TEST ---");

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "✗ Cannot test backend auth - no Firebase user");
            return;
        }

        currentUser.getIdToken(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String idToken = task.getResult().getToken();
                        Log.d(TAG, "Testing backend auth with Firebase token...");

                        apiService.authenticateWithFirebase(idToken).enqueue(new Callback<AuthResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<AuthResponse> call,
                                    @NonNull Response<AuthResponse> response) {
                                Log.d(TAG, "Backend auth response code: " + response.code());

                                if (response.isSuccessful() && response.body() != null) {
                                    AuthResponse authResponse = response.body();
                                    Log.d(TAG, "✓ Backend auth successful: " + authResponse.isSuccess());
                                    Log.d(TAG, "Access token received: " +
                                            (authResponse.getAccessToken() != null ? "yes" : "no"));

                                    User user = authResponse.getUser();
                                    if (user != null) {
                                        Log.d(TAG, "✓ User data received: " + user.getEmail());
                                    } else {
                                        Log.e(TAG, "✗ No user data in response");
                                    }
                                } else {
                                    Log.e(TAG, "✗ Backend auth failed: " + response.code());
                                    try {
                                        if (response.errorBody() != null) {
                                            Log.e(TAG, "Error: " + response.errorBody().string());
                                        }
                                    } catch (Exception e) {
                                        Log.e(TAG, "Error reading error body", e);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                                Log.e(TAG, "✗ Backend auth network error", t);
                            }
                        });
                    } else {
                        Log.e(TAG, "✗ Failed to get Firebase token for backend test");
                    }
                });
    }

    /**
     * Test JWT-protected endpoints
     */
    private void testJwtEndpoints() {
        Log.d(TAG, "--- JWT ENDPOINTS TEST ---");

        String authToken = preferencesHelper.getAuthToken();
        if (authToken == null) {
            Log.e(TAG, "✗ No auth token stored - cannot test JWT endpoints");
            return;
        }

        // Test cart endpoint
        testCartEndpoint();

        // Test orders endpoint
        testOrdersEndpoint();

        // Test user profile endpoint (if exists)
        testUserProfileEndpoint();
    }

    /**
     * Test cart endpoint
     */
    private void testCartEndpoint() {
        Log.d(TAG, "Testing cart endpoint...");

        apiService.getCart().enqueue(new Callback<ApiResponse<List<CartItem>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<CartItem>>> call,
                    @NonNull Response<ApiResponse<List<CartItem>>> response) {
                Log.d(TAG, "Cart endpoint response: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "✓ Cart endpoint success: " + response.body().isSuccess());
                    if (response.body().getData() != null) {
                        Log.d(TAG, "✓ Cart items count: " + response.body().getData().size());
                    }
                } else {
                    Log.e(TAG, "✗ Cart endpoint failed: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            Log.e(TAG, "Cart error: " + response.errorBody().string());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading cart error body", e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<CartItem>>> call, @NonNull Throwable t) {
                Log.e(TAG, "✗ Cart endpoint network error", t);
            }
        });
    }

    /**
     * Test orders endpoint
     */
    private void testOrdersEndpoint() {
        Log.d(TAG, "Testing orders endpoint...");

        apiService.getUserOrders().enqueue(new Callback<ApiResponse<List<Order>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Order>>> call,
                    @NonNull Response<ApiResponse<List<Order>>> response) {
                Log.d(TAG, "Orders endpoint response: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "✓ Orders endpoint success: " + response.body().isSuccess());
                    if (response.body().getData() != null) {
                        Log.d(TAG, "✓ Orders count: " + response.body().getData().size());
                    }
                } else {
                    Log.e(TAG, "✗ Orders endpoint failed: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            Log.e(TAG, "Orders error: " + response.errorBody().string());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading orders error body", e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Order>>> call, @NonNull Throwable t) {
                Log.e(TAG, "✗ Orders endpoint network error", t);
            }
        });
    }

    /**
     * Test user profile endpoint
     */
    private void testUserProfileEndpoint() {
        Log.d(TAG, "Testing user profile endpoint...");

        // Note: This assumes there's a getCurrentUser endpoint
        // Adjust based on your actual API
        String userId = preferencesHelper.getUserId();
        if (userId != null) {
            apiService.getUserById(userId).enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<ApiResponse<User>> call,
                                       @NonNull Response<ApiResponse<User>> response) {
                    Log.d(TAG, "User profile endpoint response: " + response.code());

                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "✓ User profile endpoint success: " + response.body().isSuccess());
                    } else {
                        Log.e(TAG, "✗ User profile endpoint failed: " + response.code());
                        try {
                            if (response.errorBody() != null) {
                                Log.e(TAG, "Profile error: " + response.errorBody().string());
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading profile error body", e);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ApiResponse<User>> call, @NonNull Throwable t) {
                    Log.e(TAG, "✗ User profile endpoint network error", t);
                }
            });
        }
    }

    /**
     * Force re-authentication with fresh tokens
     */
    public void forceReauth() {
        Log.d(TAG, "--- FORCING RE-AUTHENTICATION ---");

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            currentUser.getIdToken(true)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String freshToken = task.getResult().getToken();
                            Log.d(TAG, "Got fresh Firebase token, authenticating with backend...");

                            apiService.authenticateWithFirebase(freshToken).enqueue(new Callback<AuthResponse>() {
                                @Override
                                public void onResponse(@NonNull Call<AuthResponse> call,
                                        @NonNull Response<AuthResponse> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        AuthResponse authResponse = response.body();

                                        // Save new access token
                                        if (authResponse.getAccessToken() != null) {
                                            preferencesHelper.saveAuthToken(authResponse.getAccessToken());
                                            Log.d(TAG, "✓ New access token saved");

                                            // Test endpoints again
                                            testJwtEndpoints();
                                        }
                                    } else {
                                        Log.e(TAG, "✗ Re-authentication failed");
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                                    Log.e(TAG, "✗ Re-authentication network error", t);
                                }
                            });
                        } else {
                            Log.e(TAG, "✗ Failed to get fresh Firebase token");
                        }
                    });
        } else {
            Log.e(TAG, "✗ No Firebase user for re-authentication");
        }
    }

    /**
     * Clear all authentication data
     */
    public void clearAllAuth() {
        Log.d(TAG, "--- CLEARING ALL AUTH DATA ---");

        // Clear stored preferences
        preferencesHelper.clearUserData();

        // Sign out from Firebase
        firebaseAuth.signOut();

        Log.d(TAG, "✓ All authentication data cleared");
    }
}