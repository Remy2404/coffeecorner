package com.coffeecorner.app.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * A simplified client manager for Supabase that uses Java HttpURLConnection.
 * This implementation avoids using the Kotlin-based Supabase library.
 */
public class SupabaseClientManager {

    // Using placeholder values that will be replaced at runtime
    private static String SUPABASE_URL = "YOUR_SUPABASE_URL";
    private static String SUPABASE_ANON_KEY = "YOUR_SUPABASE_ANON_KEY";
    private static final String TAG = "SupabaseClientManager";

    private static final Executor executor = Executors.newCachedThreadPool();
    private static SupabaseClientManager instance;

    private SupabaseClientManager() {
        // Private constructor to prevent instantiation
    }

    /**
     * Initialize Supabase credentials from BuildConfig
     * This should be called from the application class or before first use
     * 
     * @param url The Supabase URL (should be injected at build time from secure
     *            source)
     * @param key The Supabase anonymous key (should be injected at build time from
     *            secure source)
     */
    public static void initialize(String url, String key) {
        SUPABASE_URL = url;
        SUPABASE_ANON_KEY = key;
        Log.d(TAG, "Supabase client initialized");
    }

    /**
     * Load Supabase credentials from properties file
     * This is for development only and should not be used in production
     * 
     * @param context Application context
     */
    public static void loadFromProperties(Context context) {
        try {
            Properties properties = new Properties();
            try (InputStream is = context.getAssets().open("supabase.properties")) {
                properties.load(is);
                SUPABASE_URL = properties.getProperty("SUPABASE_URL");
                SUPABASE_ANON_KEY = properties.getProperty("SUPABASE_ANON_KEY");
                Log.d(TAG, "Supabase credentials loaded from properties file");
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to load Supabase credentials: " + e.getMessage());
        }
    }

    public static synchronized SupabaseClientManager getInstance() {
        if (instance == null) {
            instance = new SupabaseClientManager();
        }
        return instance;
    }

    /**
     * Get the Supabase client instance for direct API calls
     * Added to maintain compatibility with code that was originally using the
     * Kotlin Supabase library
     * 
     * @return this instance for chaining
     */
    public SupabaseClientManager getClient() {
        return this;
    }

    /**
     * Stub method to maintain compatibility with Supabase Kotlin library
     * 
     * @return this instance for chaining
     */
    public SupabaseClientManager getSupabase() {
        return this;
    }

    /**
     * Stub method to maintain compatibility with Supabase Kotlin library
     * 
     * @param pluginClass The plugin class to get
     * @return this database client
     */
    public DatabaseClient getPlugin(Class<?> pluginClass) {
        return new DatabaseClient("products"); // Default to products for compatibility
    }

    /**
     * A simplified database interface that replaces Postgrest functionality.
     * 
     * @return A DatabaseClient instance for database operations
     */
    public DatabaseClient from(String tableName) {
        return new DatabaseClient(tableName);
    }

    /**
     * Inner class to handle database operations
     */
    public class DatabaseClient {
        private final String tableName;

        public DatabaseClient(String tableName) {
            this.tableName = tableName;
        }

        /**
         * Select a different table to operate on
         * 
         * @param tableName The name of the table to operate on
         * @return A DatabaseClient instance for chaining
         */
        public DatabaseClient from(String tableName) {
            return new DatabaseClient(tableName);
        }

        /**
         * Select records from the table
         * 
         * @return An OperationResult for chaining
         */
        public OperationResult select() {
            return new OperationResult(null, tableName, "GET");
        }

        /**
         * Insert an object into the specified table
         * 
         * @param object The object to insert (will be converted to JSON)
         * @return An OperationResult for chaining
         */
        public OperationResult insert(Object object) {
            return new OperationResult(object, tableName, "POST");
        }

        /**
         * Update records in the specified table
         * 
         * @param object The updated data (will be converted to JSON)
         * @return An OperationResult for chaining
         */
        public OperationResult update(Object object) {
            return new OperationResult(object, tableName, "PATCH");
        }

        /**
         * Delete records from the specified table
         * 
         * @return An OperationResult for chaining
         */
        public OperationResult delete() {
            return new OperationResult(null, tableName, "DELETE");
        }

        /**
         * Delete records from the specified table with returning option
         * 
         * @param returning The returning option (e.g., "representation" or "minimal")
         * @return An OperationResult for chaining
         */
        public OperationResult delete(String returning) {
            OperationResult result = new OperationResult(null, tableName, "DELETE");
            result.addParameter("returning", returning);
            return result;
        }

        /**
         * Upload a file to storage
         * 
         * @param file The storage file
         * @param data The upload data
         * @return An OperationResult for chaining
         */
        public OperationResult upload(Object file, Object data) {
            // This is a stub implementation for compilation
            return new OperationResult(data, "storage.files", "POST");
        }

        /**
         * Get public URL for a file
         * 
         * @param fileName The name of the file
         * @return The public URL as a string
         */
        public String getPublicUrl(String fileName) {
            // This is a stub implementation for compilation
            return SUPABASE_URL + "/storage/v1/object/public/" + fileName;
        }
    }

    /**
     * Class to handle the result of database operations
     */
    public class OperationResult {
        /**
         * Execute the operation with separate success and error handlers for
         * SupabaseResponse
         *
         * @param onSuccess Handler for successful responses
         * @param onError   Handler for error responses (receives Throwable)
         */
        public void executeWithResponseHandlers(
                java.util.function.Consumer<SupabaseClientManager.SupabaseResponse> onSuccess,
                java.util.function.Consumer<Throwable> onError) {
            executeAsync(new SupabaseCallback() {
                @Override
                public void onSuccess(SupabaseResponse response) {
                    if (response.getError() == null) {
                        onSuccess.accept(response);
                    } else {
                        onError.accept(new Exception(response.getError().getMessage()));
                    }
                }
            });
        }

        /**
         * Add an order by clause to the request
         *
         * @param field     The field to order by
         * @param ascending True for ascending, false for descending
         * @return This instance for chaining
         */
        public OperationResult order(String field, boolean ascending) {
            parameters.put("order", field + "." + (ascending ? "asc" : "desc"));
            return this;
        }

        private final Object data;
        private final String tableName;
        private final String method;
        private final Map<String, String> filters = new HashMap<>();
        private final Map<String, String> parameters = new HashMap<>();

        public OperationResult(Object data, String tableName, String method) {
            this.data = data;
            this.tableName = tableName;
            this.method = method;
        }

        /**
         * Add a filter where field equals value
         * 
         * @param field The field to filter on
         * @param value The value to match
         * @return This instance for chaining
         */
        public OperationResult eq(String field, String value) {
            filters.put(field, value);
            return this;
        }

        /**
         * Specify that only a single record should be returned
         * 
         * @return This instance for chaining
         */
        public OperationResult single() {
            parameters.put("limit", "1");
            return this;
        }

        /**
         * Add a parameter to the request
         * 
         * @param key   The parameter key
         * @param value The parameter value
         * @return This instance for chaining
         */
        public OperationResult addParameter(String key, String value) {
            parameters.put(key, value);
            return this;
        }

        /**
         * Execute the operation asynchronously
         * 
         * @param callback Callback to handle the response
         */
        public void executeAsync(final SupabaseCallback callback) {
            executor.execute(() -> {
                try {
                    StringBuilder urlBuilder = new StringBuilder(SUPABASE_URL + "/rest/v1/" + tableName);

                    // Add filters as query parameters if present
                    if (!filters.isEmpty()) {
                        urlBuilder.append("?");

                        boolean first = true;
                        for (Map.Entry<String, String> entry : filters.entrySet()) {
                            if (!first) {
                                urlBuilder.append("&");
                            }
                            urlBuilder.append(entry.getKey()).append("=eq.").append(entry.getValue());
                            first = false;
                        }
                    }

                    // Add other parameters
                    if (!parameters.isEmpty()) {
                        if (filters.isEmpty()) {
                            urlBuilder.append("?");
                        } else {
                            urlBuilder.append("&");
                        }

                        boolean first = filters.isEmpty();
                        for (Map.Entry<String, String> entry : parameters.entrySet()) {
                            if (!first) {
                                urlBuilder.append("&");
                            }
                            urlBuilder.append(entry.getKey()).append("=").append(entry.getValue());
                            first = false;
                        }
                    }

                    URL url = new URL(urlBuilder.toString());
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod(method);
                    connection.setRequestProperty("apikey", SUPABASE_ANON_KEY);
                    connection.setRequestProperty("Authorization", "Bearer " + SUPABASE_ANON_KEY);
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Prefer", "return=minimal");

                    if (data != null) {
                        connection.setDoOutput(true);
                        try (OutputStream os = connection.getOutputStream()) {
                            // Simple JSON conversion - in a real app, use a proper JSON library
                            String jsonData = data.toString();
                            byte[] input = jsonData.getBytes(StandardCharsets.UTF_8);
                            os.write(input, 0, input.length);
                        }
                    }

                    int responseCode = connection.getResponseCode();

                    if (responseCode >= 200 && responseCode < 300) {
                        StringBuilder response = new StringBuilder();
                        try (BufferedReader br = new BufferedReader(
                                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                            String responseLine;
                            while ((responseLine = br.readLine()) != null) {
                                response.append(responseLine.trim());
                            }
                        }

                        callback.onSuccess(new SupabaseResponse(response.toString(), null));
                    } else {
                        StringBuilder errorResponse = new StringBuilder();
                        try (BufferedReader br = new BufferedReader(
                                new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                            String responseLine;
                            while ((responseLine = br.readLine()) != null) {
                                errorResponse.append(responseLine.trim());
                            }
                        }

                        callback.onSuccess(new SupabaseResponse(null, new SupabaseError(errorResponse.toString())));
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error executing Supabase operation", e);
                    callback.onSuccess(new SupabaseResponse(null, new SupabaseError(e.getMessage())));
                }
            });
        }

        /**
         * Execute the operation synchronously
         * 
         * @param callback Callback to handle the response
         */
        public void execute(final SupabaseCallback callback) {
            // For simplicity, we're using the same implementation as executeAsync
            // In a real app, you might want to handle this differently
            executeAsync(callback);
        }

        /**
         * Execute the operation with success and error handlers for string responses
         * 
         * @param onSuccess Handler for successful String responses
         * @param onError   Handler for error responses
         */
        public void executeWithStringResponse(Consumer<String> onSuccess, Consumer<String> onError) {
            executeAsync(response -> {
                if (response.getError() == null) {
                    onSuccess.accept(response.getData());
                } else {
                    onError.accept(response.getError().getMessage());
                }
            });
        }

        /**
         * Execute the operation with a callback for the SupabaseResponse
         * Use this when you need to work with the raw response object
         * 
         * @param responseConsumer Consumer that processes the SupabaseResponse
         */
        public void executeWithResponse(Consumer<SupabaseResponse> responseConsumer) {
            executeAsync(responseConsumer::accept);
        }
    }

    /**
     * Interface for Supabase callbacks
     */
    public interface SupabaseCallback {
        void onSuccess(SupabaseResponse response);
    }

    /**
     * 
     * Class to represent a Supabase response
     */
    public static class SupabaseResponse {
        /**
         * Parse JSON data to a list of class instances using simple deserialization.
         * In a real app, use a proper JSON library like Gson or Jackson.
         *
         * @param <T>   The type to deserialize to
         * @param clazz The class to instantiate
         * @return A list of class instances
         */
        public <T> java.util.List<T> getDataList(Class<T> clazz) {
            java.util.List<T> list = new java.util.ArrayList<>();
            try {
                if (data == null) {
                    return list;
                }
                String jsonString = data.trim();
                if (jsonString.startsWith("[")) {
                    org.json.JSONArray arr = new org.json.JSONArray(jsonString);
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject json = arr.getJSONObject(i);
                        T instance = clazz.getDeclaredConstructor().newInstance();
                        for (java.lang.reflect.Field field : clazz.getDeclaredFields()) {
                            field.setAccessible(true);
                            String fieldName = field.getName();
                            if (json.has(fieldName)) {
                                Object value = json.get(fieldName);
                                if (value != null && !value.equals(JSONObject.NULL)) {
                                    field.set(instance, value);
                                }
                            }
                        }
                        list.add(instance);
                    }
                } else {
                    // Single object, not an array
                    JSONObject json = new JSONObject(jsonString);
                    T instance = clazz.getDeclaredConstructor().newInstance();
                    for (java.lang.reflect.Field field : clazz.getDeclaredFields()) {
                        field.setAccessible(true);
                        String fieldName = field.getName();
                        if (json.has(fieldName)) {
                            Object value = json.get(fieldName);
                            if (value != null && !value.equals(JSONObject.NULL)) {
                                field.set(instance, value);
                            }
                        }
                    }
                    list.add(instance);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error deserializing JSON to class list: " + e.getMessage());
            }
            return list;
        }

        private final String data;
        private final SupabaseError error;

        public SupabaseResponse(String data, SupabaseError error) {
            this.data = data;
            this.error = error;
        }

        public String getData() {
            return data;
        }

        /**
         * Parse JSON data to a class instance using simple deserialization.
         * In a real app, use a proper JSON library like Gson or Jackson.
         *
         * @param <T>   The type to deserialize to
         * @param clazz The class to instantiate
         * @return An instance of the class populated with data
         */
        public <T> T getData(Class<T> clazz) {
            try {
                if (data == null) {
                    return null;
                }
                // If the data is a JSON array, get the first object
                String jsonString = data.trim();
                if (jsonString.startsWith("[")) {
                    org.json.JSONArray arr = new org.json.JSONArray(jsonString);
                    if (arr.length() == 0)
                        return null;
                    jsonString = arr.getJSONObject(0).toString();
                }
                JSONObject json = new JSONObject(jsonString);
                T instance = clazz.getDeclaredConstructor().newInstance();
                for (java.lang.reflect.Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true);
                    String fieldName = field.getName();
                    if (json.has(fieldName)) {
                        Object value = json.get(fieldName);
                        if (value != null && !value.equals(JSONObject.NULL)) {
                            field.set(instance, value);
                        }
                    }
                }
                return instance;
            } catch (Exception e) {
                Log.e(TAG, "Error deserializing JSON to class: " + e.getMessage());
                return null;
            }
        }

        public SupabaseError getError() {
            return error;
        }

        // Removed invalid executeWithResponseHandlers from SupabaseResponse. This
        // method belongs only in OperationResult.
    }

    /**
     * Class to represent a Supabase error
     */
    public static class SupabaseError {
        private final String message;

        public SupabaseError(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
