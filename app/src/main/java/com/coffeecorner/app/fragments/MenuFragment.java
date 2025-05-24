package com.coffeecorner.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.coffeecorner.app.R;
import com.coffeecorner.app.adapters.MenuItemAdapter;
import com.coffeecorner.app.models.Product;
import com.coffeecorner.app.viewmodels.CartViewModel;
import com.coffeecorner.app.viewmodels.ProductViewModel;
import com.google.android.material.tabs.TabLayout;

public class MenuFragment extends Fragment implements MenuItemAdapter.OnItemClickListener {

    private TabLayout tabLayoutMenuCategories;
    private ProductViewModel productViewModel;
    private CartViewModel cartViewModel;
    private MenuItemAdapter adapter;
    private TextView tvToolbarTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        // Initialize ViewModels
        productViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);
        cartViewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
                .get(CartViewModel.class);

        initializeViews(view);
        setupTabLayout();
        setupRecyclerView(view);
        setupObservers();

        return view;
    }

    private void initializeViews(View view) {
        tabLayoutMenuCategories = view.findViewById(R.id.tabLayoutMenuCategories);
    }

    private void setupTabLayout() {
        productViewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            tabLayoutMenuCategories.removeAllTabs();
            tabLayoutMenuCategories.addTab(tabLayoutMenuCategories.newTab().setText("All"));
            if (categories != null) {
                for (String category : categories) {
                    tabLayoutMenuCategories.addTab(tabLayoutMenuCategories.newTab().setText(category));
                }
            }
        });

        tabLayoutMenuCategories.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText() != null) {
                    String category = tab.getText().toString();
                    productViewModel.filterByCategory(category);
                    updateTabUI(tab.getPosition());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Not needed
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Not needed
            }
        });
    }

    private void setupRecyclerView(View view) {
        RecyclerView rvMenuItems = view.findViewById(R.id.rvMenuItems);
        rvMenuItems.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MenuItemAdapter();
        adapter.setOnItemClickListener(this);
        rvMenuItems.setAdapter(adapter);
    }

    private void setupObservers() {
        productViewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            if (adapter != null && products != null) {
                adapter.submitList(products);
            }
        });
        productViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                if (isLoading) {
                    showLoadingIndicator();
                } else {
                    hideLoadingIndicator();
                }
            }
        });

        productViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
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

        // Update title based on selected category
        String title = position == 0 ? "All Menu" : tabLayoutMenuCategories.getTabAt(position).getText().toString();
        if (tvToolbarTitle != null) {
            tvToolbarTitle.setText(title);
        }
    }

    @Override
    public void onAddToCartClick(Product product) {
        cartViewModel.addToCart(product, 1);
        Toast.makeText(requireContext(), product.getName() + " added to cart", Toast.LENGTH_SHORT).show();
    }

    private void showLoadingIndicator() {
        // Show loading progress bar or shimmer effect
        // You can add a ProgressBar to the layout and show it here
        // For now, we'll use a simple approach
        if (getActivity() != null) {
            // If there's a progress bar in the layout, show it
            View progressBar = getActivity().findViewById(R.id.progressBar);
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }
    }

    private void hideLoadingIndicator() {
        // Hide loading progress bar
        if (getActivity() != null) {
            View progressBar = getActivity().findViewById(R.id.progressBar);
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
        }
    }
}
