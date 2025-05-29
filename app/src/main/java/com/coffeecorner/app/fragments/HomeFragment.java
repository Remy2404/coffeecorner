package com.coffeecorner.app.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;

import com.coffeecorner.app.models.CartItem;
import com.coffeecorner.app.repositories.CartRepository;
import java.util.List;

import com.coffeecorner.app.R;
import com.coffeecorner.app.adapters.ProductAdapter;
import com.coffeecorner.app.utils.GridSpacingItemDecoration;
import com.coffeecorner.app.utils.PreferencesHelper;
import com.coffeecorner.app.viewmodels.CartViewModel;
import com.coffeecorner.app.viewmodels.ProductViewModel;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private TextView tvLocationValue;
    private ImageView ivLocationDropdown;
    private EditText etSearch;
    private ImageButton btnFilter;
    private MaterialCardView cvPromo;
    private TabLayout tabLayoutCategories;
    private RecyclerView rvProducts;
    private ProductAdapter productAdapter;
    private ProductViewModel productViewModel;
    private CartViewModel cartViewModel; // Add CartViewModel field

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize ViewModels
        productViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);
        cartViewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
                .get(CartViewModel.class);

        initializeViews(view);
        setupListeners();
        setupRecyclerView();
        setupObservers();

        // Load data from ViewModel
        productViewModel.loadProducts();

        return view;
    }

    private void initializeViews(View view) {
        tvLocationValue = view.findViewById(R.id.tvLocationValue);
        ivLocationDropdown = view.findViewById(R.id.ivLocationDropdown);
        etSearch = view.findViewById(R.id.etSearch);
        btnFilter = view.findViewById(R.id.btnFilter);
        cvPromo = view.findViewById(R.id.cvPromo);
        tabLayoutCategories = view.findViewById(R.id.tabLayoutCategories);
        rvProducts = view.findViewById(R.id.rvProducts);
    }

    private void setupListeners() {
        tvLocationValue.setOnClickListener(v -> {
            showLocationPicker();
        });
        btnFilter.setOnClickListener(v -> {
            showFilterDialog();
        });

        tabLayoutCategories.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String category = tab.getText().toString();
                filterProductsByCategory(category);
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

        // Set up search functionality
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            String query = etSearch.getText().toString().trim();
            if (!query.isEmpty()) {
                productViewModel.searchProducts(query);
                return true;
            }
            return false;
        });
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(requireContext(), new ArrayList<>());

        // Set up grid layout with 2 columns and proper spacing
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        rvProducts.setLayoutManager(layoutManager);

        // Set up click listeners
        productAdapter.setOnProductClickListener((product, position) -> {
            // Navigate to product details
            Bundle args = new Bundle();
            args.putString("productId", product.getId());
            Navigation.findNavController(requireView()).navigate(R.id.action_homeFragment_to_productDetailsFragment,
                    args);
        });
        productAdapter.setOnAddToCartClickListener((product, position) -> {
            cartViewModel.addToCart(product, 1);
            Toast.makeText(requireContext(), product.getName() + " added to cart", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(requireView()).navigate(R.id.action_to_cart);
        });

        rvProducts.setAdapter(productAdapter);

        // Add item decoration for spacing if needed
        int spacing = getResources().getDimensionPixelSize(R.dimen.grid_spacing);
        rvProducts.addItemDecoration(new GridSpacingItemDecoration(2, spacing, true));
    }

    private void setupObservers() {
        // Observe product list changes
        productViewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            productAdapter.updateProducts(products);
        });

        // Observe categories for tab creation
        productViewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            tabLayoutCategories.removeAllTabs();
            for (String category : categories) {
                tabLayoutCategories.addTab(tabLayoutCategories.newTab().setText(category));
            }
        });

        // Observe loading state
        productViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            // Toggle loading indicator if needed
        });

        // Observe error messages
        productViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void filterProductsByCategory(String category) {
        productViewModel.filterByCategory(category);
    }

    private void showLocationPicker() {
        String[] locations = {
                "Downtown Coffee Corner",
                "Mall Branch",
                "University Campus",
                "Airport Terminal",
                "Business District"
        };

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Select Location")
                .setItems(locations, (dialog, which) -> {
                    String selectedLocation = locations[which];
                    tvLocationValue.setText(selectedLocation);

                    // Save selected location to preferences
                    PreferencesHelper preferencesHelper = new PreferencesHelper(requireContext());
                    preferencesHelper.saveSelectedLocation(selectedLocation);

                    Toast.makeText(requireContext(), "Location set to: " + selectedLocation, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showFilterDialog() {
        String[] filterOptions = {
                "Price: Low to High",
                "Price: High to Low",
                "Most Popular",
                "Newest First",
                "Highest Rated",
                "Available Only"
        };

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Filter Products")
                .setItems(filterOptions, (dialog, which) -> {
                    String selectedFilter = filterOptions[which];
                    applyFilter(selectedFilter);
                    Toast.makeText(requireContext(), "Filter applied: " + selectedFilter, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void applyFilter(String filterType) {
        switch (filterType) {
            case "Price: Low to High":
                productViewModel.sortByPriceAscending();
                break;
            case "Price: High to Low":
                productViewModel.sortByPriceDescending();
                break;
            case "Most Popular":
                productViewModel.sortByPopularity();
                break;
            case "Newest First":
                productViewModel.sortByNewest();
                break;
            case "Highest Rated":
                productViewModel.sortByRating();
                break;
            case "Available Only":
                productViewModel.filterAvailableOnly();
                break;
        }
    }
}
