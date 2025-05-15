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
import com.coffeecorner.app.adapters.OrderAdapter;
import com.coffeecorner.app.models.Order;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OrderListFragment extends Fragment {

    private static final String ARG_ORDERS = "orders";

    private RecyclerView recyclerOrders;
    private TextView tvNoOrders;
    private OrderAdapter orderAdapter;
    private List<Order> orders = new ArrayList<>();

    public OrderListFragment() {
        // Required empty public constructor
    }

    /**
     * Create a new instance of OrderListFragment with a list of orders.
     * 
     * @param orders The list of orders to display
     * @return A new instance of OrderListFragment
     */
    public static OrderListFragment newInstance(List<Order> orders) {
        OrderListFragment fragment = new OrderListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ORDERS, new ArrayList<>(orders));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        recyclerOrders = view.findViewById(R.id.recyclerOrders);
        tvNoOrders = view.findViewById(R.id.tvNoOrders);

        // Set up RecyclerView
        recyclerOrders.setLayoutManager(new LinearLayoutManager(requireContext()));
        orderAdapter = new OrderAdapter(requireContext(), orders);
        recyclerOrders.setAdapter(orderAdapter);
        // Get the orders from arguments
        if (getArguments() != null) {
            // Using type-safe approach to avoid unchecked cast warning
            Serializable serializable = getArguments().getSerializable(ARG_ORDERS);
            if (serializable instanceof ArrayList<?>) {
                ArrayList<?> list = (ArrayList<?>) serializable;
                if (!list.isEmpty() && list.get(0) instanceof Order) {
                    orders.clear();
                    for (Object o : list) {
                        orders.add((Order) o);
                    }
                    updateOrdersList();
                }
            }
        }
    }

    /**
     * Update the orders displayed in this fragment.
     * 
     * @param newOrders The new list of orders to display
     */
    public void updateOrders(List<Order> newOrders) {
        orders.clear();
        orders.addAll(newOrders);
        updateOrdersList();
    }

    private void updateOrdersList() {
        if (orderAdapter != null) {
            orderAdapter.notifyDataSetChanged();
        }

        // Show/hide no orders message
        if (orders.isEmpty()) {
            tvNoOrders.setVisibility(View.VISIBLE);
            recyclerOrders.setVisibility(View.GONE);
        } else {
            tvNoOrders.setVisibility(View.GONE);
            recyclerOrders.setVisibility(View.VISIBLE);
        }
    }
}
