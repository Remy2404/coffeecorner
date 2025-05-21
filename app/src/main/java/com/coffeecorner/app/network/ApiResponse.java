package com.coffeecorner.app.network;

/**
 * ApiResponse - Wrapper class for API responses
 * Standardizes response format across all API calls
 * 
 * @param <T> Type of data in the response
 */
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;

    /**
     * Default constructor
     */
    public ApiResponse() {
        // Default constructor
    }

    /**
     * Constructor with all fields
     * 
     * @param success Whether the request was successful
     * @param message Response message
     * @param data Response data
     */
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    /**
     * Create a success response
     * 
     * @param data Response data
     * @param message Success message
     * @param <T> Type of data
     * @return ApiResponse with success flag set to true
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data);
    }

    /**
     * Create an error response
     * 
     * @param message Error message
     * @param <T> Type of data
     * @return ApiResponse with success flag set to false
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    /**
     * Check if response is successful
     * 
     * @return true if successful
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Set success flag
     * 
     * @param success Whether request was successful
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Get response message
     * 
     * @return Message from server
     */
    public String getMessage() {
        return message;
    }

    /**
     * Set response message
     * 
     * @param message Message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Get response data
     * 
     * @return Data payload
     */
    public T getData() {
        return data;
    }

    /**
     * Set response data
     * 
     * @param data Data payload
     */
    public void setData(T data) {
        this.data = data;
    }
}
