package com.coffeecorner.app.utils;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Pattern;

/**
 * Utility class for validating user input in forms
 */
public class Validator {
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Patterns.EMAIL_ADDRESS;

    // Password must be at least 8 characters with at least one letter and one
    // number
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$");

    // Phone number validation (simple format)
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9]{10,15}$");

    // Credit card number validation (simplified)
    private static final Pattern CREDIT_CARD_PATTERN = Pattern.compile("^[0-9]{13,19}$");

    /**
     * Validate email address format
     * 
     * @param email Email to validate
     * @return True if email is valid
     */
    public static boolean isValidEmail(CharSequence email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validate password strength
     * 
     * @param password Password to validate
     * @return True if password meets requirements
     */
    public static boolean isValidPassword(CharSequence password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * Check if passwords match
     * 
     * @param password        First password
     * @param confirmPassword Second password
     * @return True if passwords match
     */
    public static boolean doPasswordsMatch(CharSequence password, CharSequence confirmPassword) {
        return password != null && confirmPassword != null && password.toString().equals(confirmPassword.toString());
    }

    /**
     * Validate phone number format
     * 
     * @param phone Phone number to validate
     * @return True if phone number is valid
     */
    public static boolean isValidPhone(CharSequence phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * Validate name (not empty, at least 2 characters)
     * 
     * @param name Name to validate
     * @return True if name is valid
     */
    public static boolean isValidName(CharSequence name) {
        return name != null && name.length() >= 2;
    }

    /**
     * Validate a required field is not empty
     * 
     * @param text Text to validate
     * @return True if text is not empty
     */
    public static boolean isNotEmpty(CharSequence text) {
        return !TextUtils.isEmpty(text);
    }

    /**
     * Validate credit card number (simplified)
     * 
     * @param cardNumber Credit card number
     * @return True if credit card number is valid
     */
    public static boolean isValidCreditCardNumber(CharSequence cardNumber) {
        // Remove spaces and dashes for validation
        String cleanCardNumber = cardNumber.toString().replaceAll("[ -]", "");
        return CREDIT_CARD_PATTERN.matcher(cleanCardNumber).matches() && validateLuhn(cleanCardNumber);
    }

    /**
     * Validate credit card expiry date (MM/YY format)
     * 
     * @param expiryDate Expiry date string
     * @return True if expiry date is valid and not expired
     */
    public static boolean isValidExpiryDate(String expiryDate) {
        if (expiryDate == null || !expiryDate.matches("^(0[1-9]|1[0-2])/[0-9]{2}$")) {
            return false;
        }

        String[] parts = expiryDate.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]) + 2000; // Convert YY to 20YY

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int currentMonth = calendar.get(java.util.Calendar.MONTH) + 1; // Calendar months are 0-based
        int currentYear = calendar.get(java.util.Calendar.YEAR);

        // Check if card is expired
        return (year > currentYear) || (year == currentYear && month >= currentMonth);
    }

    /**
     * Validate CVV code (3-4 digits)
     * 
     * @param cvv CVV code
     * @return True if CVV is valid
     */
    public static boolean isValidCVV(String cvv) {
        return cvv != null && cvv.matches("^[0-9]{3,4}$");
    }

    /**
     * Validate postal/zip code (simple format)
     * 
     * @param postalCode Postal code
     * @return True if postal code is valid
     */
    public static boolean isValidPostalCode(String postalCode) {
        return postalCode != null && postalCode.matches("^[a-zA-Z0-9]{5,10}$");
    }

    /**
     * Implement Luhn algorithm for credit card validation
     * 
     * @param cardNumber Credit card number without spaces
     * @return True if card passes Luhn check
     */
    private static boolean validateLuhn(String cardNumber) {
        int sum = 0;
        boolean alternate = false;

        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }

        return (sum % 10 == 0);
    }
}
