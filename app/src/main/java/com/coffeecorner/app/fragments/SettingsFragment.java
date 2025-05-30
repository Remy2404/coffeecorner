package com.coffeecorner.app.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.coffeecorner.app.R;
import com.coffeecorner.app.activities.LoginActivity;
import com.coffeecorner.app.utils.PreferencesHelper;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Objects;

public class SettingsFragment extends Fragment {

    private NavController navController;
    private PreferencesHelper preferencesHelper;

    // Theme switching state management
    private boolean isThemeSwitching = false;
    private Handler themeHandler = new Handler(Looper.getMainLooper());

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        try {
            return inflater.inflate(R.layout.fragment_settings, container, false);
        } catch (Exception e) {
            // Log the error and provide a fallback empty view if inflation fails
            android.util.Log.e("SettingsFragment", "Error inflating layout", e);
            return new View(requireContext());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);        navController = Navigation.findNavController(view);
        preferencesHelper = new PreferencesHelper(requireContext());
        
        // Set up the back button
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        }
        
        // Initialize UI elements
        RelativeLayout layoutEditProfile = view.findViewById(R.id.layoutEditProfile);
        RelativeLayout layoutPaymentMethods = view.findViewById(R.id.layoutPaymentMethods);
        RelativeLayout layoutAboutUs = view.findViewById(R.id.layoutAboutUs);
        SwitchMaterial switchNotifications = view.findViewById(R.id.switchNotifications);
        SwitchMaterial switchDarkMode = view.findViewById(R.id.switchDarkMode);
        RelativeLayout layoutPrivacyPolicy = view.findViewById(R.id.layoutPrivacyPolicy);
        RelativeLayout layoutTermsConditions = view.findViewById(R.id.layoutTermsConditions);
        Button btnLogout = view.findViewById(R.id.btnLogout);
        
        // Set initial states for switches
        switchNotifications.setChecked(preferencesHelper.isNotificationsEnabled());
        // Initialize dark mode switch from PreferencesHelper (centralized source of truth)
        switchDarkMode.setChecked(preferencesHelper.isDarkModeEnabled());
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isThemeSwitching) return;
            isThemeSwitching = true;
            preferencesHelper.setDarkModeEnabled(isChecked);
            themeHandler.removeCallbacksAndMessages(null);
            themeHandler.postDelayed(() -> {
                try {
                    int targetMode = isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
                    if (AppCompatDelegate.getDefaultNightMode() != targetMode) {
                        AppCompatDelegate.setDefaultNightMode(targetMode);
                        requireActivity().recreate();
                    }
                } finally {
                    isThemeSwitching = false;
                }
            }, 200);
        });

        // Set up click listeners
        layoutEditProfile.setOnClickListener(v -> {
            // Navigate to EditProfileFragment - Ensure this action exists in your nav_graph
            if (Objects.requireNonNull(navController.getCurrentDestination()).getId() == R.id.settingsFragment) {
                navController.navigate(R.id.action_settingsFragment_to_editProfileFragment);
            }
        });

        layoutPaymentMethods.setOnClickListener(v -> {
            try {
                navController.navigate(R.id.action_settingsFragment_to_paymentMethodsFragment);
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Payment Methods screen is under development", Toast.LENGTH_SHORT).show();
            }
        });

        layoutAboutUs.setOnClickListener(v -> {
            try {
                navController.navigate(R.id.action_settingsFragment_to_aboutUsFragment);
            } catch (Exception e) {
                Toast.makeText(requireContext(), "About Us screen is under development", Toast.LENGTH_SHORT).show();
            }
        });

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferencesHelper.setNotificationsEnabled(isChecked);
            Toast.makeText(requireContext(), "Notifications " + (isChecked ? "Enabled" : "Disabled"),
                    Toast.LENGTH_SHORT).show();
        });

        layoutPrivacyPolicy.setOnClickListener(v -> {
            // Open Privacy Policy URL
            String url = "https://www.example.com/privacypolicy"; // Replace with your actual URL
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });

        layoutTermsConditions.setOnClickListener(v -> {
            // Open Terms & Conditions URL
            String url = "https://www.example.com/terms"; // Replace with your actual URL
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });

        btnLogout.setOnClickListener(v -> new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    // Clear user session/preferences
                    preferencesHelper.clear(); // Assuming you have a clear method in PreferencesHelper
                    // Navigate to LoginActivity
                    Intent intent = new Intent(requireActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                }).setNegativeButton("Cancel", null)
                .show());
    }

    @Override
    public void onResume() {
        super.onResume();
        // Ensure dark mode switch is always in sync with the current theme
        View view = getView();
        if (view != null) {
            SwitchMaterial switchDarkMode = view.findViewById(R.id.switchDarkMode);
            if (switchDarkMode != null) {
                boolean isDark = preferencesHelper.isDarkModeEnabled();
                if (switchDarkMode.isChecked() != isDark) {
                    switchDarkMode.setChecked(isDark);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Clean up handler to prevent memory leaks
        if (themeHandler != null) {
            themeHandler.removeCallbacksAndMessages(null);
        }
    }
}
