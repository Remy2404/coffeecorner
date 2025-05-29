package com.coffeecorner.app.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coffeecorner.app.R;
import com.coffeecorner.app.adapters.RewardAdapter;
import com.coffeecorner.app.models.Reward;

import java.util.ArrayList;
import java.util.List;

public class MyRewardsFragment extends Fragment implements RewardAdapter.OnRewardActionListener {

    private TextView tvTotalPoints;
    private TextView tvMembershipTier;
    private TextView tvMembershipDescription;
    private ImageView ivMembershipBadge;
    private ProgressBar progressReward;
    private RecyclerView rvRewards;
    private RecyclerView rvPointsHistory;

    private RewardAdapter rewardAdapter;
    private List<Reward> rewardList;

    private int currentPoints = 2350;
    private String currentTier = "Gold Member";
    private int pointsToNextReward = 150;
    private int totalPointsForReward = 500;

    public MyRewardsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rewards, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupClickListeners();
        setupRewards();
        loadUserData();
    }

    private void initViews(View view) {
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        tvTotalPoints = view.findViewById(R.id.tvTotalPoints);
        tvMembershipTier = view.findViewById(R.id.tvMembershipTier);
        tvMembershipDescription = view.findViewById(R.id.tvMembershipDescription);
        ivMembershipBadge = view.findViewById(R.id.ivMembershipBadge);
        progressReward = view.findViewById(R.id.progressReward);
        rvRewards = view.findViewById(R.id.rvRewards);
        rvPointsHistory = view.findViewById(R.id.rvPointsHistory);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        }
    }

    private void setupClickListeners() {
        // No additional click listeners needed for this version
    }

    private void setupRewards() {
        rewardList = generateSampleRewards();

        rewardAdapter = new RewardAdapter(requireContext(), rewardList);
        rewardAdapter.setOnRewardActionListener(this);

        rvRewards.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvRewards.setAdapter(rewardAdapter);
        rvRewards.setNestedScrollingEnabled(false);

        // Setup points history RecyclerView - placeholder for now
        rvPointsHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvPointsHistory.setNestedScrollingEnabled(false);
    }

    private void loadUserData() {
        // Display current points
        if (tvTotalPoints != null) {
            tvTotalPoints.setText(String.valueOf(currentPoints));
        }

        // Display membership information
        if (tvMembershipTier != null) {
            tvMembershipTier.setText(currentTier);
        }

        if (tvMembershipDescription != null) {
            tvMembershipDescription
                    .setText("Enjoy exclusive " + currentTier.split(" ")[0] + " benefits until Dec 31, 2024");
        }

        // Set membership badge based on tier
        if (ivMembershipBadge != null) {
            if (currentTier.contains("Gold")) {
                ivMembershipBadge.setImageResource(R.drawable.badge_gold);
            } else if (currentTier.contains("Silver")) {
                ivMembershipBadge.setImageResource(R.drawable.badge_silver);
            } else {
                ivMembershipBadge.setImageResource(R.drawable.badge_bronze);
            }
        }

        // Setup progress bar
        if (progressReward != null) {
            int currentProgress = totalPointsForReward - pointsToNextReward;
            progressReward.setMax(totalPointsForReward);
            progressReward.setProgress(currentProgress);
        }
    }

    private List<Reward> generateSampleRewards() {
        List<Reward> rewards = new ArrayList<>();

        rewards.add(new Reward(
                "Free Coffee",
                "Get any regular size coffee for free",
                500,
                R.drawable.ic_coffee));

        rewards.add(new Reward(
                "Free Pastry",
                "Choose any pastry from our selection",
                300,
                R.drawable.ic_cake));

        rewards.add(new Reward(
                "Size Upgrade",
                "Upgrade any drink to the largest size",
                200,
                R.drawable.ic_trending_up));

        rewards.add(new Reward(
                "10% Discount",
                "Get 10% off your next order",
                800,
                R.drawable.ic_local_offer));

        rewards.add(new Reward(
                "Free Lunch Combo",
                "Get a free sandwich and drink combo",
                1200,
                R.drawable.ic_fastfood));

        rewards.add(new Reward(
                "Birthday Special",
                "Free birthday cake slice with any purchase",
                1500,
                R.drawable.ic_cake));

        return rewards;
    }

    @Override
    public void onRedeemClicked(Reward reward, int position) {
        if (currentPoints >= reward.getPointsRequired()) {
            showRedeemConfirmationDialog(reward, position);
        } else {
            int pointsNeeded = reward.getPointsRequired() - currentPoints;
            Toast.makeText(requireContext(),
                    "You need " + pointsNeeded + " more points to redeem this reward",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void showRedeemConfirmationDialog(Reward reward, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Redeem Reward")
                .setMessage("Are you sure you want to redeem \"" + reward.getTitle() + "\" for " +
                        reward.getPointsRequired() + " points?")
                .setPositiveButton("Redeem", (dialog, which) -> {
                    redeemReward(reward, position);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void redeemReward(Reward reward, int position) {
        // Deduct points
        currentPoints -= reward.getPointsRequired();

        // Update UI
        loadUserData();

        // Show success message
        Toast.makeText(requireContext(),
                "Successfully redeemed: " + reward.getTitle() + "!",
                Toast.LENGTH_LONG).show();

        // Show redemption code or instructions
        showRedemptionInstructions(reward);
    }

    private void showRedemptionInstructions(Reward reward) {
        String redemptionCode = "RC" + System.currentTimeMillis() % 100000;

        new AlertDialog.Builder(requireContext())
                .setTitle("Reward Redeemed!")
                .setMessage("Your " + reward.getTitle() + " is ready!\n\n" +
                        "Redemption Code: " + redemptionCode + "\n\n" +
                        "Show this code to the cashier to claim your reward. " +
                        "Valid for 30 days from today.")
                .setPositiveButton("Got it", null)
                .show();
    }

    public void refreshData() {
        loadUserData();
        if (rewardAdapter != null) {
            rewardAdapter.notifyDataSetChanged();
        }
    }
}
