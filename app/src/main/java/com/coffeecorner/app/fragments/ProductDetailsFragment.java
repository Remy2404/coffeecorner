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
        imgProduct = view.findViewById(R.id.imgProductDetails);
        tvProductName = view.findViewById(R.id.tvProductName);
        tvDescription = view.findViewById(R.id.tvProductDescription);
        tvPrice = view.findViewById(R.id.tvProductPrice);
        tvQuantity = view.findViewById(R.id.tvQuantity);
        tvCalories = view.findViewById(R.id.tvCalories);
        
        btnBack = view.findViewById(R.id.btnBack);
        btnFavorite = view.findViewById(R.id.btnFavorite);
        btnDecrease = view.findViewById(R.id.btnDecrease);
        btnIncrease = view.findViewById(R.id.btnIncrease);
        btnAddToCart = view.findViewById(R.id.btnAddToCart);
        
        rgSize = view.findViewById(R.id.radioGroupSize);
        rgTemperature = view.findViewById(R.id.radioGroupTemperature);
        chipGroupExtras = view.findViewById(R.id.chipGroupExtras);
        
        // Set initial quantity
        tvQuantity.setText(String.valueOf(quantity));
        
        // Configure collapsing toolbar
        CollapsingToolbarLayout collapsingToolbar = view.findViewById(R.id.collapsingToolbarProductDetails);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
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
            .execute(response -> {
                product = response.getData(Product.class);
                
                // Update UI on main thread
                requireActivity().runOnUiThread(() -> {
                    displayProduct();
                });
                
                // Check if product is in favorites
                checkFavoriteStatus(productId);
                
                return null;
            }, error -> {
                // Handle error
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Failed to load product", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).popBackStack();
                });
                return null;
            });
    }
    
    private void displayProduct() {
        if (product == null) return;
        
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
        if (product == null) return;
        
        double basePrice = product.getPrice();
        double sizeExtra = 0;
        
        // Add size price
        int selectedSizeId = rgSize.getCheckedRadioButtonId();
        if (selectedSizeId == R.id.radioMedium) {
            sizeExtra = 0.5; // $0.50 extra for medium
        } else if (selectedSizeId == R.id.radioLarge) {
            sizeExtra = 1.0; // $1.00 extra for large
        }
        
        // Add extras price
        double extrasTotal = 0;
        for (int i = 0; i < chipGroupExtras.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupExtras.getChildAt(i);
            if (chip.isChecked()) {
                // Each extra costs $0.50
                extrasTotal += 0.5;
            }
        }
        
        // Calculate total
        double itemTotal = (basePrice + sizeExtra + extrasTotal) * quantity;
        
        // Update button text
        btnAddToCart.setText("Add to Cart - " + currencyFormatter.format(itemTotal));
    }
    
    private void addToCart() {
        if (product == null) return;
        
        // Get selected size
        String size = "Small"; // Default
        int selectedSizeId = rgSize.getCheckedRadioButtonId();
        if (selectedSizeId == R.id.radioMedium) {
            size = "Medium";
        } else if (selectedSizeId == R.id.radioLarge) {
            size = "Large";
        }
        
        // Get selected temperature
        String temperature = "Hot"; // Default
        int selectedTempId = rgTemperature.getCheckedRadioButtonId();
        if (selectedTempId == R.id.radioIced) {
            temperature = "Iced";
        }
        
        // Get selected extras
        StringBuilder customizations = new StringBuilder();
        for (int i = 0; i < chipGroupExtras.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupExtras.getChildAt(i);
            if (chip.isChecked()) {
                if (customizations.length() > 0) {
                    customizations.append(", ");
                }
                customizations.append(chip.getText());
            }
        }
        
        // Add to cart
        CartManager.getInstance().addToCart(
            product, 
            quantity, 
            size, 
            temperature, 
            customizations.length() > 0 ? customizations.toString() : null
        );
        
        // Show success message
        Toast.makeText(requireContext(), "Added to cart", Toast.LENGTH_SHORT).show();
        
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
        btnFavorite.setImageResource(isFavorite ? 
            R.drawable.ic_favorite_filled : 
            R.drawable.ic_favorite_outline);
    }
    
    private void toggleFavoriteStatus() {
        // In a real app, update the favorite status in the database
        // For simplicity, we'll just show a toast
        String message = isFavorite ? 
            "Added to favorites" : 
            "Removed from favorites";
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
