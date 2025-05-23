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
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.coffeecorner.app.R;
import com.coffeecorner.app.models.Product;
import com.coffeecorner.app.utils.CartManager;
import com.coffeecorner.app.utils.SupabaseClientManager;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.NumberFormat;
import java.util.Locale;

import io.github.jan.supabase.postgrest.Postgrest;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        initViews(view);

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

        // Configure collapsing toolbar
        collapsingToolbar = view.findViewById(R.id.collapsingToolbarProductDetails);
        if (collapsingToolbar != null) {
            collapsingToolbar.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
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

    private void loadProduct(String productId) {
        SupabaseClientManager.getInstance().getClient()
                .getSupabase()
                .getPlugin(Postgrest.class)
                .from("products")
                .select()
                .eq("id", productId)
                .single()
                .executeWithResponseHandlers(
                    response -> {
                        product = response.getData(Product.class);
                        requireActivity().runOnUiThread(() -> {
                            displayProduct();
                        });
                        checkFavoriteStatus(productId);
                    },
                    throwable -> {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "Failed to load product", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(requireView()).popBackStack();
                        });
                    }
                );

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
        if (product == null)
            return;

        // Get selected size
        String size = "Small"; // Default
        if (chipSizeM != null && chipSizeM.isChecked()) {
            size = "Medium";
        } else if (chipSizeL != null && chipSizeL.isChecked()) {
            size = "Large";
        }

        // Get temperature - default to "Hot" since we don't have radioGroupTemperature
        String temperature = "Hot";

        // Get selected extras
        StringBuilder customizations = new StringBuilder();
        if (chipGroupExtras != null) {
            for (int i = 0; i < chipGroupExtras.getChildCount(); i++) {
                Chip chip = (Chip) chipGroupExtras.getChildAt(i);
                if (chip.isChecked()) {
                    if (customizations.length() > 0) {
                        customizations.append(", ");
                    }
                    customizations.append(chip.getText());
                }
            }
        }

        // Add to cart
        CartManager.getInstance().addToCart(
                product,
                quantity,
                size,
                temperature,
                customizations.length() > 0 ? customizations.toString() : null);

        // Show success message
        Toast.makeText(requireContext(), getString(R.string.added_to_cart), Toast.LENGTH_SHORT).show();

        // Navigate back
        Navigation.findNavController(requireView()).popBackStack();
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
