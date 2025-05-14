package com.coffeecorner.app.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coffeecorner.app.R;
import com.coffeecorner.app.adapters.LocationAdapter;
import com.coffeecorner.app.models.Location;

import java.util.ArrayList;
import java.util.List;

public class AboutUsActivity extends AppCompatActivity {

    private RecyclerView rvLocations;
    private LocationAdapter locationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        // Initialize toolbar
        setupToolbar();

        // Initialize locations recycler view
        setupLocationsRecyclerView();

        // Setup social media buttons
        setupSocialMediaButtons();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());
    }

    private void setupLocationsRecyclerView() {
        rvLocations = findViewById(R.id.rvLocations);
        rvLocations.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        
        // Create sample location data
        List<Location> locations = getSampleLocations();
        
        // Initialize adapter
        locationAdapter = new LocationAdapter(this, locations);
        rvLocations.setAdapter(locationAdapter);
    }

    private List<Location> getSampleLocations() {
        List<Location> locations = new ArrayList<>();
        
        locations.add(new Location(
                "BKK1 Branch",
                "123 Norodom Blvd, Phnom Penh",
                "Open 7AM-9PM daily",
                "+855 23 123 4567",
                "12.567890,104.923456"));
        
        locations.add(new Location(
                "Riverside Branch",
                "45 Sisowath Quay, Phnom Penh",
                "Open 6AM-10PM daily",
                "+855 23 987 6543",
                "12.576543,104.934567"));
        
        locations.add(new Location(
                "Toul Kork Branch",
                "78 Street 315, Phnom Penh",
                "Open 7AM-8PM daily",
                "+855 23 456 7890",
                "12.587654,104.912345"));
        
        return locations;
    }

    private void setupSocialMediaButtons() {
        ImageButton btnFacebook = findViewById(R.id.btnFacebook);
        ImageButton btnInstagram = findViewById(R.id.btnInstagram);
        ImageButton btnTwitter = findViewById(R.id.btnTwitter);
        ImageButton btnYoutube = findViewById(R.id.btnYoutube);

        btnFacebook.setOnClickListener(v -> openSocialMedia("https://facebook.com/coffeecorner"));
        btnInstagram.setOnClickListener(v -> openSocialMedia("https://instagram.com/coffeecorner"));
        btnTwitter.setOnClickListener(v -> openSocialMedia("https://twitter.com/coffeecorner"));
        btnYoutube.setOnClickListener(v -> openSocialMedia("https://youtube.com/coffeecorner"));
    }

    private void openSocialMedia(String url) {
        // Open social media link using intent
        // This will be implemented using an intent to open the URL
        // You can use a utility method from a helper class for this
        // For now, we'll just show a message
        // TODO: Implement with AppUtils.openUrl(this, url);
    }
}
