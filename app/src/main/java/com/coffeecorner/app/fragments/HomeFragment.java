package com.coffeecorner.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;
import com.coffeecorner.app.R;
import com.coffeecorner.app.adapters.ProductAdapter;
import com.coffeecorner.app.models.Product;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private TextView tvLocationValue;
    private ImageView ivLocationDropdown;
    private EditText etSearch;
    private ImageButton btnFilter;
    private MaterialCardView cvPromo;
    private TabLayout tabLayoutCategories;
    private RecyclerView rvProducts;
    private ProductAdapter productAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initializeViews(view);
        setupListeners();
        setupRecyclerView();
        loadProducts();
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
            // TODO: Show location picker dialog
        });

        btnFilter.setOnClickListener(v -> {
            // TODO: Show filter dialog
        });

        tabLayoutCategories.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // TODO: Filter products by selected category
                filterProductsByCategory(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(requireContext(), new ArrayList<>());
        rvProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvProducts.setAdapter(productAdapter);
    }

    private void loadProducts() {
        // TODO: Load products from Supabase
        // For now, we'll use dummy data
        List<Product> products = new ArrayList<>();
        // Add sample products
        productAdapter.updateProducts(products);
    }

    private void filterProductsByCategory(int categoryPosition) {
        // TODO: Implement category filtering
    }
}
