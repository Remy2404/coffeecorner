package com.coffeecorner.app.network;

import com.coffeecorner.app.utils.Constants;

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
     */
    private Retrofit createRetrofit() {
        // Create logging interceptor for debugging
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Create OkHttpClient with timeout settings and logging
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .build();

        // Create Retrofit instance
        return new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
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
}
