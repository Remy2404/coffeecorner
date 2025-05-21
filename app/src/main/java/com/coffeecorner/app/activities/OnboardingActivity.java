package com.coffeecorner.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.coffeecorner.app.R;
import com.coffeecorner.app.utils.PreferencesHelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * OnboardingActivity - Introduces users to the Coffee Corner app
 * Displays a series of slides with app features and benefits
 */
public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private MaterialButton btnNext;
    private PreferencesHelper preferencesHelper;

    // Content for the onboarding slides
    private final String[] titles = {
            "Welcome to Coffee Corner",
            "Discover Our Menu",
            "Fast Delivery",
            "Earn Rewards"
    };

    private final String[] descriptions = {
            "Your perfect coffee experience starts here. Explore our selection of premium coffee and treats.",
            "Browse our wide range of coffee, tea, and pastries. Customize your order just the way you like it.",
            "Order ahead and pick up at your convenience, or get it delivered right to your doorstep.",
            "Earn points with every purchase and redeem them for free drinks and special offers."
    };

    // Placeholder for actual drawable resources to be created
    private final int[] images = {
            R.drawable.onboarding_welcome,
            R.drawable.onboarding_menu,
            R.drawable.onboarding_delivery,
            R.drawable.onboarding_rewards
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize PreferencesHelper
        preferencesHelper = new PreferencesHelper(this);

        // Make the activity fullscreen
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_onboarding);

        // Initialize views
        viewPager = findViewById(R.id.viewPagerOnboarding);
        btnNext = findViewById(R.id.btnNext);
        MaterialButton btnSkip = findViewById(R.id.btnSkip);
        TabLayout tabIndicator = findViewById(R.id.tabIndicator);

        // Set up the ViewPager with the slides adapter
        OnboardingAdapter adapter = new OnboardingAdapter();
        viewPager.setAdapter(adapter);

        // Connect the tab indicators with the ViewPager
        new TabLayoutMediator(tabIndicator, viewPager,
                (tab, position) -> {
                    // No text for tabs, just the indicator dots
                }).attach();

        // Next button click logic
        btnNext.setOnClickListener(v -> {
            // If at the last slide, go to login
            if (viewPager.getCurrentItem() == titles.length - 1) {
                navigateToLogin();
            } else {
                // Otherwise, go to the next slide
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
        });

        // Skip button click logic - go straight to login
        btnSkip.setOnClickListener(v -> navigateToLogin());

        // Set up a listener to update the Next button text on the last slide
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == titles.length - 1) {
                    btnNext.setText("Get Started");
                } else {
                    btnNext.setText("Next");
                }
            }
        });
    }

    /**
     * Navigate to the login screen when onboarding is complete
     */
    private void navigateToLogin() {
        // Save onboarding completed state in SharedPreferences
        preferencesHelper.setOnboardingCompleted(true);

        // Start LoginActivity
        Intent intent = new Intent(OnboardingActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Close this activity so it's not in the back stack
    }

    /**
     * Adapter for the onboarding slides
     */
    private class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {

        @NonNull
        @Override
        public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new OnboardingViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(
                            R.layout.onboarding_slide, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
            holder.setData(titles[position], descriptions[position], images[position]);
        }

        @Override
        public int getItemCount() {
            return titles.length;
        }

        /**
         * ViewHolder for each onboarding slide
         */
        class OnboardingViewHolder extends RecyclerView.ViewHolder {

            private final TextView tvTitle;
            private final TextView tvDescription;
            private final ImageView imgOnboarding;

            OnboardingViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvDescription = itemView.findViewById(R.id.tvDescription);
                imgOnboarding = itemView.findViewById(R.id.imgOnboarding);
            }

            void setData(String title, String description, int imageResource) {
                tvTitle.setText(title);
                tvDescription.setText(description);
                imgOnboarding.setImageResource(imageResource);
            }
        }
    }
}