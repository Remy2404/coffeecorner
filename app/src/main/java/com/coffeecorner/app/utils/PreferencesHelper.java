package com.coffeecorner.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Helper class to manage app preferences
 */
public class PreferencesHelper {
    // Shared Preferences file name
    private static final String PREF_NAME = "CoffeeCornerPrefs"; // Preference keys
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_PHONE = "user_phone";
    private static final String KEY_USER_PROFILE_PIC = "user_profile_pic";
    private static final String KEY_USER_POINTS = "user_points";
    private static final String KEY_USER_TIER = "user_tier";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    // Default values
    private static final String DEFAULT_USER_ID = "user1"; // Default user ID for API calls
    private static final String KEY_AUTH_TOKEN = "auth_token";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_NOTIFICATIONS_ENABLED = "notifications_enabled";
    private static final String KEY_LAST_ORDER_ID = "last_order_id";
    private static final String KEY_SAVED_ADDRESS = "saved_address";
    private static final String KEY_DEFAULT_PAYMENT = "default_payment";
    private static final String KEY_FIRST_TIME_USER = "first_time_user";
    private static final String KEY_ONBOARDING_COMPLETED = "onboarding_completed";
    private static final String KEY_SELECTED_LOCATION = "selected_location";
    private static final String KEY_LOYALTY_POINTS = "loyalty_points";
    private static final String KEY_PROMO_CODE = "promo_code";
    private static final String KEY_USER_GENDER = "user_gender";
    private static final String KEY_USER_DATE_OF_BIRTH = "user_date_of_birth";

    private final SharedPreferences sharedPreferences;

    public PreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Save user login information
     * 
     * @param userId    User ID
     * @param name      User name
     * @param email     User email
     * @param authToken Authentication token
     */
    public void saveUserLogin(String userId, String name, String email, String authToken) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_AUTH_TOKEN, authToken);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    /**
     * Log out user by clearing login information
     */
    public void clearUserLogin() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USER_NAME);
        editor.remove(KEY_USER_EMAIL);
        editor.remove(KEY_AUTH_TOKEN);
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.apply();
    }

    /**
     * Clear user ID from preferences
     */
    public void clearUserId() {
        sharedPreferences.edit().remove(KEY_USER_ID).apply();
    }

    /**
     * Check if user is logged in
     * 
     * @return True if user is logged in
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Get current authentication token
     * 
     * @return Authentication token
     */
    public String getAuthToken() {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null);
    }

    /**
     * Save authentication token
     * 
     * @param token Authentication token
     */
    public void saveAuthToken(String token) {
        sharedPreferences.edit().putString(KEY_AUTH_TOKEN, token).apply();
    }

    /**
     * Get user ID
     * 
     * @return User ID or default user ID if not set
     */
    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, DEFAULT_USER_ID);
    }

    /**
     * Save user ID
     * 
     * @param userId User ID to save
     */
    public void saveUserId(String userId) {
        sharedPreferences.edit().putString(KEY_USER_ID, userId).apply();
    }

    /**
     * Check if user has completed onboarding
     * 
     * @return True if user has completed onboarding
     */
    public boolean hasCompletedOnboarding() {
        return sharedPreferences.getBoolean(KEY_ONBOARDING_COMPLETED, false);
    }

    /**
     * Get user name
     * 
     * @return User name
     */
    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, null);
    }

    /**
     * Get user email
     * 
     * @return User email
     */
    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, null);
    }

    /**
     * Get user phone
     * 
     * @return User phone
     */
    public String getUserPhone() {
        return sharedPreferences.getString(KEY_USER_PHONE, null);
    }

    /**
     * Save user phone
     * 
     * @param phone User phone number
     */
    public void saveUserPhone(String phone) {
        sharedPreferences.edit().putString(KEY_USER_PHONE, phone).apply();
    }

    /**
     * Get user profile picture URL
     * 
     * @return Profile picture URL
     */
    public String getUserProfilePic() {
        return sharedPreferences.getString(KEY_USER_PROFILE_PIC, null);
    }

    /**
     * Save user profile picture URL
     * 
     * @param profilePicUrl Profile picture URL
     */
    public void saveUserProfilePic(String profilePicUrl) {
        sharedPreferences.edit().putString(KEY_USER_PROFILE_PIC, profilePicUrl).apply();
    }

    /**
     * Get user loyalty points
     * 
     * @return Loyalty points
     */
    public int getUserPoints() {
        return sharedPreferences.getInt(KEY_USER_POINTS, 0);
    }

    /**
     * Save user loyalty points
     * 
     * @param points Loyalty points
     */
    public void saveUserPoints(int points) {
        sharedPreferences.edit().putInt(KEY_USER_POINTS, points).apply();
    }

    /**
     * Get user gender
     * 
     * @return User gender
     */
    public String getUserGender() {
        return sharedPreferences.getString(KEY_USER_GENDER, null);
    }

    /**
     * Save user gender
     * 
     * @param gender User gender
     */
    public void saveUserGender(String gender) {
        sharedPreferences.edit().putString(KEY_USER_GENDER, gender).apply();
    }

    /**
     * Get user date of birth
     * 
     * @return User date of birth
     */
    public String getUserDateOfBirth() {
        return sharedPreferences.getString(KEY_USER_DATE_OF_BIRTH, null);
    }

    /**
     * Save user date of birth
     * 
     * @param dateOfBirth User date of birth
     */
    public void saveUserDateOfBirth(String dateOfBirth) {
        sharedPreferences.edit().putString(KEY_USER_DATE_OF_BIRTH, dateOfBirth).apply();
    }

    /**
     * Save user photo (alias for saveUserProfilePic)
     * 
     * @param photoUrl User photo URL
     */
    public void saveUserPhoto(String photoUrl) {
        saveUserProfilePic(photoUrl);
    }

    /**
     * Check if dark mode is enabled
     * 
     * @return True if dark mode is enabled
     */
    public boolean isDarkModeEnabled() {
        return sharedPreferences.getBoolean(KEY_DARK_MODE, false);
    }

    /**
     * Set dark mode setting
     * 
     * @param enabled True to enable dark mode
     */
    public void setDarkModeEnabled(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_DARK_MODE, enabled).apply();
    }

    /**
     * Check if notifications are enabled
     * 
     * @return True if notifications are enabled
     */
    public boolean areNotificationsEnabled() {
        return sharedPreferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, true);
    }

    /**
     * Check if notifications are enabled (alias method)
     * 
     * @return True if notifications are enabled
     */
    public boolean isNotificationsEnabled() {
        return areNotificationsEnabled();
    }

    /**
     * Set notifications setting
     * 
     * @param enabled True to enable notifications
     */
    public void setNotificationsEnabled(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply();
    }

    /**
     * Get last order ID
     * 
     * @return Last order ID
     */
    public String getLastOrderId() {
        return sharedPreferences.getString(KEY_LAST_ORDER_ID, null);
    }

    /**
     * Save last order ID
     * 
     * @param orderId Order ID
     */
    public void saveLastOrderId(String orderId) {
        sharedPreferences.edit().putString(KEY_LAST_ORDER_ID, orderId).apply();
    }

    /**
     * Get saved delivery address
     * 
     * @return Saved address
     */
    public String getSavedAddress() {
        return sharedPreferences.getString(KEY_SAVED_ADDRESS, null);
    }

    /**
     * Save delivery address
     * 
     * @param address Delivery address
     */
    public void saveAddress(String address) {
        sharedPreferences.edit().putString(KEY_SAVED_ADDRESS, address).apply();
    }

    /**
     * Get default payment method
     * 
     * @return Default payment method
     */
    public String getDefaultPayment() {
        return sharedPreferences.getString(KEY_DEFAULT_PAYMENT, null);
    }

    /**
     * Save default payment method
     * 
     * @param paymentMethod Payment method
     */
    public void saveDefaultPayment(String paymentMethod) {
        sharedPreferences.edit().putString(KEY_DEFAULT_PAYMENT, paymentMethod).apply();
    }

    /**
     * Check if user is first time user
     * 
     * @return True if first time user
     */
    public boolean isFirstTimeUser() {
        return sharedPreferences.getBoolean(KEY_FIRST_TIME_USER, true);
    }

    /**
     * Set first time user status
     * 
     * @param isFirstTime True if first time user
     */
    public void setFirstTimeUser(boolean isFirstTime) {
        sharedPreferences.edit().putBoolean(KEY_FIRST_TIME_USER, isFirstTime).apply();
    }

    /**
     * Check if onboarding has been completed
     * 
     * @return True if onboarding completed
     */
    public boolean isOnboardingCompleted() {
        return sharedPreferences.getBoolean(KEY_ONBOARDING_COMPLETED, false);
    }

    /**
     * Set onboarding completion status
     * 
     * @param completed True if onboarding completed
     */
    public void setOnboardingCompleted(boolean completed) {
        sharedPreferences.edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply();
    }

    /**
     * Clear all preferences
     */
    public void clearAll() {
        sharedPreferences.edit().clear().apply();
    }

    /**
     * Clear all preferences (alias method)
     */
    public void clear() {
        clearAll();
    }    public void clearUserData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USER_NAME);
        editor.remove(KEY_USER_EMAIL);
        editor.remove(KEY_USER_PHONE);
        editor.remove(KEY_USER_PROFILE_PIC);
        editor.remove(KEY_USER_POINTS);
        editor.remove(KEY_USER_TIER);
        editor.remove(KEY_IS_LOGGED_IN);
        editor.remove(KEY_AUTH_TOKEN);
        editor.remove(KEY_USER_GENDER);
        editor.remove(KEY_USER_DATE_OF_BIRTH);
        editor.apply();
    }

    /**
     * Save selected location
     * 
     * @param location Selected location
     */
    public void saveSelectedLocation(String location) {
        sharedPreferences.edit().putString(KEY_SELECTED_LOCATION, location).apply();
    }

    /**
     * Get selected location
     * 
     * @return Selected location
     */
    public String getSelectedLocation() {
        return sharedPreferences.getString(KEY_SELECTED_LOCATION, "Downtown Coffee Corner");
    }

    /**
     * Get loyalty points
     * 
     * @return Loyalty points
     */
    public int getLoyaltyPoints() {
        return sharedPreferences.getInt(KEY_LOYALTY_POINTS, 0);
    }

    /**
     * Save loyalty points
     * 
     * @param points Loyalty points
     */
    public void saveLoyaltyPoints(int points) {
        sharedPreferences.edit().putInt(KEY_LOYALTY_POINTS, points).apply();
    }

    /**
     * Get promo code
     * 
     * @return Promo code
     */
    public String getPromoCode() {
        return sharedPreferences.getString(KEY_PROMO_CODE, null);
    }

    /**
     * Save promo code
     * 
     * @param promoCode Promo code
     */
    public void savePromoCode(String promoCode) {
        sharedPreferences.edit().putString(KEY_PROMO_CODE, promoCode).apply();
    }
}
