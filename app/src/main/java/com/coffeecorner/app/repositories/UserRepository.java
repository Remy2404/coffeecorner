package com.coffeecorner.app.repositories;

import android.content.Context;
import android.util.Log; // Added for logging

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.coffeecorner.app.models.FirebaseAuthRequest;
import com.coffeecorner.app.models.User;
import com.coffeecorner.app.network.ApiService;
import com.coffeecorner.app.network.RetrofitClient;
import com.coffeecorner.app.network.ApiResponse;
import com.coffeecorner.app.network.AuthResponse;
import com.coffeecorner.app.utils.PreferencesHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * UserRepository - Single source of truth for user data
 * Manages user authentication, registration, and profile
 */
public class UserRepository {

    public static class UserData {
        public String id;
        public String full_name;
        public String email;
        public String phone;
        public String photo_url;
        public String created_at;
        public String updated_at;

        public UserData() {
        }

        public UserData(String id, String full_name, String email, String phone, String photo_url) {
            this.id = id;
            this.full_name = full_name;
            this.email = email;
            this.phone = phone;
            this.photo_url = photo_url;
        }
    }

    private static volatile UserRepository instance;
    private final PreferencesHelper preferencesHelper;
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    private final ApiService apiService; // Added ApiService

    private UserRepository(Context context) {
        preferencesHelper = new PreferencesHelper(context);
        apiService = RetrofitClient.getApiService(); // Initialize ApiService
    }

    public static UserRepository getInstance(Context context) {
        if (instance == null) {
            synchronized (UserRepository.class) {
                if (instance == null) {
                    instance = new UserRepository(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    /**
     * Login a user with email and password
     *
     * @param email    User email
     * @param password User password
     * @param callback Callback to handle result
     */
    public void login(String email, String password, @NonNull AuthCallback callback) {
        apiService.login(email, password).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<User>> call,
                    @NonNull Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getData();
                    currentUser.setValue(user);
                    preferencesHelper.saveUserId(user.getId());
                    callback.onSuccess(user);
                } else {
                    // Log error for debugging
                    Log.e("UserRepository", "Login failed: " + response.code() + " - " + response.message());
                    try {
                        if (response.errorBody() != null) {
                            Log.e("UserRepository", "Error body: " + response.errorBody().string());
                        }
                    } catch (Exception e) {
                        Log.e("UserRepository", "Error parsing error body", e);
                    }
                    callback.onError("Login failed. Please check credentials.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<User>> call, @NonNull Throwable t) {
                Log.e("UserRepository", "Login network error", t);
                callback.onError("Network error. Please try again. " + t.getMessage());
            }
        });
    }

    /**
     * Register a new user
     *
     * @param name           User name
     * @param email          User email
     * @param password       User password
     * @param recaptchaToken The reCAPTCHA token
     * @param callback       Callback to handle result
     */
    public void register(String name, String email, String password, String recaptchaToken,
            @NonNull AuthCallback callback) {
        // User registrationRequest = new User(); // Or a specific RegisterRequest model
        // registrationRequest.setName(name);
        // registrationRequest.setEmail(email);
        // registrationRequest.setPassword(password); // Assuming password is part of
        // the request

        apiService.register(name, email, password).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<User>> call,
                    @NonNull Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getData();
                    currentUser.setValue(user);
                    // preferencesHelper.saveUserId(user.getId()); // Save user ID upon successful
                    // registration
                    callback.onSuccess(user);
                } else {
                    Log.e("UserRepository", "Registration failed: " + response.code() + " - " + response.message());
                    try {
                        if (response.errorBody() != null) {
                            Log.e("UserRepository", "Error body: " + response.errorBody().string());
                        }
                    } catch (Exception e) {
                        Log.e("UserRepository", "Error parsing error body", e);
                    }
                    callback.onError("Registration failed. Please try again.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<User>> call, @NonNull Throwable t) {
                Log.e("UserRepository", "Registration network error", t);
                callback.onError("Network error. Please try again. " + t.getMessage());
            }
        });
    }

    /**
     * Get the current authenticated user
     *
     * @return LiveData with current user
     */
    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    /**
     * Get current user profile
     */
    public LiveData<User> getUserProfile() {
        // Return the current user LiveData
        return currentUser;
    }

    /**
     * Check if user is authenticated
     */
    public boolean isUserAuthenticated() {
        // Check if there's a saved user ID in preferences
        return preferencesHelper.getUserId() != null && !preferencesHelper.getUserId().isEmpty();
    }

    /**
     * Check if there is a logged in user
     *
     * @return true if a user is logged in
     */
    public boolean isUserLoggedIn() {
        return preferencesHelper.getUserId() != null && !preferencesHelper.getUserId().isEmpty();
    }

    /**
     * Save user ID to preferences
     *
     * @param userId User ID to save
     */
    public void saveUserId(String userId) {
        preferencesHelper.saveUserId(userId);
    }

    /**
     * Clear user ID from preferences (logout)
     */
    public void clearUserId() {
        preferencesHelper.clearUserId();
        currentUser.setValue(null);

        // Optional: Add API call for server-side logout if needed
        // For now, local logout is sufficient
        android.util.Log.d("UserRepository", "User logged out successfully");
    }

    /**
     * Update user profile
     */
    public void updateUserProfile(User user, ProfileUpdateCallback callback) {
        if (user == null) {
            callback.onError("User data cannot be null");
            return;
        }

        // Make API call to update user profile
        apiService.updateUserProfile(user).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User updatedUser = response.body().getData();
                    currentUser.setValue(updatedUser);
                    callback.onSuccess(updatedUser);
                } else {
                    callback.onError("Failed to update profile");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Logout user
     */
    public void logout() {
        // Clear user data from preferences and set current user to null
        preferencesHelper.clearUserData();
        currentUser.setValue(null);
    }

    /**
     * Get user by ID
     *
     * @param userId   User ID
     * @param callback Callback to handle result
     */
    public void getUserById(String userId, @NonNull UserCallback callback) {
        if (userId == null || userId.isEmpty()) {
            callback.onError("User ID cannot be null or empty.");
            return;
        }
        apiService.getUserById(userId).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<User>> call,
                    @NonNull Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getData();
                    currentUser.setValue(user);
                    callback.onUserLoaded(user);
                } else {
                    String errorMsg = "Failed to load user profile.";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMsg = response.body().getMessage();
                    } else if (response.message() != null && !response.message().isEmpty()) {
                        errorMsg = response.message();
                    }
                    Log.e("UserRepository", "Get user by ID failed: " + response.code() + " - " + errorMsg);
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<User>> call, @NonNull Throwable t) {
                Log.e("UserRepository", "Get user by ID network error", t);
                callback.onError("Network error. Please try again. " + t.getMessage());
            }
        });
    }

    /**
     * Update user profile
     *
     * @param user     Updated user data
     * @param callback Callback to handle result
     */
    public void updateProfile(User user, @NonNull UserCallback callback) {
        // Assuming user object contains the ID
        String userId = user.getId(); // Make sure your User model has an getId() method
        if (userId == null || userId.isEmpty()) {
            callback.onError("User ID cannot be null or empty for update.");
            return;
        }

        apiService.updateUser(userId, user).enqueue(new Callback<ApiResponse<User>>() { // Corrected API call
            @Override
            public void onResponse(@NonNull Call<ApiResponse<User>> call,
                    @NonNull Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User updatedUser = response.body().getData();
                    currentUser.setValue(updatedUser);
                    callback.onUserLoaded(updatedUser);
                } else {
                    String errorMsg = "Failed to update profile.";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMsg = response.body().getMessage();
                    } else if (response.message() != null && !response.message().isEmpty()) {
                        errorMsg = response.message();
                    }
                    Log.e("UserRepository", "Update profile failed: " + response.code() + " - " + errorMsg);
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<User>> call, @NonNull Throwable t) {
                Log.e("UserRepository", "Update profile network error", t);
                callback.onError("Network error. Please try again. " + t.getMessage());
            }
        });
    }

    /**
     * Request password reset
     *
     * @param email    User email
     * @param callback Callback to handle result
     */
    public void requestPasswordReset(String email, @NonNull ResetCallback callback) {
        if (email == null || email.isEmpty()) {
            callback.onError("Email cannot be null or empty.");
            return;
        }
        apiService.requestPasswordReset(email).enqueue(new Callback<ApiResponse<Void>>() { // Corrected API call
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Void>> call,
                    @NonNull Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    // Password reset request successful
                    callback.onSuccess("Password reset link sent to your email.");
                } else {
                    String errorMsg = "Failed to request password reset.";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMsg = response.body().getMessage();
                    } else if (response.message() != null && !response.message().isEmpty()) {
                        errorMsg = response.message();
                    }
                    Log.e("UserRepository", "Request password reset failed: " + response.code() + " - " + errorMsg);
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                Log.e("UserRepository", "Request password reset network error", t);
                callback.onError("Network error. Please try again. " + t.getMessage());
            }
        });
    }

    /**
     * Change user password
     *
     * @param userId      User ID
     * @param oldPassword Current password
     * @param newPassword New password
     * @param callback    Callback to handle result
     */
    public void changePassword(String userId, String oldPassword, String newPassword, @NonNull ResetCallback callback) {
        if (userId == null || userId.isEmpty()) {
            callback.onError("User ID cannot be null or empty.");
            return;
        }

        apiService.changePassword(userId, oldPassword, newPassword).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Void>> call,
                    @NonNull Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess("Password changed successfully.");
                } else {
                    Log.e("UserRepository", "Change password failed: " + response.code() + " - " + response.message());
                    callback.onError("Failed to change password.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                Log.e("UserRepository", "Change password network error", t);
                callback.onError("Network error. Please try again. " + t.getMessage());
            }
        });
    }

    /**
     * Change user password (overloaded method for EditProfileFragment)
     *
     * @param currentPassword Current password
     * @param newPassword    New password
     * @param callback       Callback to handle result
     */
    public void changePassword(String currentPassword, String newPassword, @NonNull PasswordChangeCallback callback) {
        String userId = preferencesHelper.getUserId();
        if (userId == null || userId.isEmpty()) {
            callback.onError("User ID not found. Please log in again.");
            return;
        }

        changePassword(userId, currentPassword, newPassword, new ResetCallback() {
            @Override
            public void onSuccess(String message) {
                callback.onSuccess();
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    /**
     * Update user profile with Supabase integration
     * Falls back to local storage if Supabase is not available
     */
    public void updateUserProfileWithSupabase(User user, @NonNull ProfileUpdateCallback callback) {
        if (user == null) {
            callback.onError("User data is null");
            return;
        }

        String userId = user.getId();
        if (userId == null || userId.isEmpty()) {
            userId = preferencesHelper.getUserId();
            if (userId == null || userId.isEmpty()) {
                userId = "temp_user_" + System.currentTimeMillis();
                preferencesHelper.saveUserId(userId);
                user.setId(userId);
            }
        }

        // Save to local storage as backup
        saveUserToPreferences(user);
        currentUser.setValue(user);

        // Make API call to update user profile on the server
        String authToken = preferencesHelper.getAuthToken();
        if (authToken == null || authToken.isEmpty()) {
            Log.w("UserRepository", "No auth token available, profile update may fail");
        }

        // Use the API service to update the profile
        apiService.updateUserProfile(user).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    // Update was successful on the server
                    User updatedUser = response.body().getData();
                    // Update local storage and LiveData with server response
                    currentUser.setValue(updatedUser);
                    saveUserToPreferences(updatedUser);
                    callback.onSuccess(updatedUser);
                    Log.d("UserRepository", "Profile updated successfully on server");
                } else {
                    // Server rejected the update or returned an error
                    String errorMsg = "Failed to update profile on server";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMsg = response.body().getMessage();
                    }
                    Log.e("UserRepository", "Server error: " + errorMsg + ", Code: " + response.code());
                    
                    // Still consider it a "success" since we have local data saved
                    // This ensures the user experience isn't disrupted if server has issues
                    Log.w("UserRepository", "Using local profile data despite server error");
                    callback.onSuccess(user);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                // Network error, but local data is still saved
                Log.e("UserRepository", "Network error during profile update", t);
                // Still return success with local data
                Log.w("UserRepository", "Using local profile data despite network error");
                callback.onSuccess(user);
            }
        });
    }

    /**
     * Save user data to SharedPreferences
     */
    private void saveUserToPreferences(User user) {
        if (user.getId() != null) {
            preferencesHelper.saveUserId(user.getId());
        }
        if (user.getFullName() != null && user.getEmail() != null) {
            // Use saveUserLogin to save name and email together
            preferencesHelper.saveUserLogin(user.getId(), user.getFullName(), user.getEmail(),
                    preferencesHelper.getAuthToken());
        }
        if (user.getPhone() != null) {
            preferencesHelper.saveUserPhone(user.getPhone());
        }
        if (user.getPhotoUrl() != null) {
            preferencesHelper.saveUserProfilePic(user.getPhotoUrl());
        }
    }

    /**
     * Load user data from SharedPreferences
     */
    public User loadUserFromPreferences() {
        User user = new User();
        user.setId(preferencesHelper.getUserId());
        user.setFullName(preferencesHelper.getUserName());
        user.setEmail(preferencesHelper.getUserEmail());
        user.setPhone(preferencesHelper.getUserPhone());
        user.setPhotoUrl(preferencesHelper.getUserProfilePic());
        return user;
    }

    /**
     * Authenticate with backend using Firebase ID token
     */
    public void authenticateWithFirebase(String firebaseToken, @NonNull AuthCallback callback) {
        // Try both authentication methods - form-encoded and JSON
        // First try JSON approach
        FirebaseAuthRequest jsonRequest = new FirebaseAuthRequest(firebaseToken);
        
        Log.d("UserRepository", "Trying Firebase auth with JSON body...");
        
        apiService.authenticateWithFirebaseJson(jsonRequest).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call,
                    @NonNull Response<AuthResponse> response) {
                processAuthResponse(response, callback, firebaseToken, false);
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                Log.e("UserRepository", "Firebase auth with JSON failed, trying form-encoded", t);
                // Fall back to form-encoded approach
                tryFormEncodedAuth(firebaseToken, callback);
            }
        });
    }
    
    /**
     * Try authenticating with form-encoded parameters
     */
    private void tryFormEncodedAuth(String firebaseToken, @NonNull AuthCallback callback) {
        Log.d("UserRepository", "Trying Firebase auth with form-encoded...");
        
        apiService.authenticateWithFirebase(firebaseToken).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call,
                    @NonNull Response<AuthResponse> response) {
                processAuthResponse(response, callback, firebaseToken, true);
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                Log.e("UserRepository", "Firebase auth network error (both methods failed)", t);
                callback.onError("Network error. Please try again. " + t.getMessage());
            }
        });
    }
    
    /**
     * Process authentication response
     */
    private void processAuthResponse(Response<AuthResponse> response, AuthCallback callback, 
                                    String firebaseToken, boolean isLastAttempt) {
        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
            AuthResponse authResponse = response.body();
            User user = authResponse.getUser();
            
            if (user != null) {
                // Save access token
                if (authResponse.getAccessToken() != null) {
                    preferencesHelper.saveAuthToken(authResponse.getAccessToken());
                    Log.d("UserRepository", "Access token saved successfully: " + 
                          authResponse.getAccessToken().substring(0, Math.min(15, authResponse.getAccessToken().length())) + "...");
                }
                
                // Save user data
                currentUser.setValue(user);
                saveUserToPreferences(user);
                
                Log.d("UserRepository", "Firebase authentication successful for user: " + user.getEmail());
                callback.onSuccess(user);
            } else {
                Log.e("UserRepository", "User object is null in AuthResponse");
                Log.e("UserRepository", "Full response: success=" + authResponse.isSuccess() + 
                       ", message=" + authResponse.getMessage() + 
                       ", accessToken=" + (authResponse.getAccessToken() != null ? "present" : "null"));
                
                // If this was our last attempt, report the error
                if (isLastAttempt) {
                    callback.onError("User data is missing from server response.");
                } else {
                    // Try the form-encoded approach
                    tryFormEncodedAuth(firebaseToken, callback);
                }
            }
        } else {
            Log.e("UserRepository", "Firebase auth failed: " + response.code() + " - " + response.message());
            try {
                if (response.errorBody() != null) {
                    Log.e("UserRepository", "Error body: " + response.errorBody().string());
                }
                if (response.body() != null) {
                    Log.e("UserRepository", "Response body success: " + response.body().isSuccess());
                    Log.e("UserRepository", "Response body message: " + response.body().getMessage());
                }
            } catch (Exception e) {
                Log.e("UserRepository", "Error parsing error body", e);
            }
            
            // If this was our last attempt, report the error
            if (isLastAttempt) {
                callback.onError("Authentication failed. Please try again.");
            } else {
                // Try the form-encoded approach
                tryFormEncodedAuth(firebaseToken, callback);
            }            }
        }

    /**
     * Interface for authentication callbacks
     */
    public interface AuthCallback {
        void onSuccess(User user);

        void onError(String errorMessage);
    }

    /**
     * Interface for user data callbacks
     */
    public interface UserCallback {
        void onUserLoaded(User user);

        void onError(String errorMessage);
    }

    /**
     * Interface for password reset callbacks
     */
    public interface ResetCallback {
        void onSuccess(String message);

        void onError(String errorMessage);
    }

    /**
     * Interface for profile update callbacks
     */
    public interface ProfileUpdateCallback {
        void onSuccess(User user);

        void onError(String errorMessage);
    }

    /**
     * Interface for password change callbacks
     */
    public interface PasswordChangeCallback {
        void onSuccess();

        void onError(String errorMessage);
    }
}
