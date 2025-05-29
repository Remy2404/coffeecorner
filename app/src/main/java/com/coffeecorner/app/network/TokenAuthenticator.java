package com.coffeecorner.app.network;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.coffeecorner.app.utils.PreferencesHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.Tasks;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * TokenAuthenticator handles automatic JWT token refresh when receiving 401 responses.
 * Implements OkHttp Authenticator interface to automatically refresh expired tokens
 * using Firebase authentication and backend re-authentication flow.
 */
public class TokenAuthenticator implements Authenticator {
    
    private static final String TAG = "TokenAuthenticator";
    private static final int MAX_RETRY_COUNT = 3;
    private static final long TIMEOUT_SECONDS = 30;
    
    private final Context context;
    private final PreferencesHelper preferencesHelper;
    private final FirebaseAuth firebaseAuth;
    private final String baseUrl;
    
    // Track retry attempts to prevent infinite loops
    private int retryCount = 0;
    
    /**
     * Constructor for TokenAuthenticator
     * 
     * @param context Application context for PreferencesHelper
     * @param baseUrl Base URL for creating temporary Retrofit instance
     */
    public TokenAuthenticator(@NonNull Context context, @NonNull String baseUrl) {
        this.context = context;
        this.baseUrl = baseUrl;
        this.preferencesHelper = new PreferencesHelper(context);
        this.firebaseAuth = FirebaseAuth.getInstance();
    }
    
    /**
     * Called when a request receives a 401 Unauthorized response.
     * Attempts to refresh the JWT token using Firebase authentication.
     * 
     * @param route The route that failed
     * @param response The response that indicated authentication failure
     * @return A new Request with updated authentication, or null if refresh failed
     */
    @Nullable
    @Override
    public Request authenticate(@Nullable Route route, @NonNull Response response) throws IOException {
        Log.d(TAG, "Authentication required - attempting token refresh");
        
        // Prevent infinite retry loops
        if (retryCount >= MAX_RETRY_COUNT) {
            Log.e(TAG, "Max retry attempts reached. Authentication failed.");
            retryCount = 0;
            return null;
        }
        
        retryCount++;
        
        // Check if we have a Firebase user
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "No Firebase user found. Cannot refresh token.");
            retryCount = 0;
            return null;
        }
        
        try {
            // Get fresh Firebase ID token
            String freshFirebaseToken = getFreshFirebaseToken(currentUser);
            if (freshFirebaseToken == null) {
                Log.e(TAG, "Failed to get fresh Firebase token");
                retryCount = 0;
                return null;
            }
            
            // Re-authenticate with backend using fresh Firebase token
            String newJwtToken = authenticateWithBackend(freshFirebaseToken);
            if (newJwtToken == null) {
                Log.e(TAG, "Failed to get new JWT token from backend");
                retryCount = 0;
                return null;
            }
            
            // Save new token
            preferencesHelper.saveAuthToken(newJwtToken);
            Log.d(TAG, "Successfully refreshed JWT token");
            
            // Reset retry count on success
            retryCount = 0;
            
            // Return the original request with new Authorization header
            return response.request().newBuilder()
                    .header("Authorization", "Bearer " + newJwtToken)
                    .build();
                    
        } catch (Exception e) {
            Log.e(TAG, "Error during token refresh: " + e.getMessage(), e);
            retryCount = 0;
            return null;
        }
    }
    
    /**
     * Gets a fresh Firebase ID token from the current user
     * 
     * @param user Current Firebase user
     * @return Fresh Firebase ID token, or null if failed
     */
    @Nullable
    private String getFreshFirebaseToken(@NonNull FirebaseUser user) {
        try {
            Log.d(TAG, "Requesting fresh Firebase token");
            
            // Force token refresh to get a fresh one
            return Tasks.await(user.getIdToken(true), TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .getToken();
                    
        } catch (Exception e) {
            Log.e(TAG, "Failed to get fresh Firebase token: " + e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Authenticates with backend using Firebase token to get new JWT
     * 
     * @param firebaseToken Fresh Firebase ID token
     * @return New JWT access token, or null if failed
     */
    @Nullable
    private String authenticateWithBackend(@NonNull String firebaseToken) {
        try {
            Log.d(TAG, "Authenticating with backend using Firebase token");
            
            // Create temporary Retrofit instance without authentication
            // to avoid circular dependency during token refresh
            Retrofit tempRetrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
              ApiService tempApiService = tempRetrofit.create(ApiService.class);
            
            // Call backend authentication endpoint using form-encoded method
            Call<AuthResponse> call = tempApiService.authenticateWithFirebase(firebaseToken);
            retrofit2.Response<AuthResponse> response = call.execute();
            
            if (response.isSuccessful() && response.body() != null) {
                AuthResponse authResponse = response.body();
                String accessToken = authResponse.getAccessToken();
                
                if (accessToken != null && !accessToken.isEmpty()) {
                    Log.d(TAG, "Successfully obtained new JWT token from backend");
                    return accessToken;
                } else {
                    Log.e(TAG, "Backend response missing access token");
                    return null;
                }
            } else {
                Log.e(TAG, "Backend authentication failed. Response code: " + response.code());
                if (response.errorBody() != null) {
                    Log.e(TAG, "Error body: " + response.errorBody().string());
                }
                return null;
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error authenticating with backend: " + e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Checks if the response indicates an authentication failure
     * 
     * @param response HTTP response to check
     * @return true if response indicates authentication failure
     */
    public static boolean isAuthenticationFailure(@NonNull Response response) {
        return response.code() == 401;
    }
    
    /**
     * Checks if a request already has been retried for authentication
     * This helps prevent infinite retry loops
     * 
     * @param request The request to check
     * @return true if request has been retried
     */
    public static boolean isRequestRetried(@NonNull Request request) {
        return request.header("X-Auth-Retry") != null;
    }
    
    /**
     * Marks a request as being retried for authentication
     * 
     * @param originalRequest The original request
     * @return Request with retry marker
     */
    public static Request markAsRetried(@NonNull Request originalRequest) {
        return originalRequest.newBuilder()
                .header("X-Auth-Retry", "true")
                .build();
    }
    
    /**
     * Resets the retry counter
     * Call this when starting a new authentication session
     */
    public void resetRetryCount() {
        retryCount = 0;
    }
    
    /**
     * Gets the current retry count
     * 
     * @return Current retry count
     */
    public int getRetryCount() {
        return retryCount;
    }
}