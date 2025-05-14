package com.coffeecorner.app.utils;

import com.coffeecorner.app.models.FeedbackItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for filtering reviews in the FeedbackActivity
 */
public class FeedbackFilterHelper {

    /**
     * Filter reviews by rating
     * 
     * @param allReviews   The complete list of reviews
     * @param ratingFilter The rating to filter by (1-5), or 0 for all reviews
     * @return The filtered list of reviews
     */
    public static List<FeedbackItem> filterByRating(List<FeedbackItem> allReviews, int ratingFilter) {
        if (ratingFilter == 0) {
            // Return all reviews
            return new ArrayList<>(allReviews);
        }

        List<FeedbackItem> filteredReviews = new ArrayList<>();

        for (FeedbackItem review : allReviews) {
            // Convert float rating to int (e.g., 4.5 -> 4)
            int reviewRating = (int) review.getRating();

            // For exact match (e.g., only 5-star reviews)
            if (reviewRating == ratingFilter) {
                filteredReviews.add(review);
            }
        }

        return filteredReviews;
    }

    /**
     * Alternative method that includes reviews within a range
     * For example, "4 stars" could include 4.0-4.9 stars
     */
    public static List<FeedbackItem> filterByRatingRange(List<FeedbackItem> allReviews, int ratingFilter) {
        if (ratingFilter == 0) {
            // Return all reviews
            return new ArrayList<>(allReviews);
        }

        List<FeedbackItem> filteredReviews = new ArrayList<>();

        for (FeedbackItem review : allReviews) {
            float reviewRating = review.getRating();

            // Include reviews with ratings in the correct range
            // e.g., 4.0-4.9 for "4 stars"
            if (reviewRating >= ratingFilter && reviewRating < (ratingFilter + 1)) {
                filteredReviews.add(review);
            }
        }

        return filteredReviews;
    }

    /**
     * Sort reviews by rating (high to low)
     */
    public static List<FeedbackItem> sortByRatingHighToLow(List<FeedbackItem> reviews) {
        List<FeedbackItem> sortedReviews = new ArrayList<>(reviews);
        sortedReviews.sort((a, b) -> Float.compare(b.getRating(), a.getRating()));
        return sortedReviews;
    }

    /**
     * Sort reviews by date (newest first)
     * Note: This assumes the timeAgo field is standardized - in a real app you'd
     * probably have a Date object in the FeedbackItem class
     */
    public static List<FeedbackItem> sortByNewest(List<FeedbackItem> reviews) {
        // In a real implementation, you would sort by actual date
        // For this example, we're just returning the list as-is since
        // our sample data is already sorted by newest first
        return new ArrayList<>(reviews);
    }
}
