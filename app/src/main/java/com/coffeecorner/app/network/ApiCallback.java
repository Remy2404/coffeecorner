package com.coffeecorner.app.network;

/**
 * Generic callback interface for API operations
 * 
 * @param <T> Type of data in the response
 */
public interface ApiCallback<T> {
    /**
     * Called when the API call is successful
     * 
     * @param result The result data
     */
    void onSuccess(T result);

    /**
     * Called when the API call fails
     * 
     * @param errorMessage Error message
     */
    void onError(String errorMessage);
}