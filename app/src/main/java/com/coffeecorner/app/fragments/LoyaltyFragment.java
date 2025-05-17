package com.coffeecorner.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coffeecorner.app.R;
import com.coffeecorner.app.activities.MainActivity;
import com.google.android.material.progressindicator.LinearProgressIndicator;

public class LoyaltyFragment extends Fragment {

    private TextView tvPoints;
    private TextView tvLevel;
    private TextView tvNextReward;
    private LinearProgressIndicator progressRewards;
    private RecyclerView rvRewards;

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
    }

    private void loadLoyaltyData() {
        // In a real app, this would come from the API
        // For now we'll use mock data
        int points = 350;
        String level = "Gold Member";
        int pointsToNextReward = 50;
        
        tvPoints.setText(String.valueOf(points));
        tvLevel.setText(level);
        tvNextReward.setText(getString(R.string.points_to_next_reward, pointsToNextReward));
        
        // Set progress to next reward (assuming 400 points needed)
        progressRewards.setProgress((points % 400) * 100 / 400);
    }

    private void setupRewards() {
        // Set up RecyclerView for available rewards
        rvRewards.setLayoutManager(new LinearLayoutManager(requireContext()));
        // In a real app, you'd add an adapter with reward items
        // rewardsAdapter = new RewardsAdapter(rewardsList);
        // rvRewards.setAdapter(rewardsAdapter);
    }

    private void showMessage(String message) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showToast(message);
        }
    }
}
