
package com.coffeecorner.app.utils;

import android.content.Context;
import android.util.Log;

import com.coffeecorner.app.models.User;
import com.coffeecorner.app.repositories.UserRepository;

public class UserProfileManager {
    private static final String TAG = "UserProfileManager";
    private Context context;
    private UserRepository userRepository;
    private PreferencesHelper preferencesHelper;

    public UserProfileManager(Context context) {
        this.context = context;
        this.userRepository = UserRepository.getInstance(context);
        this.preferencesHelper = new PreferencesHelper(context);
    }

    public interface ProfileDataCallback {
        void onSuccess(User user);

        void onError(String errorMessage);
    }

    public void fetchUserDataFromServer(ProfileDataCallback callback) {
        Log.d(TAG, "Fetching user data from server");

        userRepository.fetchUserProfileFromServer(new UserRepository.ProfileCallback() {
            @Override
            public void onSuccess(User user) {
                if (validateUserData(user)) {
                    saveUserDataToPreferences(user);
                    Log.d(TAG, "User data fetched from server and cached locally");
                    callback.onSuccess(user);
                } else {
                    Log.w(TAG, "Invalid user data received from server");
                    callback.onError("Invalid user data received from server");
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Failed to fetch user data from server: " + errorMessage);
                callback.onError(errorMessage);
            }
        });
    }

    public User loadUserDataFromPreferences() {
        Log.d(TAG, "Loading user data from local preferences");

        User user = userRepository.loadUserFromPreferences();
        if (user != null && validateUserData(user)) {
            Log.d(TAG, "Valid user data loaded from preferences");
            return user;
        } else {
            Log.w(TAG, "No valid user data found in preferences, creating default user");
            return createDefaultUser();
        }
    }

    public void saveUserDataToPreferences(User user) {
        if (user == null) {
            Log.w(TAG, "Cannot save null user data to preferences");
            return;
        }

        try {
            if (user.getId() != null)
                preferencesHelper.saveUserId(user.getId());
            if (user.getFullName() != null)
                preferencesHelper.saveUserLogin(user.getId(), user.getFullName(), user.getEmail(),
                        preferencesHelper.getAuthToken());
            if (user.getEmail() != null && user.getFullName() == null)
                preferencesHelper.saveUserLogin(user.getId(), preferencesHelper.getUserName(), user.getEmail(),
                        preferencesHelper.getAuthToken());
            if (user.getPhone() != null)
                preferencesHelper.saveUserPhone(user.getPhone());
            if (user.getGender() != null)
                preferencesHelper.saveUserGender(user.getGender());
            if (user.getPhotoUrl() != null)
                preferencesHelper.saveUserPhoto(user.getPhotoUrl());
            if (user.getDateOfBirth() != null)
                preferencesHelper.saveUserDateOfBirth(user.getDateOfBirth());

            Log.d(TAG, "User data saved to preferences successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error saving user data to preferences: " + e.getMessage());
        }
    }

    public boolean validateUserData(User user) {
        if (user == null) {
            Log.w(TAG, "User object is null");
            return false;
        }

        if (user.getId() == null || user.getId().trim().isEmpty()) {
            Log.w(TAG, "User ID is null or empty");
            return false;
        }

        Log.d(TAG, "User data validation passed");
        return true;
    }

    private User createDefaultUser() {
        String userId = preferencesHelper.getUserId();
        if (userId == null || userId.isEmpty()) {
            userId = "temp_user_" + System.currentTimeMillis();
        }

        User user = new User();
        user.setId(userId);
        user.setFullName(preferencesHelper.getUserName() != null ? preferencesHelper.getUserName() : "");
        user.setEmail(preferencesHelper.getUserEmail() != null ? preferencesHelper.getUserEmail() : "");
        user.setPhone(preferencesHelper.getUserPhone() != null ? preferencesHelper.getUserPhone() : "");
        user.setGender(preferencesHelper.getUserGender() != null ? preferencesHelper.getUserGender() : "other");
        user.setPhotoUrl(preferencesHelper.getUserProfilePic() != null ? preferencesHelper.getUserProfilePic() : "");
        user.setDateOfBirth(
                preferencesHelper.getUserDateOfBirth() != null ? preferencesHelper.getUserDateOfBirth() : "");

        Log.d(TAG, "Default user created with ID: " + userId);
        return user;
    }

    public void fetchWithFallback(ProfileDataCallback callback) {
        Log.d(TAG, "Starting fetch with fallback strategy");

        fetchUserDataFromServer(new ProfileDataCallback() {
            @Override
            public void onSuccess(User user) {
                Log.d(TAG, "Server fetch successful");
                callback.onSuccess(user);
            }

            @Override
            public void onError(String errorMessage) {
                Log.w(TAG, "Server fetch failed, falling back to local data: " + errorMessage);
                User localUser = loadUserDataFromPreferences();
                if (localUser != null) {
                    callback.onSuccess(localUser);
                } else {
                    callback.onError("Both server and local data unavailable");
                }
            }
        });
    }

    public void syncUserData() {
        Log.d(TAG, "Synchronizing user data");

        fetchUserDataFromServer(new ProfileDataCallback() {
            @Override
            public void onSuccess(User user) {
                Log.d(TAG, "User data synchronized successfully");
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Failed to synchronize user data: " + errorMessage);
            }
        });
    }

    public void clearLocalData() {
        Log.d(TAG, "Clearing local user data");
        preferencesHelper.clearUserData();
    }
}
