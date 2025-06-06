package com.coffeecorner.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coffeecorner.app.R;
import com.coffeecorner.app.adapters.NotificationAdapter;
import com.coffeecorner.app.models.Notification;
import com.coffeecorner.app.utils.PreferencesHelper;
// Temporarily commented out due to dependency conflicts
// import com.coffeecorner.app.utils.SupabaseClientManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

// Temporarily commented out due to dependency conflicts
// import io.github.jan.supabase.postgrest.Postgrest;
import com.coffeecorner.app.utils.Returning;

public class NotificationFragment extends Fragment {

    private RecyclerView recyclerNotifications;
    private LinearLayout emptyView;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList = new ArrayList<>();
    private PreferencesHelper preferencesHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        preferencesHelper = new PreferencesHelper(requireContext());

        // Initialize UI components
        recyclerNotifications = view.findViewById(R.id.recyclerNotifications);
        emptyView = view.findViewById(R.id.emptyView);

        // Set up the RecyclerView
        recyclerNotifications.setLayoutManager(new LinearLayoutManager(requireContext()));
        notificationAdapter = new NotificationAdapter(requireContext(), notificationList);
        recyclerNotifications.setAdapter(notificationAdapter);

        // Set up clear all button click listener
        view.findViewById(R.id.btnClearAll).setOnClickListener(v -> showClearConfirmationDialog());

        // Load notifications
        loadNotifications();
    }

    private void loadNotifications() {
        String userId = preferencesHelper.getUserId();
        if (userId == null || userId.isEmpty()) {
            updateEmptyState(true);
            return;
        }

        // Show loading state
        // You can add a progress indicator here if needed

        // Temporarily commented out due to dependency conflicts
        /*
         * // Get Supabase client and fetch notifications
         * SupabaseClientManager.getInstance().getClient()
         * .getSupabase()
         * .getPlugin(Postgrest.class)
         * .from("notifications")
         * .select()
         * .eq("user_id", userId)
         * .order("created_at", false) // Most recent first
         * .executeWithResponseHandlers(
         * response -> {
         * notificationList.clear();
         * notificationList.addAll(response.getDataList(Notification.class));
         * requireActivity().runOnUiThread(() -> {
         * notificationAdapter.notifyDataSetChanged();
         * updateEmptyState(notificationList.isEmpty());
         * });
         * },
         * throwable -> {
         * requireActivity().runOnUiThread(() -> {
         * updateEmptyState(true);
         * });
         * }
         * );
         */

        // TODO: Replace with actual notification loading implementation
        updateEmptyState(true);
    }

    private void updateEmptyState(boolean isEmpty) {
        if (isEmpty) {
            recyclerNotifications.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerNotifications.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    private void showClearConfirmationDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Clear Notifications")
                .setMessage("Are you sure you want to clear all notifications?")
                .setPositiveButton("Clear All", (dialog, which) -> clearAllNotifications())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void clearAllNotifications() {
        String userId = preferencesHelper.getUserId();
        if (userId == null || userId.isEmpty()) {
            return;
        }

        // Temporarily commented out due to dependency conflicts
        /*
         * // Delete all notifications for the current user
         * SupabaseClientManager.getInstance().getClient()
         * .getSupabase()
         * .getPlugin(Postgrest.class)
         * .from("notifications")
         * .delete(Returning.REPRESENTATION)
         * .eq("user_id", userId)
         * .executeWithResponseHandlers(
         * response -> {
         * requireActivity().runOnUiThread(() -> {
         * notificationList.clear();
         * notificationAdapter.notifyDataSetChanged();
         * updateEmptyState(true);
         * });
         * },
         * throwable -> {
         * // Handle error
         * }
         * );
         */

        // TODO: Replace with actual notification clearing implementation
        notificationList.clear();
        notificationAdapter.notifyDataSetChanged();
        updateEmptyState(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh notifications when fragment resumes
        loadNotifications();
    }
}
