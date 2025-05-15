package com.coffeecorner.app.utils;

/**
 * Utility class to define constants for Supabase return types.
 * This mimics the Supabase Kotlin library's functionality.
 */
public class Returning {
    /**
     * Requests that deleted rows be returned in the response.
     */
    public static final String REPRESENTATION = "representation";
    
    /**
     * Requests that no rows be returned in the response.
     */
    public static final String MINIMAL = "minimal";
}
