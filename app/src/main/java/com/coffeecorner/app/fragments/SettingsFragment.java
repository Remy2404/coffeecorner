package com.coffeecorner.app.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

public class SettingsFragment extends Fragment {

    private NavController navController;
    private PreferencesHelper preferencesHelper;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        preferencesHelper = new PreferencesHelper(requireContext());

        // Set up the back button
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        }

        // Initialize UI elements
        RelativeLayout layoutEditProfile = view.findViewById(R.id.layoutEditProfile);
        SwitchMaterial switchNotifications = view.findViewById(R.id.switchNotifications);
        SwitchMaterial switchDarkMode = view.findViewById(R.id.switchDarkMode);
        RelativeLayout layoutPrivacyPolicy = view.findViewById(R.id.layoutPrivacyPolicy);
        RelativeLayout layoutTermsConditions = view.findViewById(R.id.layoutTermsConditions);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        // Set initial states for switches
        switchNotifications.setChecked(preferencesHelper.isNotificationsEnabled());
        switchDarkMode.setChecked(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);

        // Set up click listeners
        layoutEditProfile.setOnClickListener(v -> {
            // Navigate to EditProfileFragment - Ensure this action exists in your nav_graph
            if (navController.getCurrentDestination().getId() == R.id.settingsFragment) {
                navController.navigate(R.id.action_settingsFragment_to_editProfileFragment);
            }
        });

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferencesHelper.setNotificationsEnabled(isChecked);
            Toast.makeText(requireContext(), "Notifications " + (isChecked ? "Enabled" : "Disabled"),
                    Toast.LENGTH_SHORT).show();
        });

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            // It's good practice to recreate the activity to apply the theme change
            // immediately
            // However, for a fragment, you might need to signal the activity or handle it
            // differently
            // For simplicity, a Toast message is shown here.
            Toast.makeText(requireContext(),
                    "Dark Mode " + (isChecked ? "Enabled" : "Disabled") + ". Restart app to see full changes.",
                    Toast.LENGTH_LONG).show();
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

        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
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
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }
}
