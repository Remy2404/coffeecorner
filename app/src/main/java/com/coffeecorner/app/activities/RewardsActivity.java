package com.coffeecorner.app.activities;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coffeecorner.app.adapters.RewardAdapter;
import com.coffeecorner.app.adapters.PointsHistoryAdapter;
import com.coffeecorner.app.models.Reward;
import com.coffeecorner.app.models.PointsHistory;
import com.coffeecorner.app.R;

import java.util.ArrayList;
import java.util.List;

public class RewardsActivity extends AppCompatActivity {

    private RecyclerView rvRewards;
    private RecyclerView rvPointsHistory;
    private RewardAdapter rewardAdapter;
    private PointsHistoryAdapter pointsHistoryAdapter;
    private TextView tvTotalPoints;
    private ProgressBar progressReward;
    private TextView tvMembershipTier;
    private TextView tvMembershipDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards);

        // Initialize views
        tvTotalPoints = findViewById(R.id.tvTotalPoints);
        progressReward = findViewById(R.id.progressReward);
        tvMembershipTier = findViewById(R.id.tvMembershipTier);
        tvMembershipDescription = findViewById(R.id.tvMembershipDescription);

        // Initialize toolbar
        setupToolbar();

        // Setup user's reward information
        setupUserRewardsInfo();

        // Initialize rewards recycler view
        setupRewardsRecyclerView();

        // Initialize points history recycler view
        setupPointsHistoryRecyclerView();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());
    }

    private void setupUserRewardsInfo() {
        // This would typically come from a user profile or backend service
        // For demo purposes, we'll use hardcoded values
        tvTotalPoints.setText("2,350");
        progressReward.setMax(500);
        progressReward.setProgress(350);
        tvMembershipTier.setText("Gold Member");
        tvMembershipDescription.setText("Enjoy exclusive Gold benefits until Dec 31, 2023");
    }

    private void setupRewardsRecyclerView() {
        rvRewards = findViewById(R.id.rvRewards);
        rvRewards.setLayoutManager(new LinearLayoutManager(this));

        // Create sample rewards data
        List<Reward> rewards = getSampleRewards();

        // Initialize adapter
        rewardAdapter = new RewardAdapter(this, rewards);
        rvRewards.setAdapter(rewardAdapter);
    }

    private List<Reward> getSampleRewards() {
        List<Reward> rewards = new ArrayList<>();

        rewards.add(new Reward(
                "Free Cappuccino",
                "Any size, valid for 30 days",
                500,
                R.drawable.ic_coffee));

        rewards.add(new Reward(
                "Free Pastry",
                "Any pastry from our bakery section",
                350,
                R.drawable.ic_coffee));

        rewards.add(new Reward(
                "50% Off Any Drink",
                "Valid for one transaction",
                250,
                R.drawable.ic_coffee));

        return rewards;
    }

    private void setupPointsHistoryRecyclerView() {
        rvPointsHistory = findViewById(R.id.rvPointsHistory);
        rvPointsHistory.setLayoutManager(new LinearLayoutManager(this));

        // Create sample points history data
        List<PointsHistory> pointsHistoryList = getSamplePointsHistory();

        // Initialize adapter
        pointsHistoryAdapter = new PointsHistoryAdapter(this, pointsHistoryList);
        rvPointsHistory.setAdapter(pointsHistoryAdapter);
    }

    private List<PointsHistory> getSamplePointsHistory() {
        List<PointsHistory> pointsHistoryList = new ArrayList<>();

        pointsHistoryList.add(new PointsHistory(
                "Purchase at Downtown Branch",
                "May 12, 2023 • 10:45 AM",
                75,
                true));

        pointsHistoryList.add(new PointsHistory(
                "Redeemed Free Latte",
                "May 10, 2023 • 3:20 PM",
                500,
                false));

        pointsHistoryList.add(new PointsHistory(
                "Purchase at Riverside Branch",
                "May 8, 2023 • 8:15 AM",
                60,
                true));

        pointsHistoryList.add(new PointsHistory(
                "Birthday Bonus",
                "May 5, 2023 • 12:00 AM",
                250,
                true));

        pointsHistoryList.add(new PointsHistory(
                "Purchase at Toul Kork Branch",
                "May 2, 2023 • 2:30 PM",
                55,
                true));

        return pointsHistoryList;
    }
}
