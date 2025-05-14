package com.coffeecorner.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.coffeecorner.app.R;
import com.coffeecorner.app.adapters.OrderHistoryPagerAdapter;
import com.coffeecorner.app.models.Order;
import com.coffeecorner.app.utils.PreferencesHelper;
import com.coffeecorner.app.utils.SupabaseClientManager;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import io.github.jan.supabase.postgrest.Postgrest;

public class OrderHistoryFragment extends Fragment {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private LinearLayout emptyView;
    private Button btnOrderNow;
    private OrderHistoryPagerAdapter pagerAdapter;
    private PreferencesHelper preferencesHelper;

    private List<Order> activeOrders = new ArrayList<>();
    private List<Order> completedOrders = new ArrayList<>();
    private List<Order> cancelledOrders = new ArrayList<>();

    public OrderHistoryFragment() {
        // Required empty public constructor
    }    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_history, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        preferencesHelper = new PreferencesHelper(requireContext());
        
        // Initialize views
        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);
        emptyView = view.findViewById(R.id.emptyView);
        btnOrderNow = view.findViewById(R.id.btnOrderNow);
        
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
        }).attach();
        
        // Set button click listener
        btnOrderNow.setOnClickListener(v -> {
            // Navigate to menu
            Navigation.findNavController(view).navigate(R.id.action_to_menu);
        });
        
        // Load orders
        loadOrders();
    }
    
    private void loadOrders() {
        String userId = preferencesHelper.getUserId();
        if (userId == null || userId.isEmpty()) {
            showEmptyView();
            return;
        }
        
        // Get Supabase client and fetch orders
        SupabaseClientManager.getInstance().getClient()
            .getSupabase()
            .getPlugin(Postgrest.class)
            .from("orders")
            .select()
            .eq("user_id", userId)
            .order("created_at", false) // Most recent first
            .execute(response -> {
                List<Order> orders = response.getData(Order.class);
                
                // Clear existing lists
                activeOrders.clear();
                completedOrders.clear();
                cancelledOrders.clear();
                
                // Categorize orders
                for (Order order : orders) {
                    if (order.getStatus().equals(Order.STATUS_CONFIRMED) || 
                        order.getStatus().equals(Order.STATUS_PREPARING) || 
                        order.getStatus().equals(Order.STATUS_READY) || 
                        order.getStatus().equals(Order.STATUS_DELIVERING)) {
                        activeOrders.add(order);
                    } else if (order.getStatus().equals(Order.STATUS_DELIVERED) || 
                               order.getStatus().equals(Order.STATUS_COMPLETED)) {
                        completedOrders.add(order);
                    } else if (order.getStatus().equals(Order.STATUS_CANCELLED)) {
                        cancelledOrders.add(order);
                    }
                }
                
                // Update UI on main thread
                requireActivity().runOnUiThread(() -> {
                    if (activeOrders.isEmpty() && completedOrders.isEmpty() && cancelledOrders.isEmpty()) {
                        showEmptyView();
                    } else {
                        showOrdersView();
                        pagerAdapter.setOrders(activeOrders, completedOrders, cancelledOrders);
                    }
                });
                
                return null;
            }, error -> {
                // Handle error on main thread
                requireActivity().runOnUiThread(this::showEmptyView);
                return null;
            });
    }
    
    private void showEmptyView() {
        emptyView.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
    }
    
    private void showOrdersView() {
        emptyView.setVisibility(View.GONE);
        viewPager.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.VISIBLE);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Refresh orders when fragment resumes
        loadOrders();
    }
}
