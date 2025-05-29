package com.coffeecorner.app.network;

import android.content.Context;
import com.coffeecorner.app.utils.Constants;
import com.coffeecorner.app.utils.PreferencesHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

/**
 * RetrofitClient - Singleton class for creating and managing Retrofit instances
 * Provides configured Retrofit client for API calls
 */
public class RetrofitClient {
    private static volatile RetrofitClient instance;
    private static Retrofit retrofit;

    private RetrofitClient() {
        // Private constructor to prevent instantiation
        retrofit = createRetrofit();
    }

    /**
     * Get singleton instance of RetrofitClient
     * 
     * @return RetrofitClient instance
     */
    public static RetrofitClient getInstance() {
        if (instance == null) {
            synchronized (RetrofitClient.class) {
                if (instance == null) {
                    instance = new RetrofitClient();
                }
            }
        }
        return instance;
    }

    /**
     * Create and configure Retrofit instance
     * 
     * @return Configured Retrofit instance
     */    private Retrofit createRetrofit() {
        // Get application context for TokenAuthenticator
        final Context appContext;
        try {
            appContext = com.coffeecorner.app.CoffeeCornerApplication.getInstance()
                    .getApplicationContext();
        } catch (Exception e) {
            android.util.Log.e("RetrofitClient", "Failed to get application context: " + e.getMessage());
            // Fallback: create Retrofit without TokenAuthenticator if application context
            // fails
            return createBasicRetrofit();
        }

        // Create logging interceptor for debugging
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Create TokenAuthenticator for automatic JWT refresh
        TokenAuthenticator tokenAuthenticator = null;
        try {
            tokenAuthenticator = new TokenAuthenticator(appContext, Constants.API_BASE_URL);
        } catch (Exception e) {
            android.util.Log.e("RetrofitClient", "Failed to create TokenAuthenticator: " + e.getMessage());
            // Continue without authenticator if creation fails
        } // Create OkHttpClient with timeout settings, logging, and authentication
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor);

        // Add authenticator only if it was created successfully
        if (tokenAuthenticator != null) {
            clientBuilder.authenticator(tokenAuthenticator);
        }

        OkHttpClient client = clientBuilder
                .addInterceptor(chain -> {
                    okhttp3.Request original = chain.request();

                    // Build new request with authorization header
                    okhttp3.Request.Builder builder = original.newBuilder()
                            .method(original.method(), original.body());

                    // Add Accept header for JSON
                    builder.header("Accept", "application/json");

                    // Get auth token from preferences if available
                    PreferencesHelper preferencesHelper = new PreferencesHelper(appContext);
                    String token = preferencesHelper.getAuthToken();

                    // Add Authorization header if token exists
                    if (token != null && !token.isEmpty()) {
                        builder.header("Authorization", "Bearer " + token);
                        android.util.Log.d("RetrofitClient", "Adding Authorization header with token: " +
                                (token.length() > 10 ? token.substring(0, 10) + "..." : token));
                    } else {
                        android.util.Log.w("RetrofitClient",
                                "No auth token found - request will be made without authentication");
                    }

                    return chain.proceed(builder.build());
                })
                .build(); // Create a custom Gson instance with proper field naming strategy
        // We'll use LOWER_CASE_WITH_UNDERSCORES directly instead of our custom strategy
        // since we now have SerializedName annotations in model classes
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        // Create Retrofit instance with custom Gson
        return new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    /**
     * Create a basic Retrofit instance without TokenAuthenticator as fallback
     * 
     * @return Basic Retrofit instance
     */
    private Retrofit createBasicRetrofit() {
        // Create logging interceptor for debugging
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Create OkHttpClient with basic configuration
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(chain -> {
                    okhttp3.Request original = chain.request();
                    okhttp3.Request.Builder builder = original.newBuilder()
                            .method(original.method(), original.body())
                            .header("Accept", "application/json");
                    return chain.proceed(builder.build());
                })
                .build();

        // Create Gson instance
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        // Create basic Retrofit instance
        return new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    /**
     * Get API service interface
     * 
     * @return ApiService instance
     */
    public static ApiService getApiService() {
        if (retrofit == null) {
            getInstance(); // Initialize if needed
        }
        return retrofit.create(ApiService.class);
    }

    /**
     * Get API service interface (alias method)
     * 
     * @return ApiService instance
     */
    public static ApiService getApi() {
        return getApiService();
    }
}
