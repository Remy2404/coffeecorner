package com.coffeecorner.app.utils;

import com.google.gson.FieldNamingStrategy;

import java.lang.reflect.Field;

/**
 * Custom field naming strategy to handle snake_case to camelCase conversion for
 * JSON deserialization
 * This ensures proper mapping of fields like image_url (in JSON) to imageUrl
 * (in Java)
 */
public class CustomFieldNamingStrategy implements FieldNamingStrategy {
    @Override
    public String translateName(Field f) {
        // Convert Java field name to snake_case for JSON
        // For example: imageUrl -> image_url
        String name = f.getName();
        return camelCaseToSnakeCase(name);
    }

    private String camelCaseToSnakeCase(String camelCase) {
        // The JSON field will be snake_case (image_url), while Java field is camelCase
        // (imageUrl)
        // This method converts Java's camelCase to JSON's snake_case format
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < camelCase.length(); i++) {
            char currentChar = camelCase.charAt(i);

            // If the character is uppercase, append an underscore and convert to lowercase
            if (Character.isUpperCase(currentChar)) {
                result.append('_');
                result.append(Character.toLowerCase(currentChar));
            } else {
                result.append(currentChar);
            }
        }

        return result.toString();
    }
}
