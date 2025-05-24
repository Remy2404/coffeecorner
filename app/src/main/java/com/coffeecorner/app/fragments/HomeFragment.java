package com.coffeecorner.app.fragments;

import android.os.Bundle;
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
import com.google.android.material.tabs.TabLayout;

import com.coffeecorner.app.R;
import com.coffeecorner.app.adapters.ProductAdapter;
import com.coffeecorner.app.utils.GridSpacingItemDecoration;
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
            // TODO: Implement location picker with Google Maps API
            Toast.makeText(requireContext(), "Location picker coming soon", Toast.LENGTH_SHORT).show();
        });

        btnFilter.setOnClickListener(v -> {
            // TODO: Implement advanced filtering options
            Toast.makeText(requireContext(), "Filtering options coming soon", Toast.LENGTH_SHORT).show();
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
            Navigation.findNavController(requireView()).navigate(R.id.action_homeFragment_to_productDetailsFragment, args);
        });

        productAdapter.setOnAddToCartClickListener((product, position) -> {
            // Add to cart using CartViewModel
            cartViewModel.addToCart(product, 1);
            Toast.makeText(requireContext(), product.getName() + " added to cart", Toast.LENGTH_SHORT).show();
            // Navigate to cart after adding item
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
}
