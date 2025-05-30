package com.coffeecorner.app.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.List;

public class LoyaltyFragment extends Fragment implements RewardAdapter.OnRewardActionListener {

    private TextView tvPoints;
    private TextView tvLevel;
    private TextView tvNextReward;
    private LinearProgressIndicator progressRewards;
    private RecyclerView rvRewards;
    private RewardAdapter rewardAdapter;
    private List<Reward> rewardList;
    
    // Current user points for redemption logic
    private int currentUserPoints = 350;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_loyalty, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize views
        initializeViews(view);
        
        // Load user loyalty data
        loadLoyaltyData();
        
        // Setup available rewards
        setupRewards();
    }

    private void initializeViews(View view) {
        tvPoints = view.findViewById(R.id.tvPoints);
        tvLevel = view.findViewById(R.id.tvLevel);
        tvNextReward = view.findViewById(R.id.tvNextReward);
        progressRewards = view.findViewById(R.id.progressRewards);
        rvRewards = view.findViewById(R.id.rvRewards);
    }    private void loadLoyaltyData() {
        // In a real app, this would come from the API
        // For now we'll use mock data
        int points = currentUserPoints;
        String level = "Gold Member";
        int pointsToNextReward = 50;
        
        tvPoints.setText(String.valueOf(points));
        tvLevel.setText(level);
        tvNextReward.setText(getString(R.string.points_to_next_reward, pointsToNextReward));
        
        // Set progress to next reward (assuming 400 points needed)
        progressRewards.setProgress((points % 400) * 100 / 400);
    }    private void setupRewards() {
        // Set up RecyclerView for available rewards
        rvRewards.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        // Generate sample rewards
        rewardList = generateSampleRewards();
        
        // Initialize and set up the RewardAdapter
        rewardAdapter = new RewardAdapter(requireContext(), rewardList);
        rewardAdapter.setOnRewardActionListener(this);
        rvRewards.setAdapter(rewardAdapter);
        rvRewards.setNestedScrollingEnabled(false);
    }
    
    private List<Reward> generateSampleRewards() {
        List<Reward> rewards = new ArrayList<>();
        
        rewards.add(new Reward(
                "Free Coffee",
                "Get any regular size coffee for free",
                250,
                R.drawable.ic_coffee));
                
        rewards.add(new Reward(
                "Free Pastry",
                "Choose any pastry from our selection",
                150,
                R.drawable.ic_cake));
                
        rewards.add(new Reward(
                "Size Upgrade",
                "Upgrade any drink to the largest size",
                100,
                R.drawable.ic_trending_up));
                
        rewards.add(new Reward(
                "10% Discount",
                "Get 10% off your next order",
                400,
                R.drawable.ic_local_offer));
                
        rewards.add(new Reward(
                "Free Lunch Combo",
                "Get a free sandwich and drink combo",
                600,
                R.drawable.ic_fastfood));
                
        return rewards;
    }
    
    @Override
    public void onRedeemClicked(Reward reward, int position) {
        if (currentUserPoints >= reward.getPointsRequired()) {
            showRedeemConfirmationDialog(reward, position);
        } else {
            int pointsNeeded = reward.getPointsRequired() - currentUserPoints;
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
        currentUserPoints -= reward.getPointsRequired();
        
        // Update UI
        loadLoyaltyData();
        
        // Show success message
        Toast.makeText(requireContext(),
                "Successfully redeemed: " + reward.getTitle() + "!",
                Toast.LENGTH_LONG).show();
                
        // Show redemption instructions
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
}
