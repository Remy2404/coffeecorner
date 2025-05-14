package com.coffeecorner.app.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.coffeecorner.app.fragments.OrderListFragment;
import com.coffeecorner.app.models.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryPagerAdapter extends FragmentStateAdapter {

    private List<Order> activeOrders = new ArrayList<>();
    private List<Order> completedOrders = new ArrayList<>();
    private List<Order> cancelledOrders = new ArrayList<>();
    
    private OrderListFragment activeOrdersFragment;
    private OrderListFragment completedOrdersFragment;
    private OrderListFragment cancelledOrdersFragment;

    public OrderHistoryPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
        
        // Initialize the fragments
        activeOrdersFragment = OrderListFragment.newInstance(activeOrders);
        completedOrdersFragment = OrderListFragment.newInstance(completedOrders);
        cancelledOrdersFragment = OrderListFragment.newInstance(cancelledOrders);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return activeOrdersFragment;
            case 1:
                return completedOrdersFragment;
            case 2:
                return cancelledOrdersFragment;
            default:
                return activeOrdersFragment;
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Active, Completed, Cancelled tabs
    }
    
    public void setOrders(List<Order> activeOrders, List<Order> completedOrders, List<Order> cancelledOrders) {
        this.activeOrders.clear();
        this.activeOrders.addAll(activeOrders);
        
        this.completedOrders.clear();
        this.completedOrders.addAll(completedOrders);
        
        this.cancelledOrders.clear();
        this.cancelledOrders.addAll(cancelledOrders);
        
        // Update the fragments
        activeOrdersFragment.updateOrders(activeOrders);
        completedOrdersFragment.updateOrders(completedOrders);
        cancelledOrdersFragment.updateOrders(cancelledOrders);
    }
}
