package com.coffeecorner.app.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.coffeecorner.app.R;

/**
 * Utility class for loading images consistently throughout the app.
 * Uses Glide for efficient image loading, caching, and error handling.
 */
public class ImageLoader {

    /**
     * Load an image from a URL into an ImageView with default settings
     *
     * @param context   The context
     * @param imageUrl  The URL of the image to load
     * @param imageView The target ImageView
     */
    public static void loadImage(Context context, String imageUrl, ImageView imageView) {
        loadImage(context, imageUrl, imageView, R.drawable.coffee_coco, R.drawable.coffee_coco, null);
    }

    /**
     * Load an image from a URL into an ImageView with custom placeholder and error
     * images
     *
     * @param context     The context
     * @param imageUrl    The URL of the image to load
     * @param imageView   The target ImageView
     * @param placeholder Placeholder resource ID to show while loading
     * @param errorImage  Error resource ID to show if loading fails
     */
    public static void loadImage(Context context, String imageUrl, ImageView imageView,
            int placeholder, int errorImage) {
        loadImage(context, imageUrl, imageView, placeholder, errorImage, null);
    }

    /**
     * Load an image from a URL into an ImageView with full customization
     *
     * @param context     The context
     * @param imageUrl    The URL of the image to load
     * @param imageView   The target ImageView
     * @param placeholder Placeholder resource ID to show while loading
     * @param errorImage  Error resource ID to show if loading fails
     * @param listener    Optional listener for load events
     */
    public static void loadImage(Context context, String imageUrl, ImageView imageView,
            int placeholder, int errorImage,
            @Nullable final ImageLoadListener listener) {
        try {
            // Check for null context or detached view
            if (context == null || imageView == null) {
                Log.e("ImageLoader", "Context or ImageView is null");
                if (listener != null) {
                    listener.onError();
                }
                return;
            }

            // Handle empty URL
            if (imageUrl == null || imageUrl.trim().isEmpty()) {
                Log.e("ImageLoader", "Image URL is null or empty for " + imageView.getTag());
                imageView.setImageResource(errorImage);
                if (listener != null) {
                    listener.onError();
                }
                return;
            }

            // Enhanced logging with URL validation check
            Log.d("ImageLoader", "Loading image from URL: " + imageUrl);
            if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
                Log.w("ImageLoader", "URL doesn't start with http:// or https:// - might be invalid: " + imageUrl);
                imageView.setImageResource(errorImage);
                if (listener != null) {
                    listener.onError();
                }
                return;
            }

            // Additional validation for common URL patterns
            String lowerUrl = imageUrl.toLowerCase();
            if (!lowerUrl.endsWith(".jpg") && !lowerUrl.endsWith(".jpeg") &&
                    !lowerUrl.endsWith(".png") && !lowerUrl.endsWith(".webp") &&
                    !lowerUrl.endsWith(".gif") && !lowerUrl.contains("/images/")) {
                Log.w("ImageLoader", "URL doesn't end with common image extensions: " + imageUrl);
            }

            RequestOptions options = new RequestOptions()
                    .placeholder(placeholder)
                    .error(errorImage)
                    .fitCenter();

            RequestBuilder<Drawable> requestBuilder = Glide.with(context)
                    .load(imageUrl)
                    .apply(options);

            if (listener != null) {
                requestBuilder.listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                            Target<Drawable> target, boolean isFirstResource) {
                        Log.e("ImageLoader", "Failed to load image: " + imageUrl, e);
                        if (e != null) {
                            for (Throwable t : e.getRootCauses()) {
                                Log.e("ImageLoader", "Root cause: " + t.getMessage());
                            }
                        }
                        listener.onError();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model,
                            Target<Drawable> target, DataSource dataSource,
                            boolean isFirstResource) {
                        Log.d("ImageLoader", "Successfully loaded image: " + imageUrl);
                        listener.onSuccess();
                        return false;
                    }
                });
            }

            requestBuilder.into(imageView);
        } catch (Exception e) {
            // Fallback to error image in case of any exception
            Log.e("ImageLoader", "Exception when loading image: " + imageUrl, e);
            imageView.setImageResource(errorImage);
            if (listener != null) {
                listener.onError();
            }
        }
    }

    /**
     * Load a circular image from a URL into an ImageView
     *
     * @param context   The context
     * @param imageUrl  The URL of the image to load
     * @param imageView The target ImageView
     */
    public static void loadCircularImage(Context context, String imageUrl, ImageView imageView) {
        if (context == null || imageView == null) {
            return;
        }
        try {
            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.ic_default_user)
                    .error(R.drawable.ic_default_user)
                    .circleCrop();

            Glide.with(context)
                    .load(imageUrl)
                    .apply(options)
                    .into(imageView);
        } catch (Exception e) {
            imageView.setImageResource(R.drawable.ic_default_user);
        }
    }

    /**
     * Interface for image load callbacks
     */
    public interface ImageLoadListener {
        void onSuccess();

        void onError();
    }
}
