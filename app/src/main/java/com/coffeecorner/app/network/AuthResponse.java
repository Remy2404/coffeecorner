package com.coffeecorner.app.network;

import com.coffeecorner.app.models.User;
import com.google.gson.annotations.SerializedName;

/**
 * AuthResponse - Specialized response class for authentication endpoints
 * Handles the specific format returned by Firebase authentication
 */
public class AuthResponse {
    
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("user")
    private User user;
    
    @SerializedName("access_token")
    private String accessToken;
    
    @SerializedName("token_type")
    private String tokenType;

    /**
     * Default constructor
     */
    public AuthResponse() {
    }

    /**
     * Constructor with all fields
     */
    public AuthResponse(boolean success, String message, User user, String accessToken, String tokenType) {
        this.success = success;
        this.message = message;
        this.user = user;
        this.accessToken = accessToken;
        this.tokenType = tokenType;
    }

    /**
     * Check if the authentication was successful
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Set success status
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Get response message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Set response message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Get user data
     */
    public User getUser() {
        return user;
    }

    /**
     * Set user data
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Get access token
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Set access token
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Get token type (usually "bearer")
     */
    public String getTokenType() {
        return tokenType;
    }

    /**
     * Set token type
     */
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    @Override
    public String toString() {
        return "AuthResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", user=" + user +
                ", accessToken='" + accessToken + '\'' +
                ", tokenType='" + tokenType + '\'' +
                '}';
    }
}
