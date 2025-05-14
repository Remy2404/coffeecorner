package com.coffeecorner.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coffeecorner.app.R;
import com.coffeecorner.app.adapters.FeedbackAdapter;
import com.coffeecorner.app.models.FeedbackItem;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class FeedbackActivity extends AppCompatActivity {

    private RecyclerView rvFeedback;
    private FeedbackAdapter feedbackAdapter;
    private MaterialButton btnWriteReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        // Initialize toolbar
        setupToolbar();

        // Initialize feedback list
        setupFeedbackRecyclerView();

        // Set up write review button
        btnWriteReview = findViewById(R.id.btnWriteReview);
        btnWriteReview.setOnClickListener(v -> openWriteReviewDialog());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());
    }

    private void setupFeedbackRecyclerView() {
        rvFeedback = findViewById(R.id.rvFeedback);
        rvFeedback.setLayoutManager(new LinearLayoutManager(this));
        
        // Create sample feedback data
        List<FeedbackItem> feedbackItems = getSampleFeedbackItems();
        
        // Initialize adapter
        feedbackAdapter = new FeedbackAdapter(this, feedbackItems);
        rvFeedback.setAdapter(feedbackAdapter);
    }

    private List<FeedbackItem> getSampleFeedbackItems() {
        List<FeedbackItem> items = new ArrayList<>();
        
        items.add(new FeedbackItem(
                "Sarah Johnson",
                "I love the latte art at this place! The baristas are true artists.",
                4.5f,
                "2 days ago",
                null));
        
        items.add(new FeedbackItem(
                "Michael Chen",
                "Great atmosphere for working. The wifi is reliable and the coffee keeps me going!",
                5.0f,
                "1 week ago",
                null));
        
        items.add(new FeedbackItem(
                "Emma Williams",
                "Delicious pastries. Their croissants are flaky perfection.",
                4.0f,
                "2 weeks ago",
                null));
        
        items.add(new FeedbackItem(
                "David Lee",
                "The service was a bit slow during peak hours, but the coffee was worth the wait.",
                3.5f,
                "3 weeks ago",
                null));
        
        return items;
    }
    
    private void openWriteReviewDialog() {
        // In a real app, this would open a dialog or new activity for writing a review
        // For now, we'll just simulate this
        // TODO: Implement proper review submission
    }
}
