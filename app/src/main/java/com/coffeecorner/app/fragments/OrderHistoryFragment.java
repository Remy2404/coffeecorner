package com.coffeecorner.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.coffeecorner.app.R;
import com.coffeecorner.app.adapters.OrderHistoryPagerAdapter;
import com.coffeecorner.app.models.Order;
import com.coffeecorner.app.viewmodels.OrderViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryFragment extends Fragment {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private LinearLayout emptyView;
    private Button btnOrderNow;
    private ImageView btnBack;
    private OrderHistoryPagerAdapter pagerAdapter;
    private OrderViewModel orderViewModel;

    private List<Order> activeOrders = new ArrayList<>();
    private List<Order> completedOrders = new ArrayList<>();
    private List<Order> cancelledOrders = new ArrayList<>();

    public OrderHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        orderViewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class); // Initialize views
        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);
        emptyView = view.findViewById(R.id.emptyView);
        btnOrderNow = view.findViewById(R.id.btnOrderNow);
        btnBack = view.findViewById(R.id.btnBack);

        // Set up ViewPager
        pagerAdapter = new OrderHistoryPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Connect TabLayout with ViewPager
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Active");
                    break;
                case 1:
                    tab.setText("Completed");
                    break;
                case 2:
                    tab.setText("Cancelled");
                    break;
            }
        }).attach(); // Set button click listener
        btnOrderNow.setOnClickListener(v -> {
            // Navigate to menu
            Navigation.findNavController(view).navigate(R.id.action_to_menu);
        });

        // Set back button click listener
        btnBack.setOnClickListener(v -> {
            // Navigate back to previous fragment
            Navigation.findNavController(view).navigateUp();
        });

        // Setup observers
        setupObservers();

        // Load orders
        refreshOrders();
    }

    /**
     * Refresh orders from the repository
     */
    public void refreshOrders() {
        if (orderViewModel != null) {
            orderViewModel.loadOrders();
        }
    }

    private void setupObservers() {
        // Observe active orders
        orderViewModel.getActiveOrders().observe(getViewLifecycleOwner(), orders -> {
            activeOrders = orders;
            updateOrdersDisplay();
        });

        // Observe completed orders
        orderViewModel.getCompletedOrders().observe(getViewLifecycleOwner(), orders -> {
            completedOrders = orders;
            updateOrdersDisplay();
        });

        // Observe cancelled orders
        orderViewModel.getCancelledOrders().observe(getViewLifecycleOwner(), orders -> {
            cancelledOrders = orders;
            updateOrdersDisplay();
        });

        // Observe loading state
        orderViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            // Toggle loading indicator if needed
        });

        // Observe error messages
        orderViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                // Show error message
            }
        });
    }

    private void updateOrdersDisplay() {
        // Update pager adapter data
        pagerAdapter.setOrders(activeOrders, completedOrders, cancelledOrders);

        // Show empty view if no orders
        if (activeOrders.isEmpty() && completedOrders.isEmpty() && cancelledOrders.isEmpty()) {
            showEmptyView();
        } else {
            hideEmptyView();
        }
    }

    private void showEmptyView() {
        emptyView.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
    }

    private void hideEmptyView() {
        emptyView.setVisibility(View.GONE);
        viewPager.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh orders when fragment resumes
        orderViewModel.refreshOrders();
    }
}
