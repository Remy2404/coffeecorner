package com.coffeecorner.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.coffeecorner.app.models.User;
import com.coffeecorner.app.network.ApiResponse;
import com.coffeecorner.app.repositories.UserRepository;

/**
 * UserViewModel - Handles all user-related operations and data
 * Acts as a bridge between UI and UserRepository
 */
public class UserViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<ApiResponse<User>> profileUpdateResult = new MutableLiveData<>();

    public UserViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Get current user profile information
     * 
     * @return LiveData containing user details
     */
    public LiveData<User> getUserProfile() {
        return userRepository.getUserProfile();
    }

    /**
     * Check if user is authenticated
     * 
     * @return true if user is authenticated
     */
    public boolean isAuthenticated() {
        return userRepository.isUserAuthenticated();
    }

    /**
     * Update user profile information
     * 
     * @param user Updated user data
     */
    public void updateUserProfile(User user) {
        isLoading.setValue(true);
        userRepository.updateUserProfile(user, new UserRepository.ProfileUpdateCallback() {
            @Override
            public void onSuccess(User updatedUser) {
                isLoading.postValue(false);
                profileUpdateResult.postValue(new ApiResponse<>(true, "Profile updated successfully", updatedUser));
            }

            @Override
            public void onError(String message) {
                isLoading.postValue(false);
                profileUpdateResult.postValue(new ApiResponse<>(false, message, null));
            }
        });
    }

    /**
     * Logout the current user
     */
    public void logout() {
        userRepository.logout();
    }

    /**
     * Get the loading state
     * 
     * @return LiveData indicating if an operation is in progress
     */
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    /**
     * Get profile update operation result
     * 
     * @return LiveData containing the result of profile update
     */
    public LiveData<ApiResponse<User>> getProfileUpdateResult() {
        return profileUpdateResult;
    }
}
