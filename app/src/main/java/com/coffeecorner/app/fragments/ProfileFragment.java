package com.coffeecorner.app.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.coffeecorner.app.R;
import com.coffeecorner.app.activities.LoginActivity;
import com.coffeecorner.app.models.User;
import com.coffeecorner.app.viewmodel.UserViewModel;
import com.coffeecorner.app.viewmodel.UserViewModelFactory;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ProfileFragment extends Fragment {

    private ImageView ivProfilePic;
    private TextView tvUsername, tvEmail, tvLoyaltyPoints, tvTotalOrders, tvMemberSince;
    private ImageButton btnEditProfile;
    private View btnSettings;
    private UserViewModel userViewModel;
    private Switch switchTheme;
    private static final String PREFS_NAME = "app_prefs";
    private static final String KEY_DARK_MODE = "dark_mode";
    private boolean isThemeSwitching = false;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize ViewModel with custom factory
        userViewModel = new ViewModelProvider(requireActivity(), new UserViewModelFactory(requireContext())).get(UserViewModel.class);

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
        setupClickListeners(view);

        // Set initial switch state based on saved preference
        isThemeSwitching = true;
        switchTheme.setChecked(getDarkModePref());
        isThemeSwitching = false;
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isThemeSwitching) return;
            saveDarkModePref(isChecked);
            int newMode = isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
            if (AppCompatDelegate.getDefaultNightMode() != newMode) {
                AppCompatDelegate.setDefaultNightMode(newMode);
            }
        });
    }

    private void initializeViews(View view) {
        ivProfilePic = view.findViewById(R.id.imgProfile);
        tvUsername = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvLoyaltyPoints = view.findViewById(R.id.tvLoyaltyPoints);
        tvTotalOrders = view.findViewById(R.id.tvTotalOrders);
        tvMemberSince = view.findViewById(R.id.tvMemberSince);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnSettings = view.findViewById(R.id.btnSettings);
        switchTheme = view.findViewById(R.id.switchTheme);
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

    private void setupClickListeners(View view) {
        btnEditProfile.setOnClickListener(v -> {
            // Navigate to edit profile screen
            Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_editProfileFragment);
        });

        btnSettings.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Settings clicked", Toast.LENGTH_SHORT).show();
        });

        // Setup click listeners for action items
        view.findViewById(R.id.layoutMyOrders).setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_orderHistoryFragment);
        });

        view.findViewById(R.id.layoutLogout).setOnClickListener(v -> {
            showLogoutConfirmationDialog();
        });
    }

    private void updateUI(User user) {
        tvUsername.setText(user.getName());
        tvEmail.setText(user.getEmail());
        if (user.getLoyaltyPoints() != null) {
            tvLoyaltyPoints.setText(String.valueOf(user.getLoyaltyPoints()));
        }
        if (user.getTotalOrders() != null) {
            tvTotalOrders.setText(String.valueOf(user.getTotalOrders()));
        }
        if (user.getMemberSince() != null) {
            tvMemberSince.setText(user.getMemberSince());
        }

        if (user.getPhotoUrl() != null && !user.getPhotoUrl().isEmpty()) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.default_profile)
                    .error(R.drawable.default_profile)
                    .into(ivProfilePic);
        } else {
            ivProfilePic.setImageResource(R.drawable.default_profile);
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

    private void saveDarkModePref(boolean isDark) {
        requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_DARK_MODE, isDark)
            .apply();
    }

    private boolean getDarkModePref() {
        return requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_DARK_MODE, false);
    }
}
