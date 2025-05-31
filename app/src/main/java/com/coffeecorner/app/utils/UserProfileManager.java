package com.coffeecorner.app.utils;

import android.content.Context;
import android.util.Log;

import com.coffeecorner.app.models.User;
import com.coffeecorner.app.repositories.UserRepository;

public class UserProfileManager {
    private static final String TAG = "UserProfileManager";
    
    private Context context;
    private PreferencesHelper preferencesHelper;
    private UserRepository userRepository;
    
    public UserProfileManager(Context context) {
        this.context = context;
        this.preferencesHelper = new PreferencesHelper(context);
        this.userRepository = UserRepository.getInstance(context);
    }
    
    public void syncUserData() {
        String userId = preferencesHelper.getUserId();
        if (userId == null || userId.isEmpty()) {
            Log.w(TAG, "No user ID found, cannot sync user data");
            return;
        }
        
        userRepository.getUserById(userId, new UserRepository.UserCallback() {
            @Override
            public void onUserLoaded(User user) {
                if (user != null) {
                    updateLocalUserData(user);
                    Log.d(TAG, "User data synchronized successfully");
                } else {
                    Log.w(TAG, "User data not found on server");
                }
            }
            
            @Override
            public void onError(String error) {
                Log.e(TAG, "Failed to sync user data: " + error);
            }
        });
    }
    
    private void updateLocalUserData(User user) {
        saveUserToPreferences(user);
    }
    
    private void saveUserToPreferences(User user) {
        if (user == null) return;
        
        if (user.getId() != null) {
            preferencesHelper.saveUserId(user.getId());
        }
        if (user.getFullName() != null && user.getEmail() != null) {
            preferencesHelper.saveUserLogin(user.getId(), user.getFullName(), user.getEmail(),
                    preferencesHelper.getAuthToken());
        }
        if (user.getPhone() != null) {
            preferencesHelper.saveUserPhone(user.getPhone());
        }
        if (user.getPhotoUrl() != null) {
            preferencesHelper.saveUserProfilePic(user.getPhotoUrl());
        }
        if (user.getGender() != null) {
            preferencesHelper.saveUserGender(user.getGender());
        }
        if (user.getDateOfBirth() != null) {
            preferencesHelper.saveUserDateOfBirth(user.getDateOfBirth());
        }
    }
    
    public User getCurrentUser() {
        return loadUserFromPreferences();
    }
    
    private User loadUserFromPreferences() {
        User user = new User();
        user.setId(preferencesHelper.getUserId());
        user.setFullName(preferencesHelper.getUserName());
        user.setEmail(preferencesHelper.getUserEmail());
        user.setPhone(preferencesHelper.getUserPhone());
        user.setPhotoUrl(preferencesHelper.getUserProfilePic());
        user.setGender(preferencesHelper.getUserGender());
        user.setDateOfBirth(preferencesHelper.getUserDateOfBirth());
        return user;
    }
    
    public void clearUserData() {
        preferencesHelper.clearUserData();
    }
    
    public boolean isUserLoggedIn() {
        return preferencesHelper.isLoggedIn();
    }
}
