package com.coffeecorner.app.models;

import com.google.gson.annotations.SerializedName;

/**
 * Request model for Firebase authentication
 */
public class FirebaseAuthRequest {

    @SerializedName("firebase_token")
    private String firebaseToken;

    public FirebaseAuthRequest() {
    }

    public FirebaseAuthRequest(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }
}
