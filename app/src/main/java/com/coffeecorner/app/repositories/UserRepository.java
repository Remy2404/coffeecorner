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

        apiService.loginUser(loginRequest).enqueue(new Callback<User>() { // Assuming loginUser returns User
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
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
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
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
     * @param callback Callback to handle result
     */
    public void register(String name, String email, String password, @NonNull AuthCallback callback) {
        User registrationRequest = new User(); // Or a specific RegisterRequest model
        registrationRequest.setName(name);
        registrationRequest.setEmail(email);
        // registrationRequest.setPassword(password); // Assuming password is part of
        // the request

        apiService.registerUser(registrationRequest).enqueue(new Callback<User>() { // Assuming registerUser returns
                                                                                    // User
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
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
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
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
        apiService.getUserProfile(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onUserLoaded(response.body());
                } else {
                    Log.e("UserRepository", "Get user by ID failed: " + response.code() + " - " + response.message());
                    callback.onError("Failed to load user profile.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e("UserRepository", "Get user by ID network error", t);
                callback.onError("Network error. Please try again. " + t.getMessage());
            }
        });
    }

    /**
     * Update user profile
     *
     * @param user     Updated user object
     * @param callback Callback to handle result
     */
    public void updateProfile(User user, @NonNull UserCallback callback) {
        if (user == null || user.getId() == null || user.getId().isEmpty()) {
            callback.onError("User or User ID cannot be null or empty for update.");
            return;
        }
        // Assuming user.getId() is the correct identifier for the API endpoint
        apiService.updateUserProfile(user.getId(), user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User updatedUser = response.body();
                    currentUser.setValue(updatedUser); // Update local LiveData
                    callback.onUserLoaded(updatedUser);
                } else {
                    Log.e("UserRepository", "Update profile failed: " + response.code() + " - " + response.message());
                    callback.onError("Failed to update profile.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
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
        // Assuming a simple request body, adjust if your API needs more
        // For example, if it's a POST request with a JSON body:
        // class PasswordResetRequest { String email; }
        // PasswordResetRequest request = new PasswordResetRequest();
        // request.email = email;
        // apiService.requestPasswordReset(request).enqueue(...)

        // If it's a GET request or simple POST with email in path/query:
        apiService.forgotPassword(email).enqueue(new Callback<Void>() { // Assuming API returns Void or a simple success
                                                                        // message
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess("Password reset link sent to your email.");
                } else {
                    Log.e("UserRepository",
                            "Password reset request failed: " + response.code() + " - " + response.message());
                    callback.onError("Failed to request password reset.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e("UserRepository", "Password reset network error", t);
                callback.onError("Network error. Please try again. " + t.getMessage());
            }
        });
    }

    /**
     * Change user password
     *
     * @param oldPassword Current password
     * @param newPassword New password
     * @param callback    Callback to handle result
     */
    public void changePassword(String userId, String oldPassword, String newPassword, @NonNull ResetCallback callback) {
        // TODO: Create a proper request model if your API expects a JSON body
        // class ChangePasswordRequest { String oldPassword; String newPassword; }
        // ChangePasswordRequest request = new ChangePasswordRequest();
        // request.oldPassword = oldPassword;
        // request.newPassword = newPassword;
        // apiService.changePassword(userId, request).enqueue(...)

        // This is a placeholder, adjust based on your ApiService.changePassword
        // signature
        // For example, if it takes userId in path and a request body:
        // apiService.changePassword(userId, new ChangePasswordRequest(oldPassword,
        // newPassword)).enqueue...

        // Assuming a simplified call for now, replace with actual implementation
        // This will likely require a specific request body model
        Log.d("UserRepository", "Change password called. UserID: " + userId); // Placeholder
        // Simulate API call for now
        if (userId != null && !oldPassword.isEmpty() && !newPassword.isEmpty()) {
            // apiService.changePassword(userId, oldPassword, newPassword) // Adjust this
            // call
            // .enqueue(new Callback<Void>() {
            // @Override
            // public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void>
            // response) {
            // if (response.isSuccessful()) {
            // callback.onSuccess("Password changed successfully");
            // } else {
            // Log.e("UserRepository", "Change password failed: " + response.code() + " - "
            // + response.message());
            // callback.onError("Failed to change password.");
            // }
            // }
            //
            // @Override
            // public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
            // Log.e("UserRepository", "Change password network error", t);
            // callback.onError("Network error. Please try again. " + t.getMessage());
            // }
            // });
            callback.onSuccess("Password change simulated successfully. Implement API call."); // Placeholder
        } else {
            callback.onError("User ID, old password, or new password cannot be empty.");
        }
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
