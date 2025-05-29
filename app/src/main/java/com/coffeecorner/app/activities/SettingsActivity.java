package com.coffeecorner.app.activities;

import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.coffeecorner.app.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings);

        setupToolbar();
        // Add your settings specific logic here
        // e.g., loading preferences, handling clicks on settings items
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar); // Assuming you have a Toolbar with id 'toolbar' in your layout
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            // getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Optional: if you
            // want a back arrow
        }

        ImageButton btnBack = findViewById(R.id.btnBack); // Assuming you have a back button with id 'btnBack'
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        }
    }
}
