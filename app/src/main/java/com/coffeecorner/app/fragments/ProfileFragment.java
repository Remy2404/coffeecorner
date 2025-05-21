package com.coffeecorner.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.coffeecorner.app.R;
import com.coffeecorner.app.activities.LoginActivity;
import com.coffeecorner.app.models.User;
import com.coffeecorner.app.viewmodels.UserViewModel;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ProfileFragment extends Fragment {

    private ImageView ivProfilePic;
    private TextView tvUsername, tvEmail;
    private Button btnEditProfile, btnLogout;
    private MaterialCardView cvOrderHistory, cvAddresses, cvPaymentMethods,
            cvNotifications, cvSettings, cvHelp, cvAbout;
    private UserViewModel userViewModel;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize ViewModel
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        initializeViews(view);

        // Setup observers
        setupObservers();

        // Setup click listeners
        setupClickListeners();
    }

    private void initializeViews(View view) {
        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvEmail = view.findViewById(R.id.tvEmail);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnLogout = view.findViewById(R.id.btnLogout);
        cvOrderHistory = view.findViewById(R.id.cvOrderHistory);
        cvAddresses = view.findViewById(R.id.cvAddresses);
        cvPaymentMethods = view.findViewById(R.id.cvPaymentMethods);
        cvNotifications = view.findViewById(R.id.cvNotifications);
        cvSettings = view.findViewById(R.id.cvSettings);
        cvHelp = view.findViewById(R.id.cvHelp);
        cvAbout = view.findViewById(R.id.cvAbout);
    }

    private void setupObservers() {
        userViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                updateUI(user);
            } else {
                // User not logged in, navigate to login screen
                navigateToLogin();
            }
        });
    }

    private void setupClickListeners() {
        btnEditProfile.setOnClickListener(v -> {
            // Navigate to edit profile screen
            Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_editProfileFragment);
        });

        btnLogout.setOnClickListener(v -> {
            showLogoutConfirmationDialog();
        });

        cvOrderHistory.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_orderHistoryFragment);
        });

        cvAddresses.setOnClickListener(v -> {
            // Navigate to addresses screen
            Toast.makeText(requireContext(), "Addresses coming soon", Toast.LENGTH_SHORT).show();
        });

        cvPaymentMethods.setOnClickListener(v -> {
            // Navigate to payment methods screen
            Toast.makeText(requireContext(), "Payment methods coming soon", Toast.LENGTH_SHORT).show();
        });

        cvNotifications.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_notificationFragment);
        });

        cvSettings.setOnClickListener(v -> {
            // Navigate to settings screen
            Toast.makeText(requireContext(), "Settings coming soon", Toast.LENGTH_SHORT).show();
        });

        cvHelp.setOnClickListener(v -> {
            // Navigate to help screen
            Toast.makeText(requireContext(), "Help center coming soon", Toast.LENGTH_SHORT).show();
        });

        cvAbout.setOnClickListener(v -> {
            // Navigate to about screen
            Toast.makeText(requireContext(), "About us coming soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateUI(User user) {
        tvUsername.setText(user.getName());
        tvEmail.setText(user.getEmail());

        if (user.getPhotoUrl() != null && !user.getPhotoUrl().isEmpty()) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.default_profile_image)
                    .error(R.drawable.default_profile_image)
                    .into(ivProfilePic);
        } else {
            ivProfilePic.setImageResource(R.drawable.default_profile_image);
        }
    }

    private void showLogoutConfirmationDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    userViewModel.logout();
                    navigateToLogin();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
