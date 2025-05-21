package com.coffeecorner.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.coffeecorner.app.R;
import com.coffeecorner.app.adapters.MenuItemAdapter;
import com.coffeecorner.app.models.MenuItem;
import com.coffeecorner.app.repositories.MenuRepository;
import com.coffeecorner.app.viewmodel.MenuViewModel;
import com.coffeecorner.app.viewmodel.ViewModelFactory;
import com.google.android.material.tabs.TabLayout;

public class MenuFragment extends Fragment implements MenuItemAdapter.OnItemClickListener {

    private TabLayout tabLayoutMenuCategories;
    private MenuViewModel menuViewModel;
    private RecyclerView recyclerViewMenu;
    private MenuItemAdapter menuAdapter;
    private Toolbar toolbar;
    private TextView tvToolbarTitle;
    private MenuItem searchMenuItem;

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
                if (tab.getText() != null) {
                    String category = tab.getText().toString();
                    menuViewModel.loadProductsByCategory(category);
                    updateTabUI(tab.getPosition());
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
            MenuItemAdapter adapter = (MenuItemAdapter) ((RecyclerView) requireView().findViewById(R.id.rvMenuItems))
                    .getAdapter();
            if (adapter != null && menuItems != null) {
                adapter.submitList(menuItems);
            }
        });
    }

    /**
     * Update UI elements based on selected tab
     * 
     * @param position Position of the selected tab
     */
    private void updateTabUI(int position) {
        // You can customize the UI based on selected tab
        // For example, change indicator color, text appearance, etc.

        // Clear any existing search filters
        if (searchMenuItem != null && searchMenuItem.isActionViewExpanded()) {
            searchMenuItem.collapseActionView();
        }

        // Update title based on selected category
        String title = position == 0 ? "All Menu" : tabLayoutMenuCategories.getTabAt(position).getText().toString();
        if (tvToolbarTitle != null) {
            tvToolbarTitle.setText(title);
        }
    }

    @Override
    public void onAddToCartClick(MenuItem menuItem) {
        // Convert MenuItem to Product for CartViewModel
        com.coffeecorner.app.models.Product product = new com.coffeecorner.app.models.Product(
                menuItem.getId(),
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice(),
                menuItem.getCategory(),
                menuItem.getImageUrl());

        // Get the CartViewModel
        com.coffeecorner.app.repositories.CartRepository cartRepository = com.coffeecorner.app.repositories.CartRepository
                .getInstance(requireContext());
        com.coffeecorner.app.viewmodel.CartViewModel cartViewModel = new ViewModelProvider(requireActivity(),
                new ViewModelFactory(cartRepository)).get(com.coffeecorner.app.viewmodel.CartViewModel.class);

        // Add to cart with default quantity of 1 and no special instructions
        cartViewModel.addToCart(product, 1, "");

        // Show success message
        Toast.makeText(getContext(), menuItem.getName() + " added to cart", Toast.LENGTH_SHORT).show();
    }
}
