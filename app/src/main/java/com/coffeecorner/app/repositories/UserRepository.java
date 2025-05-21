package com.coffeecorner.app.repositories;

import android.content.Context;
import android.util.Log; // Added for logging

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.coffeecorner.app.models.User;
import com.coffeecorner.app.network.ApiService;
import com.coffeecorner.app.network.RetrofitClient;
import com.coffeecorner.app.network.ApiResponse;
import com.coffeecorner.app.utils.PreferencesHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * UserRepository - Single source of truth for user data
 * Manages user authentication, registration, and profile
 */
public class UserRepository {

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
        // TODO: Replace with actual User model for login request if different
        User loginRequest = new User(); // Or a specific LoginRequest model
        loginRequest.setEmail(email);
        // loginRequest.setPassword(password); // Assuming password is part of the
        // request

        apiService.login(email, password).enqueue(new Callback<ApiResponse<User>>() { // Corrected API call and callback type
            @Override
            public void onResponse(@NonNull Call<ApiResponse<User>> call, @NonNull Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getData();
                    currentUser.setValue(user);
                    // preferencesHelper.saveUserId(user.getId()); // Save user ID upon successful
                    // login
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
     * @param name     User name
     * @param email    User email
     * @param password User password
     * @param recaptchaToken The reCAPTCHA token
     * @param callback Callback to handle result
     */
    public void register(String name, String email, String password, String recaptchaToken, @NonNull AuthCallback callback) {
        // User registrationRequest = new User(); // Or a specific RegisterRequest model
        // registrationRequest.setName(name);
        // registrationRequest.setEmail(email);
        // registrationRequest.setPassword(password); // Assuming password is part of
        // the request

        apiService.register(name, email, password, recaptchaToken).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<User>> call, @NonNull Response<ApiResponse<User>> response) {
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
        // TODO: Add API call for server-side logout if necessary
        // apiService.logoutUser().enqueue(...);
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
            public void onResponse(@NonNull Call<ApiResponse<User>> call, @NonNull Response<ApiResponse<User>> response) {
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
            public void onResponse(@NonNull Call<ApiResponse<User>> call, @NonNull Response<ApiResponse<User>> response) {
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
     * @param email User email
     * @param callback Callback to handle result
     */
    public void requestPasswordReset(String email, @NonNull ResetCallback callback) {
        if (email == null || email.isEmpty()) {
            callback.onError("Email cannot be null or empty.");
            return;
        }
        apiService.requestPasswordReset(email).enqueue(new Callback<ApiResponse<Void>>() { // Corrected API call
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Void>> call, @NonNull Response<ApiResponse<Void>> response) {
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
        // TODO: Need to create a specific API method for change password if not already
        // available or if the existing one in ApiService doesn't match.
        // Based on ApiService, `changePassword` takes userId, oldPassword, newPassword.
        apiService.changePassword(userId, oldPassword, newPassword).enqueue(new Callback<ApiResponse<Void>>() { // Corrected API call
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Void>> call, @NonNull Response<ApiResponse<Void>> response) {
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
}
