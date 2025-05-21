package com.coffeecorner.app.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.coffeecorner.app.models.User;
import com.coffeecorner.app.repositories.UserRepository;
import com.coffeecorner.app.utils.PreferencesHelper; // Keep for managing login state locally

/**
 * UserViewModel - Manages and provides user data to the UI
 * Handles login, registration, profile management
 */
public class UserViewModel extends AndroidViewModel {

    private final UserRepository userRepository;
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final PreferencesHelper preferencesHelper; // To manage stored user ID for auto-login

    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = UserRepository.getInstance(application.getApplicationContext());
        preferencesHelper = new PreferencesHelper(application.getApplicationContext());

        // Try to load user from preferences if they're already logged in
        String userId = preferencesHelper.getUserId();
        if (userId != null && !userId.isEmpty()) {
            loadUserData(userId);
        }
    }

    /**
     * Login user with email and password
     * 
     * @param email      User email
     * @param password   User password
     * @param rememberMe Whether to remember the user
     */
    public void login(String email, String password, boolean rememberMe) {
        isLoading.setValue(true);
        userRepository.login(email, password, new UserRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                if (rememberMe) {
                    preferencesHelper.saveUserId(user.getId());
                }
                currentUser.setValue(user);
                isLoading.setValue(false);
                errorMessage.setValue(null); // Clear previous errors
            }

            @Override
            public void onError(String errorMsg) {
                errorMessage.setValue(errorMsg);
                isLoading.setValue(false);
            }
        });
    }

    /**
     * Register a new user
     * 
     * @param name     User name
     * @param email    User email
     * @param password User password
     */
    public void register(String name, String email, String password) {
        isLoading.setValue(true);
        userRepository.register(name, email, password, new UserRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                // Automatically log in the user by saving their ID
                preferencesHelper.saveUserId(user.getId());
                currentUser.setValue(user);
                isLoading.setValue(false);
                errorMessage.setValue(null); // Clear previous errors
            }

            @Override
            public void onError(String errorMsg) {
                errorMessage.setValue(errorMsg);
                isLoading.setValue(false);
            }
        });
    }

    /**
     * Log out the current user
     */
    public void logout() {
        // Clear local preferences
        preferencesHelper.clearUserId();
        // Clear LiveData
        currentUser.setValue(null);
        // Optionally, call repository to clear server-side session if applicable
        // userRepository.logout(); // If you add a logout method to UserRepository
        errorMessage.setValue(null); // Clear previous errors
    }

    /**
     * Update user profile information
     * 
     * @param name     User name
     * @param email    User email
     * @param photoUrl User photo URL
     */
    public void updateProfile(String name, String email, String photoUrl) {
        User userToUpdate = currentUser.getValue();
        if (userToUpdate != null) {
            isLoading.setValue(true);
            // Create a new User object or update the existing one for the request
            User updatedInfo = new User();
            updatedInfo.setId(userToUpdate.getId()); // Important: keep the ID
            updatedInfo.setName(name);
            updatedInfo.setEmail(email);
            if (photoUrl != null) {
                updatedInfo.setPhotoUrl(photoUrl);
            }
            // Copy other fields if necessary from userToUpdate to updatedInfo

            userRepository.updateProfile(updatedInfo, new UserRepository.UserCallback() {
                @Override
                public void onUserLoaded(User user) {
                    currentUser.setValue(user);
                    isLoading.setValue(false);
                    errorMessage.setValue(null); // Clear previous errors
                }

                @Override
                public void onError(String errorMsg) {
                    errorMessage.setValue(errorMsg);
                    isLoading.setValue(false);
                }
            });
        } else {
            errorMessage.setValue("Cannot update profile. No user logged in.");
        }
    }

    /**
     * Change user password
     * 
     * @param currentPassword Current password
     * @param newPassword     New password
     */
    public void changePassword(String currentPassword, String newPassword) {
        User user = currentUser.getValue();
        if (user == null || user.getId() == null) {
            errorMessage.setValue("User not logged in. Cannot change password.");
            return;
        }
        isLoading.setValue(true);
        userRepository.changePassword(user.getId(), currentPassword, newPassword, new UserRepository.ResetCallback() {
            @Override
            public void onSuccess(String message) {
                // Optionally, display success message via LiveData
                // successMessage.setValue(message);
                isLoading.setValue(false);
                errorMessage.setValue(null); // Clear previous errors
            }

            @Override
            public void onError(String errorMsg) {
                errorMessage.setValue(errorMsg);
                isLoading.setValue(false);
            }
        });
    }

    /**
     * Request password reset for a user
     * 
     * @param email User email
     */
    public void requestPasswordReset(String email) {
        isLoading.setValue(true);
        userRepository.requestPasswordReset(email, new UserRepository.ResetCallback() {
            @Override
            public void onSuccess(String message) {
                // Display success message, e.g., via a new LiveData<String> successMessage
                errorMessage.setValue(message); // Or use a dedicated success LiveData
                isLoading.setValue(false);
            }

            @Override
            public void onError(String errorMsg) {
                errorMessage.setValue(errorMsg);
                isLoading.setValue(false);
            }
        });
    }

    /**
     * Load user data from the repository if userId is available (e.g., from
     * preferences)
     * 
     * @param userId User ID to load
     */
    private void loadUserData(String userId) {
        isLoading.setValue(true);
        userRepository.getUserById(userId, new UserRepository.UserCallback() {
            @Override
            public void onUserLoaded(User user) {
                currentUser.setValue(user);
                isLoading.setValue(false);
                errorMessage.setValue(null); // Clear previous errors
            }

            @Override
            public void onError(String errorMsg) {
                // If loading user data fails (e.g., token expired, user deleted)
                // Clear stored userId and treat as logged out
                preferencesHelper.clearUserId();
                currentUser.setValue(null);
                errorMessage.setValue("Session expired. Please log in again."); // More specific error
                isLoading.setValue(false);
            }
        });
    }

    // Getters for LiveData
    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
}
