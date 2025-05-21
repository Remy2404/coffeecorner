package com.coffeecorner.app.utils;

/**
 * Constants used throughout the app
 */
public class Constants {
    // API endpoints
    public static final String BASE_URL = "http://10.0.2.2:8000/";
    public static final String API_BASE_URL = BASE_URL;
    public static final String API_LOGIN = "auth/login";
    public static final String API_REGISTER = "auth/register";
    public static final String API_PRODUCTS = "products";
    public static final String API_ORDERS = "orders";
    public static final String API_PROFILE = "users/profile";
    public static final String API_REWARDS = "rewards";

    // Intent extras
    public static final String EXTRA_PRODUCT_ID = "product_id";
    public static final String EXTRA_CATEGORY_ID = "category_id";
    public static final String EXTRA_ORDER_ID = "order_id";
    public static final String EXTRA_USER_ID = "user_id";
    public static final String EXTRA_FROM_NOTIFICATION = "from_notification";

    // Fragment tags
    public static final String TAG_HOME_FRAGMENT = "home_fragment";
    public static final String TAG_MENU_FRAGMENT = "menu_fragment";
    public static final String TAG_CART_FRAGMENT = "cart_fragment";
    public static final String TAG_PROFILE_FRAGMENT = "profile_fragment";

    // Navigation indices
    public static final int NAV_HOME = 0;
    public static final int NAV_MENU = 1;
    public static final int NAV_CART = 2;
    public static final int NAV_PROFILE = 3;

    // Request codes
    public static final int REQUEST_LOCATION_PERMISSION = 1001;
    public static final int REQUEST_CAMERA_PERMISSION = 1002;
    public static final int REQUEST_STORAGE_PERMISSION = 1003;
    public static final int REQUEST_PAYMENT = 2001;
    public static final int REQUEST_ADDRESS = 2002;
    public static final int REQUEST_PROFILE_EDIT = 2003;

    // Product categories
    public static final String CATEGORY_COFFEE = "coffee";
    public static final String CATEGORY_TEA = "tea";
    public static final String CATEGORY_BAKERY = "bakery";
    public static final String CATEGORY_SANDWICH = "sandwich";
    public static final String CATEGORY_DESSERT = "dessert";

    // Order status
    public static final int STATUS_PENDING = 0;
    public static final int STATUS_CONFIRMED = 1;
    public static final int STATUS_PREPARING = 2;
    public static final int STATUS_ON_DELIVERY = 3;
    public static final int STATUS_DELIVERED = 4;
    public static final int STATUS_CANCELLED = 5;

    // Payment methods
    public static final String PAYMENT_CREDIT_CARD = "credit_card";
    public static final String PAYMENT_DEBIT_CARD = "debit_card";
    public static final String PAYMENT_PAYPAL = "paypal";
    public static final String PAYMENT_GOOGLE_PAY = "google_pay";
    public static final String PAYMENT_CASH = "cash";

    // Loyalty tiers
    public static final int TIER_BRONZE = 1;
    public static final int TIER_SILVER = 2;
    public static final int TIER_GOLD = 3;
    public static final int TIER_PLATINUM = 4;

    // Points thresholds for tiers
    public static final int POINTS_SILVER = 100;
    public static final int POINTS_GOLD = 300;
    public static final int POINTS_PLATINUM = 600;

    // Notification channels
    public static final String CHANNEL_ORDERS = "orders_channel";
    public static final String CHANNEL_PROMOTIONS = "promotions_channel";
    public static final String CHANNEL_REWARDS = "rewards_channel";

    // Notification IDs
    public static final int NOTIFICATION_ORDER_ID = 1001;
    public static final int NOTIFICATION_PROMO_ID = 1002;
    public static final int NOTIFICATION_REWARD_ID = 1003;

    // Notification types
    public static final int NOTIFICATION_TYPE_ORDER = 1;
    public static final int NOTIFICATION_TYPE_PROMOTION = 2;
    public static final int NOTIFICATION_TYPE_REWARD = 3;
    public static final int NOTIFICATION_TYPE_NEWS = 4;

    // Shared preferences keys
    public static final String PREF_USER_ID = "user_id";
    public static final String PREF_AUTH_TOKEN = "auth_token";
    public static final String PREF_USER_NAME = "user_name";
    public static final String PREF_USER_EMAIL = "user_email";

    // Bundle keys
    public static final String BUNDLE_PRODUCT = "product";
    public static final String BUNDLE_ORDER = "order";
    public static final String BUNDLE_CART_ITEMS = "cart_items";

    // Dialog tags
    public static final String DIALOG_LOADING = "loading_dialog";
    public static final String DIALOG_ERROR = "error_dialog";
    public static final String DIALOG_RATING = "rating_dialog";

    // Animation durations
    public static final int ANIMATION_DURATION_SHORT = 300;
    public static final int ANIMATION_DURATION_MEDIUM = 500;
    public static final int ANIMATION_DURATION_LONG = 800;

    // Cache durations (in milliseconds)
    public static final long CACHE_DURATION_MENU = 24 * 60 * 60 * 1000; // 24 hours
    public static final long CACHE_DURATION_PROFILE = 60 * 60 * 1000; // 1 hour

    // Contact information
    public static final String SUPPORT_PHONE = "1-800-COFFEE";
    public static final String SUPPORT_EMAIL = "support@coffeecorner.app";
    public static final String COMPANY_WEBSITE = "https://coffeecorner.app";

    // Social media links
    public static final String FACEBOOK_URL = "https://facebook.com/coffeecornerapp";
    public static final String INSTAGRAM_URL = "https://instagram.com/coffeecornerapp";
    public static final String TWITTER_URL = "https://twitter.com/coffeecornerapp";

    // App settings
    public static final int MAX_CART_ITEMS = 20;
    public static final int ORDER_HISTORY_LIMIT = 10;
    public static final int NOTIFICATIONS_LIMIT = 50;

    private Constants() {
        // Private constructor to prevent instantiation
    }
}
