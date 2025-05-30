package com.coffeecorner.app.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.widget.ImageView;
import android.widget.TextView;
import com.coffeecorner.app.R;
import com.coffeecorner.app.utils.PreferencesHelper;
import com.google.android.material.button.MaterialButton;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

/**
 * OnboardingActivity - Introduces users to the Coffee Corner app
 * Displays a series of slides with app features and benefits
 */
public class OnboardingActivity extends AppCompatActivity {
    private PreferencesHelper preferencesHelper;
    private ViewPager2 viewPager;
    private OnboardingAdapter adapter;
    private List<OnboardingItem> onboardingItems;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferencesHelper = new PreferencesHelper(this);
        setContentView(R.layout.activity_onboarding);

        // Make the activity fullscreen using the latest recommended approach
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Window window = getWindow();
            window.setDecorFitsSystemWindows(false);
            WindowInsetsController insetsController = window.getInsetsController();
            if (insetsController != null) {
                insetsController.setSystemBarsBehavior(
                        WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            getWindow().getDecorView().setSystemUiVisibility(flags);
        }

        // Setup onboarding data
        onboardingItems = new ArrayList<>();
        onboardingItems.add(new OnboardingItem(R.drawable.onboarding_welcome1, "Welcome to Coffee Corner", "Your perfect coffee experience starts here. Explore our selection of premium coffee and treats."));
        onboardingItems.add(new OnboardingItem(R.drawable.onboarding_menu, "Order Easily", "Browse our menu and order your favorite coffee in seconds."));
        onboardingItems.add(new OnboardingItem(R.drawable.onboarding_sorry_bro_heng, "Earn Rewards", "Collect points with every purchase and enjoy exclusive rewards!"));

        viewPager = findViewById(R.id.viewPager);
        adapter = new OnboardingAdapter(onboardingItems);
        viewPager.setAdapter(adapter);

        // Tab indicators (optional, if you have TabLayout)
        // TabLayout tabLayout = findViewById(R.id.tabLayout);
        // new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {}).attach();
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

    // --- OnboardingItem data model ---
    static class OnboardingItem {
        int imageResId;
        String title;
        String description;
        OnboardingItem(int imageResId, String title, String description) {
            this.imageResId = imageResId;
            this.title = title;
            this.description = description;
        }
    }

    // --- Adapter for ViewPager2 ---
    class OnboardingAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {
        private final List<OnboardingItem> items;
        OnboardingAdapter(List<OnboardingItem> items) { this.items = items; }

        @Override
        public OnboardingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.onboarding_slide, parent, false);
            return new OnboardingViewHolder(view);
        }

        @Override
        public void onBindViewHolder(OnboardingViewHolder holder, int position) {
            OnboardingItem item = items.get(position);
            holder.imgOnboarding.setImageResource(item.imageResId);
            holder.tvTitle.setText(item.title);
            holder.tvDescription.setText(item.description);
            if (position == items.size() - 1) {
                holder.btnNext.setText("Get Started");
            } else {
                holder.btnNext.setText("Next");
            }
            holder.btnSkip.setOnClickListener(v -> navigateToLogin());
            holder.btnNext.setOnClickListener(v -> {
                if (position == items.size() - 1) {
                    navigateToLogin();
                } else {
                    viewPager.setCurrentItem(position + 1, true);
                }
            });
        }

        @Override
        public int getItemCount() { return items.size(); }

        class OnboardingViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
            ImageView imgOnboarding;
            TextView tvTitle, tvDescription;
            MaterialButton btnSkip, btnNext;
            OnboardingViewHolder(View itemView) {
                super(itemView);
                imgOnboarding = itemView.findViewById(R.id.imgOnboarding);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvDescription = itemView.findViewById(R.id.tvDescription);
                btnSkip = itemView.findViewById(R.id.btnSkip);
                btnNext = itemView.findViewById(R.id.btnNext);
            }
        }
    }
}
