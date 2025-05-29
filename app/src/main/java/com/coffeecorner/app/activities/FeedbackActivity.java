package com.coffeecorner.app.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coffeecorner.app.adapters.FeedbackAdapter;
import com.coffeecorner.app.models.FeedbackItem;
import com.coffeecorner.app.utils.AppUtils;
import com.coffeecorner.app.utils.FeedbackFilterHelper;
import com.coffeecorner.app.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FeedbackActivity extends AppCompatActivity implements FeedbackAdapter.OnFeedbackInteractionListener {

    private RecyclerView rvFeedback;
    private FeedbackAdapter feedbackAdapter;
    private ProgressBar progressBar5, progressBar4, progressBar3, progressBar2, progressBar1;
    private TextView tvAverageRating, tvTotalReviews;
    private TextView tvCount5, tvCount4, tvCount3, tvCount2, tvCount1;
    private RatingBar ratingBarAverage;
    private MaterialButton btnWriteReview;
    private ChipGroup chipGroupRatingFilter;

    // Statistics variables
    private float averageRating = 4.7f;
    private int totalReviews = 128;
    private int count5 = 80; // 80%
    private int count4 = 15; // 15%
    private int count3 = 3; // 3%
    private int count2 = 1; // 1%
    private int count1 = 1; // 1%

    // For managing feedback data
    private List<FeedbackItem> allReviews;
    private List<FeedbackItem> filteredReviews;
    private int currentRatingFilter = 0; // 0 means all ratings

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        // Initialize toolbar
        setupToolbar();

        // Initialize views
        initViews();

        // Setup rating statistics
        setupRatingStats();

        // Setup review list
        setupReviewsList();

        // Setup filter chips
        setupFilterChips();

        // Setup actions
        setupActions();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void initViews() {
        rvFeedback = findViewById(R.id.rvFeedback);
        progressBar5 = findViewById(R.id.progressBar5);
        progressBar4 = findViewById(R.id.progressBar4);
        progressBar3 = findViewById(R.id.progressBar3);
        progressBar2 = findViewById(R.id.progressBar2);
        progressBar1 = findViewById(R.id.progressBar1);
        tvAverageRating = findViewById(R.id.tvAverageRating);
        tvTotalReviews = findViewById(R.id.tvTotalReviews);
        tvCount5 = findViewById(R.id.tvCount5);
        tvCount4 = findViewById(R.id.tvCount4);
        tvCount3 = findViewById(R.id.tvCount3);
        tvCount2 = findViewById(R.id.tvCount2);
        tvCount1 = findViewById(R.id.tvCount1);
        ratingBarAverage = findViewById(R.id.ratingBarAverage);
        btnWriteReview = findViewById(R.id.btnWriteReview);
        chipGroupRatingFilter = findViewById(R.id.chipGroupRatingFilter);
    }

    private void setupRatingStats() {
        // In a real app, this would come from a service/API

        // Set average rating
        tvAverageRating.setText(String.valueOf(averageRating));
        ratingBarAverage.setRating(averageRating);
        tvTotalReviews.setText("Based on " + totalReviews + " reviews");

        // Set progress bars and percentages
        progressBar5.setProgress(count5);
        progressBar4.setProgress(count4);
        progressBar3.setProgress(count3);
        progressBar2.setProgress(count2);
        progressBar1.setProgress(count1);

        tvCount5.setText(count5 + "%");
        tvCount4.setText(count4 + "%");
        tvCount3.setText(count3 + "%");
        tvCount2.setText(count2 + "%");
        tvCount1.setText(count1 + "%");

        // Make the rating bars clickable to filter reviews
        setupRatingBarClickListeners();
    }

    private void setupRatingBarClickListeners() {
        View.OnClickListener ratingFilterListener = v -> {
            int chipId = R.id.chipAll; // Default to show all ratings
            // Map clicked view ID to the corresponding Chip ID
            int viewId = v.getId();
            if (viewId == R.id.progressBar5 || viewId == R.id.tvCount5) {
                chipId = R.id.chip5;
            } else if (viewId == R.id.progressBar4 || viewId == R.id.tvCount4) {
                chipId = R.id.chip4;
            } else if (viewId == R.id.progressBar3 || viewId == R.id.tvCount3) {
                chipId = R.id.chip3;
            } else if (viewId == R.id.progressBar2 || viewId == R.id.tvCount2) {
                chipId = R.id.chip2;
            } else if (viewId == R.id.progressBar1 || viewId == R.id.tvCount1) {
                chipId = R.id.chip1;
            }

            // Check the appropriate chip
            Chip chip = findViewById(chipId);
            if (chip != null) {
                chip.setChecked(true);
            }
        };

        // Apply the listener to all progress bars and percentage texts
        progressBar5.setOnClickListener(ratingFilterListener);
        progressBar4.setOnClickListener(ratingFilterListener);
        progressBar3.setOnClickListener(ratingFilterListener);
        progressBar2.setOnClickListener(ratingFilterListener);
        progressBar1.setOnClickListener(ratingFilterListener);
        tvCount5.setOnClickListener(ratingFilterListener);
        tvCount4.setOnClickListener(ratingFilterListener);
        tvCount3.setOnClickListener(ratingFilterListener);
        tvCount2.setOnClickListener(ratingFilterListener);
        tvCount1.setOnClickListener(ratingFilterListener);
    }

    private void setupFilterChips() {
        chipGroupRatingFilter.setOnCheckedChangeListener((group, checkedId) -> {
            int ratingFilter = 0;

            if (checkedId == R.id.chip5) {
                ratingFilter = 5;
            } else if (checkedId == R.id.chip4) {
                ratingFilter = 4;
            } else if (checkedId == R.id.chip3) {
                ratingFilter = 3;
            } else if (checkedId == R.id.chip2) {
                ratingFilter = 2;
            } else if (checkedId == R.id.chip1) {
                ratingFilter = 1;
            }

            currentRatingFilter = ratingFilter;
            applyFilter();
        });
    }

    private void applyFilter() {
        filteredReviews = FeedbackFilterHelper.filterByRatingRange(allReviews, currentRatingFilter);
        feedbackAdapter.updateFeedbackItems(filteredReviews);

        // Show message if no reviews match the filter
        if (filteredReviews.isEmpty()) {
            Toast.makeText(this, "No reviews match this filter", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupReviewsList() {
        // Set up RecyclerView
        rvFeedback.setLayoutManager(new LinearLayoutManager(this));

        // Create sample list of reviews
        allReviews = getSampleReviews();
        filteredReviews = new ArrayList<>(allReviews); // Start with all reviews

        // Set up adapter with interaction listener
        feedbackAdapter = new FeedbackAdapter(this, filteredReviews, this);
        rvFeedback.setAdapter(feedbackAdapter);
    }

    private List<FeedbackItem> getSampleReviews() {
        List<FeedbackItem> reviews = new ArrayList<>();

        // Sample reviews
        reviews.add(new FeedbackItem(
                "Sarah Johnson",
                "I love the latte art at this place! The baristas are true artists and the ambiance is perfect for both working and casual meet-ups.",
                4.5f,
                "2 days ago",
                ""));

        reviews.add(new FeedbackItem(
                "Michael Chen",
                "The cold brew is outstanding, perfectly balanced and never bitter. My go-to coffee shop whenever I'm in the area.",
                5.0f,
                "1 week ago",
                ""));

        reviews.add(new FeedbackItem(
                "Emily Rodriguez",
                "Great atmosphere and friendly staff, but the prices are a bit steep for what you get. Still, the quality is consistently good.",
                3.5f,
                "2 weeks ago",
                ""));

        reviews.add(new FeedbackItem(
                "David Kim",
                "Their seasonal specials are always creative and delicious. The pumpkin spice latte actually tastes like real pumpkin and not just syrup.",
                5.0f,
                "3 weeks ago",
                ""));

        reviews.add(new FeedbackItem(
                "Jessica Taylor",
                "I've been coming here for years and they never disappoint. The pastries are baked fresh daily and pair perfectly with their coffee.",
                5.0f,
                "1 month ago",
                ""));

        // Add more diverse ratings for demonstration
        reviews.add(new FeedbackItem(
                "James Wilson",
                "Coffee was cold when served. Staff was apologetic but didn't offer a replacement.",
                2.0f,
                "3 weeks ago",
                ""));

        reviews.add(new FeedbackItem(
                "Amanda Lewis",
                "Overall decent but the Wi-Fi is terrible. Hard to get work done here.",
                3.0f,
                "2 months ago",
                ""));

        reviews.add(new FeedbackItem(
                "Robert Garcia",
                "Terrible experience. Coffee was burnt and the barista was rude when I mentioned it.",
                1.0f,
                "1 month ago",
                ""));

        return reviews;
    }

    private void setupActions() {
        btnWriteReview.setOnClickListener(v -> showReviewDialog());
    }

    private void showReviewDialog() {
        // Create and show enhanced review dialog
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_write_review, null);
        RatingBar ratingBar = dialogView.findViewById(R.id.ratingBarDialog);
        TextInputEditText etReviewText = dialogView.findViewById(R.id.etReviewText);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Share Your Experience")
                .setView(dialogView)
                .setPositiveButton("Submit", null) // Set to null initially to prevent auto-dismiss
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();

        // Override the button to validate before dismissing
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            String reviewText = etReviewText.getText().toString().trim();

            if (rating == 0) {
                Toast.makeText(this, "Please provide a rating", Toast.LENGTH_SHORT).show();
                return;
            }

            // Valid submission, proceed
            dialog.dismiss();
            submitReview(rating, reviewText);
        });
    }

    private void submitReview(float rating, String reviewText) {
        // In a real app, we would send this to a backend
        // For now, we'll just add it to our local list

        // Create a new feedback item
        String userName = "You"; // In a real app, get the actual user name
        String timeAgo = "Just now";
        String photoUrl = ""; // Would be user's profile photo

        FeedbackItem newFeedback = new FeedbackItem(
                userName,
                reviewText.isEmpty() ? "Rating only" : reviewText,
                rating,
                timeAgo,
                photoUrl);

        // Add to our full list
        allReviews.add(0, newFeedback);

        // If the current filter would include this review, update the filtered list too
        if (currentRatingFilter == 0 || (int) rating == currentRatingFilter) {
            filteredReviews.add(0, newFeedback);
            feedbackAdapter.notifyItemInserted(0);
            rvFeedback.smoothScrollToPosition(0);
        } else {
            // If the review doesn't match the current filter, suggest changing the filter
            Toast.makeText(this, "Review added. Change filter to 'All' to see it.", Toast.LENGTH_LONG).show();
        }

        // Update statistics
        updateStatistics(rating);

        // Show confirmation
        showSubmissionConfirmation(rating);
    }

    private void updateStatistics(float rating) {
        // Update our review count
        totalReviews++;

        // Recalculate the average (this is simplified, in real app would be more
        // precise)
        float totalStars = averageRating * (totalReviews - 1) + rating;
        averageRating = totalStars / totalReviews;

        // Update the rating bar percentages (simplified for demo)
        if (rating >= 4.5) {
            count5 = Math.round((count5 * (totalReviews - 1) + 100) / totalReviews);
        } else if (rating >= 3.5) {
            count4 = Math.round((count4 * (totalReviews - 1) + 100) / totalReviews);
        } else if (rating >= 2.5) {
            count3 = Math.round((count3 * (totalReviews - 1) + 100) / totalReviews);
        } else if (rating >= 1.5) {
            count2 = Math.round((count2 * (totalReviews - 1) + 100) / totalReviews);
        } else {
            count1 = Math.round((count1 * (totalReviews - 1) + 100) / totalReviews);
        }

        // Update the UI
        tvAverageRating.setText(String.format(Locale.getDefault(), "%.1f", averageRating));
        ratingBarAverage.setRating(averageRating);
        tvTotalReviews.setText("Based on " + totalReviews + " reviews");

        // Update progress bars and percentages
        progressBar5.setProgress(count5);
        progressBar4.setProgress(count4);
        progressBar3.setProgress(count3);
        progressBar2.setProgress(count2);
        progressBar1.setProgress(count1);

        tvCount5.setText(count5 + "%");
        tvCount4.setText(count4 + "%");
        tvCount3.setText(count3 + "%");
        tvCount2.setText(count2 + "%");
        tvCount1.setText(count1 + "%");
    }

    private void showSubmissionConfirmation(float rating) {
        String message = "Thanks for your " + rating + " star review! Your feedback helps us improve.";
        new AlertDialog.Builder(this)
                .setTitle("Review Submitted")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    // Implement the interaction listener methods
    @Override
    public void onHelpfulClicked(FeedbackItem item, int position) {
        // In a real app, you'd send this to a backend
        Toast.makeText(this, "Marked as helpful", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReplyClicked(FeedbackItem item, int position) {
        // Show a reply dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reply to " + item.getUserName());

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Send", (dialog, which) -> {
            String reply = input.getText().toString().trim();
            if (!TextUtils.isEmpty(reply)) {
                Toast.makeText(this, "Reply sent", Toast.LENGTH_SHORT).show();
                // In a real app, you'd send this to the backend
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
