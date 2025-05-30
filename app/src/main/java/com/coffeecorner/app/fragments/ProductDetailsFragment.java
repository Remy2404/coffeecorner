package com.coffeecorner.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.coffeecorner.app.R;
import com.coffeecorner.app.models.CartItem;
import com.coffeecorner.app.models.Product;
import com.coffeecorner.app.viewmodels.CartViewModel;
import com.coffeecorner.app.network.ApiService;
import com.coffeecorner.app.network.RetrofitClient;
import com.coffeecorner.app.network.ApiResponse;
import com.coffeecorner.app.models.ProductResponse;
import java.util.Arrays;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.NumberFormat;
import java.util.Locale;

public class ProductDetailsFragment extends Fragment {

    private ImageView imgProduct;
    private TextView tvProductName, tvDescription, tvPrice, tvQuantity, tvCalories;
    private ImageButton btnBack, btnFavorite, btnDecrease, btnIncrease;
    private RadioGroup rgSize, rgTemperature;
    private ChipGroup chipGroupExtras;
    private Button btnAddToCart;

    // UI elements for size selection
    private Chip chipSizeS, chipSizeM, chipSizeL;
    private CollapsingToolbarLayout collapsingToolbar;
    private Product product;
    private int quantity = 1;
    private boolean isFavorite = false;
    private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
    private CartViewModel cartViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize CartViewModel
        cartViewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
                .get(CartViewModel.class);

        // Initialize views
        initViews(view);
        // Setup chip change listeners to update price dynamically
        setChipListeners();

        // Set click listeners
        setClickListeners();

        // Get product data from arguments
        if (getArguments() != null) {
            String productId = getArguments().getString("productId");
            if (productId != null) {
                loadProduct(productId);
            }
        }
    }

    private void initViews(View view) {
        // Using the IDs from the fragment_product_details.xml
        imgProduct = view.findViewById(R.id.ivProductImageDetail);
        tvProductName = view.findViewById(R.id.tvProductNameDetail);
        tvDescription = view.findViewById(R.id.tvProductDescription);
        tvPrice = view.findViewById(R.id.tvProductPriceDetail);
        tvQuantity = view.findViewById(R.id.tvQuantity);
        tvCalories = view.findViewById(R.id.tvCalories);

        btnBack = view.findViewById(R.id.btnBack);
        btnFavorite = view.findViewById(R.id.btnFavorite);
        btnDecrease = view.findViewById(R.id.btnDecrease);
        btnIncrease = view.findViewById(R.id.btnIncrease);
        btnAddToCart = view.findViewById(R.id.btnAddToCart);

        // Get size chips instead of radio buttons
        chipSizeS = view.findViewById(R.id.chipSizeS);
        chipSizeM = view.findViewById(R.id.chipSizeM);
        chipSizeL = view.findViewById(R.id.chipSizeL);

        // Initialize chipGroupExtras if it exists in the layout
        View extrasGroup = view.findViewById(R.id.chipGroupExtras);
        if (extrasGroup != null && extrasGroup instanceof ChipGroup) {
            chipGroupExtras = (ChipGroup) extrasGroup;
        }

        // Set initial quantity
        if (tvQuantity != null) {
            tvQuantity.setText(String.valueOf(quantity));
        }
        // Default size selection
        if (chipSizeS != null) {
            chipSizeS.setChecked(true);
        } // Configure collapsing toolbar
        collapsingToolbar = view.findViewById(R.id.collapsingToolbarProductDetails);
        if (collapsingToolbar != null) {
            collapsingToolbar.setExpandedTitleColor(
                    androidx.core.content.ContextCompat.getColor(requireContext(), android.R.color.transparent));
        }
    }

    private void setClickListeners() {
        btnBack.setOnClickListener(v -> {
            // Navigate back
            Navigation.findNavController(requireView()).popBackStack();
        });

        btnFavorite.setOnClickListener(v -> {
            // Toggle favorite status
            isFavorite = !isFavorite;
            updateFavoriteUI();
            toggleFavoriteStatus();
        });

        btnDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
                updateTotalPrice();
            }
        });

        btnIncrease.setOnClickListener(v -> {
            if (quantity < 10) { // Set a reasonable maximum
                quantity++;
                tvQuantity.setText(String.valueOf(quantity));
                updateTotalPrice();
            }
        });

        btnAddToCart.setOnClickListener(v -> {
            addToCart();
        });
    }

    /**
     * Attach listeners to chips to recalculate price when selection changes.
     */
    private void setChipListeners() {
        chipSizeS.setOnCheckedChangeListener((button, isChecked) -> updateTotalPrice());
        chipSizeM.setOnCheckedChangeListener((button, isChecked) -> updateTotalPrice());
        chipSizeL.setOnCheckedChangeListener((button, isChecked) -> updateTotalPrice());
        if (chipGroupExtras != null) {
            for (int i = 0; i < chipGroupExtras.getChildCount(); i++) {
                Chip chip = (Chip) chipGroupExtras.getChildAt(i);
                chip.setOnCheckedChangeListener((button, isChecked) -> updateTotalPrice());
            }
        }
    }

    private void loadProduct(String productId) {
        ApiService api = RetrofitClient.getApi();
        api.getProductById(productId).enqueue(new Callback<ApiResponse<Product>>() {
            @Override
            public void onResponse(Call<ApiResponse<Product>> call,
                    Response<ApiResponse<Product>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    product = response.body().getData();
                    if (product.getCalories() == 0) {
                        product.setCalories(150); // Default calories if not set
                    }
                    product.setAvailableSizes(Arrays.asList("Small", "Medium", "Large"));
                    product.setAvailableAddons(Arrays.asList("Extra Shot", "Whipped Cream", "Caramel"));
                    requireActivity().runOnUiThread(() -> {
                        displayProduct();
                        checkFavoriteStatus(productId);
                    });
                } else {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Failed to load product", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(requireView()).popBackStack();
                    });
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Product>> call, Throwable t) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Error loading product", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).popBackStack();
                });
            }
        });
    }

    private void displayProduct() {
        if (product == null)
            return;

        // Set product details
        tvProductName.setText(product.getName());
        tvDescription.setText(product.getDescription());
        tvPrice.setText(currencyFormatter.format(product.getPrice()));

        if (product.getCalories() > 0) {
            tvCalories.setText(product.getCalories() + " cal");
            tvCalories.setVisibility(View.VISIBLE);
        } else {
            tvCalories.setVisibility(View.GONE);
        }

        // Load product image
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.coffee_placeholder)
                    .error(R.drawable.coffee_placeholder)
                    .into(imgProduct);
        } else {
            imgProduct.setImageResource(R.drawable.coffee_placeholder);
        }

        // Update button text with initial price
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        if (product == null)
            return;

        double basePrice = product.getPrice();
        double sizeExtra = 0;

        // Add size price based on selected chip
        if (chipSizeM != null && chipSizeM.isChecked()) {
            sizeExtra = 0.5; // $0.50 extra for medium
        } else if (chipSizeL != null && chipSizeL.isChecked()) {
            sizeExtra = 1.0; // $1.00 extra for large
        }

        // Add extras price
        double extrasTotal = 0;
        if (chipGroupExtras != null) {
            for (int i = 0; i < chipGroupExtras.getChildCount(); i++) {
                Chip chip = (Chip) chipGroupExtras.getChildAt(i);
                if (chip.isChecked()) {
                    // Each extra costs $0.50
                    extrasTotal += 0.5;
                }
            }
        }

        // Calculate total
        double itemTotal = (basePrice + sizeExtra + extrasTotal) * quantity;

        // Update button text
        btnAddToCart.setText("Add to Cart - " + currencyFormatter.format(itemTotal));
    }

    private void addToCart() {
        if (product == null) {
            Toast.makeText(requireContext(), "Product information not available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add to cart using CartViewModel
        cartViewModel.addToCart(product, quantity);

        // Show success message
        Toast.makeText(requireContext(), getString(R.string.added_to_cart), Toast.LENGTH_SHORT).show();

        // Navigate back safely
        try {
            if (isAdded() && getView() != null) {
                Navigation.findNavController(requireView()).popBackStack();
            }
        } catch (Exception e) {
            android.util.Log.e("ProductDetailsFragment", "Navigation error: " + e.getMessage());
            // Fallback: try to pop the backstack
            if (isAdded()) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        }
    }

    private void checkFavoriteStatus(String productId) {
        // In a real app, check if the product is in the user's favorites
        // For simplicity, we'll just set it to false here
        isFavorite = false;
        updateFavoriteUI();
    }

    private void updateFavoriteUI() {
        btnFavorite.setImageResource(isFavorite ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_outline);
    }

    private void toggleFavoriteStatus() {
        // In a real app, update the favorite status in the database
        // For simplicity, we'll just show a toast
        String message = isFavorite ? "Added to favorites" : "Removed from favorites";
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
