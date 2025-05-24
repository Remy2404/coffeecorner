package com.coffeecorner.app.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.coffeecorner.app.R;
import com.coffeecorner.app.models.CartItem;
import com.coffeecorner.app.models.Order;
import com.coffeecorner.app.models.Product;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for common operations used throughout the app
 */
public class AppUtils {
    private static final String TAG = "AppUtils";
    private static final String PREFS_NAME = "CoffeeCornerPrefs";

    /**
     * Format price to standard currency format
     * 
     * @param price Price as double
     * @return Formatted price string
     */
    public static String formatPrice(double price) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        return currencyFormat.format(price);
    }

    /**
     * Calculate total price for an order
     * 
     * @param order Order to calculate total for
     * @return Total price including all fees and discounts
     */
    public static double calculateOrderTotal(Order order) {
        if (order == null || order.getItems() == null) {
            return 0.0;
        }

        double itemsTotal = 0.0;
        for (CartItem item : order.getItems()) {
            double itemPrice = item.getProduct().getPrice() + item.getExtraCharge();
            itemsTotal += (itemPrice * item.getQuantity());
        }

        return itemsTotal + order.getDeliveryFee() + order.getTax() - order.getDiscount();
    }

    /**
     * Get human-readable time ago string (e.g. "3 hours ago", "5 days ago")
     * 
     * @param dateString Date string in format "yyyy-MM-dd HH:mm:ss"
     * @return Time ago string
     */
    public static String getTimeAgo(String dateString) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date past = format.parse(dateString);
            Date now = new Date();

            long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
            long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
            long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
            long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

            if (seconds < 60) {
                return seconds + " seconds ago";
            } else if (minutes < 60) {
                return minutes + " minutes ago";
            } else if (hours < 24) {
                return hours + " hours ago";
            } else {
                return days + " days ago";
            }
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing date: " + e.getMessage());
            return "some time ago";
        }
    }

    /**
     * Truncate text to a specific length and add ellipsis if needed
     * 
     * @param text      Input text
     * @param maxLength Maximum length before truncation
     * @return Truncated text
     */
    public static String truncateText(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }

    /**
     * Save a string preference
     * 
     * @param context Context
     * @param key     Preference key
     * @param value   Preference value
     */
    public static void savePreference(Context context, String key, String value) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Get a string preference
     * 
     * @param context      Context
     * @param key          Preference key
     * @param defaultValue Default value if preference doesn't exist
     * @return Preference value
     */
    public static String getPreference(Context context, String key, String defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(key, defaultValue);
    }

    /**
     * Save a boolean preference
     * 
     * @param context Context
     * @param key     Preference key
     * @param value   Preference value
     */
    public static void savePreference(Context context, String key, boolean value) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * Get a boolean preference
     * 
     * @param context      Context
     * @param key          Preference key
     * @param defaultValue Default value if preference doesn't exist
     * @return Preference value
     */
    public static boolean getPreference(Context context, String key, boolean defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(key, defaultValue);
    }

    /**
     * Open maps app with directions to a location
     * 
     * @param context Context
     * @param address Address to navigate to
     */
    public static void openDirections(Context context, String address) {
        try {
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps"); // Use Google Maps if available

            if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(mapIntent);
            } else {
                // If Google Maps is not installed, open with any available maps app
                Uri uri = Uri.parse("http://maps.google.com/?q=" + Uri.encode(address));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error opening directions: " + e.getMessage());
            Toast.makeText(context, "Could not open maps. Please try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Open the dialer with a phone number
     * 
     * @param context     Context
     * @param phoneNumber Phone number to call
     */
    public static void dialPhoneNumber(Context context, String phoneNumber) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error dialing number: " + e.getMessage());
            Toast.makeText(context, "Could not make a call. Please try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Share content with other apps
     * 
     * @param context Context
     * @param subject Share subject
     * @param text    Text to share
     */
    public static void shareContent(Context context, String subject, String text) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        context.startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    /**
     * Contact customer support
     * 
     * @param context Context
     */
    public static void contactSupport(Context context) {
        // In a real app, this would open a dedicated support interface
        // For demo purposes, we'll just display a dialog with options
        String[] supportOptions = { "Call Support", "Email Support", "Live Chat" };

        // This would normally be implemented with AlertDialog,
        // but for simplicity we'll just make a phone call
        dialPhoneNumber(context, "1-800-COFFEE");
    }

    /**
     * Open URL in browser
     * 
     * @param context Context
     * @param url     URL to open
     */
    public static void openUrl(Context context, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error opening URL: " + e.getMessage());
            Toast.makeText(context, "Could not open link. Please try again later.", Toast.LENGTH_SHORT).show();
        }
    }
}
