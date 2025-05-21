package com.coffeecorner.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.coffeecorner.app.R;
import com.coffeecorner.app.adapters.MenuItemAdapter;
import com.coffeecorner.app.models.MenuItem;
import com.coffeecorner.app.viewmodels.MenuViewModel;
import com.google.android.material.tabs.TabLayout;

public class MenuFragment extends Fragment implements MenuItemAdapter.OnItemClickListener {

    private TabLayout tabLayoutMenuCategories;
    private MenuViewModel menuViewModel;

    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        // Initialize views and setup listeners
        tabLayoutMenuCategories = view.findViewById(R.id.tabLayoutMenuCategories);

        menuViewModel = new ViewModelProvider(this).get(MenuViewModel.class);

        setupTabLayout();
        setupRecyclerView(view);
        setupObservers();

        return view;
    }

    private void setupTabLayout() {
        // Observe categories from ViewModel and populate tabs
        menuViewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            tabLayoutMenuCategories.removeAllTabs(); // Clear existing tabs
            tabLayoutMenuCategories.addTab(tabLayoutMenuCategories.newTab().setText("All")); // Add an "All" tab
            if (categories != null) {
                for (String category : categories) {
                    tabLayoutMenuCategories.addTab(tabLayoutMenuCategories.newTab().setText(category));
                }
            }
        });

        // Set up tab selection listener
        tabLayoutMenuCategories.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // TODO: Filter menu items based on selected category
                // For now, just show a toast
                if (tab.getText() != null) {
                    menuViewModel.selectCategory(tab.getText().toString());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Do nothing
            }
        });
    }

    private void setupRecyclerView(View view) {
        RecyclerView rvMenuItems = view.findViewById(R.id.rvMenuItems);
        rvMenuItems.setLayoutManager(new LinearLayoutManager(getContext()));
        MenuItemAdapter adapter = new MenuItemAdapter();
        adapter.setOnItemClickListener(this);
        rvMenuItems.setAdapter(adapter);
        // The adapter will be updated via the observer in setupObservers
    }

    private void setupObservers() {
        // Observe filtered menu items from ViewModel
        menuViewModel.getFilteredMenuItems().observe(getViewLifecycleOwner(), menuItems -> {
            MenuItemAdapter adapter = (MenuItemAdapter) ((RecyclerView) requireView().findViewById(R.id.rvMenuItems)).getAdapter();
            if (adapter != null && menuItems != null) {
                adapter.submitList(menuItems);
            }
        });
    }

    @Override
    public void onAddToCartClick(MenuItem menuItem) {
        // TODO: Implement add to cart logic here
        Toast.makeText(getContext(), "Add to cart clicked for: " + menuItem.getName(), Toast.LENGTH_SHORT).show();
    }
}
