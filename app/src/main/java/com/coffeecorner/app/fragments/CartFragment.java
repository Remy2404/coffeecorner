package com.coffeecorner.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coffeecorner.app.R;
import com.coffeecorner.app.activities.CheckoutActivity;
import com.coffeecorner.app.activities.MainActivity;
import com.coffeecorner.app.adapters.CartAdapter;
import com.coffeecorner.app.models.CartItem;
import com.coffeecorner.app.utils.PreferencesHelper;
import com.coffeecorner.app.viewmodels.CartViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartFragment extends Fragment implements CartAdapter.CartItemListener {
    private RecyclerView recyclerCartItems;
    private TextView tvSubtotal, tvTax, tvDeliveryFee, tvDiscount, tvTotal;
    private Button btnCheckout, btnBrowseMenu;
    private ConstraintLayout emptyCartView;
    private LottieAnimationView menuIconAnimation;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems = new ArrayList<>();
    private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
    private CartViewModel cartViewModel;

    private static final double TAX_RATE = 0.09; // 9%
    private static final double DELIVERY_FEE = 2.00;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState); // Initialize views
        recyclerCartItems = view.findViewById(R.id.recyclerCartItems);
        tvSubtotal = view.findViewById(R.id.tvSubtotal);
        tvTax = view.findViewById(R.id.tvTax);
        tvDeliveryFee = view.findViewById(R.id.tvDeliveryFee);
        tvDiscount = view.findViewById(R.id.tvDiscount);
        tvTotal = view.findViewById(R.id.tvTotal);
        btnCheckout = view.findViewById(R.id.btnCheckout);

        // Find correct views in the nested layout structure
        ConstraintLayout btnBrowseMenuContainer = view.findViewById(R.id.btnBrowseMenuContainer);
        if (btnBrowseMenuContainer != null) {
            btnBrowseMenu = btnBrowseMenuContainer.findViewById(R.id.btnBrowseMenu);
            menuIconAnimation = btnBrowseMenuContainer.findViewById(R.id.menuIconAnimation);
        } else {
            // Fallback to direct lookup if container not found
            btnBrowseMenu = view.findViewById(R.id.btnBrowseMenu);
            menuIconAnimation = view.findViewById(R.id.menuIconAnimation);
        }

        emptyCartView = view.findViewById(R.id.emptyCartView);

        // Set up RecyclerView
        recyclerCartItems.setLayoutManager(new LinearLayoutManager(requireContext())); // Initialize ViewModel
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);

        // Observe ViewModel
        observeViewModel();

        // Load cart items
        loadCartItems();

        // Set up listeners
        btnCheckout.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(requireContext(), "Your cart is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Navigate to checkout activity
            Intent intent = new Intent(requireContext(), CheckoutActivity.class);
            intent.putExtra("subtotal", calculateSubtotal());
            intent.putExtra("tax", calculateTax());
            intent.putExtra("deliveryFee", DELIVERY_FEE);
            intent.putExtra("discount", calculateDiscount());
            intent.putExtra("total", calculateTotal());
            startActivity(intent);
        });
        btnBrowseMenu.setOnClickListener(v -> {
            // Navigate to menu using navigation component
            try {
                Navigation.findNavController(view).navigate(R.id.action_to_menu);
            } catch (Exception e) {
                Log.e("CartFragment", "Navigation error: " + e.getMessage());
                Toast.makeText(requireContext(), "Navigation error", Toast.LENGTH_SHORT).show();
            }
        });

        view.findViewById(R.id.btnClearCart).setOnClickListener(v -> {
            if (!cartItems.isEmpty()) {
                showClearCartConfirmation();
            }
        });

        // Add click listener for back button
        view.findViewById(R.id.btnBackCart).setOnClickListener(v -> {
            // Go back to previous screen
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }

    private void loadCartItems() {
        // In a real app, get cart items from a CartManager or repository
        cartViewModel.getCartItems().observe(getViewLifecycleOwner(), items -> {
            if (items == null) { // Add null check for items
                items = new ArrayList<>();
            }
            cartItems = items;

            if (cartItems.isEmpty()) {
                showEmptyCartView();
                // Ensure adapter is not set with empty or null list if it was previously
                // initialized
                if (cartAdapter != null) {
                    cartAdapter.updateCartItems(new ArrayList<>()); // Clear adapter
                }
                return;
            }

            // Set up adapter or update existing adapter
            if (cartAdapter == null) {
                cartAdapter = new CartAdapter(requireContext(), cartItems, this);
                recyclerCartItems.setAdapter(cartAdapter);
            } else {
                cartAdapter.updateCartItems(cartItems);
            }

            // Update price summary
            updatePriceSummary();
        });
    }

    private void updatePriceSummary() {
        cartViewModel.getCartTotal().observe(getViewLifecycleOwner(), subtotal -> { // Use getCartTotal since
                                                                                    // getCartSubtotal doesn't exist
                                                                                    // getCartSubtotal
            double tax = subtotal * TAX_RATE;
            double discount = calculateDiscount();
            double total = subtotal + tax + DELIVERY_FEE - discount;

            tvSubtotal.setText(currencyFormatter.format(subtotal));
            tvTax.setText(currencyFormatter.format(tax));
            tvDeliveryFee.setText(currencyFormatter.format(DELIVERY_FEE));
            tvDiscount.setText(discount > 0 ? "-" + currencyFormatter.format(discount) : currencyFormatter.format(0));
            tvTotal.setText(currencyFormatter.format(total));
        });
    }

    private double calculateSubtotal() {
        Double subtotal = cartViewModel.getCartTotal().getValue(); // Use getCartTotal since getCartSubtotal doesn't
                                                                   // exist
        return subtotal != null ? subtotal : 0.0;
    }

    private double calculateTax() {
        return calculateSubtotal() * TAX_RATE;
    }

    private double calculateDiscount() {
        double discount = 0;

        // Apply loyalty points discount if available
        PreferencesHelper preferencesHelper = new PreferencesHelper(requireContext());
        int loyaltyPoints = preferencesHelper.getLoyaltyPoints();
        if (loyaltyPoints >= 100) {
            // 100 points = $5 discount
            discount += (loyaltyPoints / 100) * 5.0;
        }

        // Apply promo code discount if available
        String promoCode = preferencesHelper.getPromoCode();
        if (promoCode != null && !promoCode.isEmpty()) {
            switch (promoCode.toUpperCase()) {
                case "WELCOME10":
                    discount += calculateSubtotal() * 0.10; // 10% discount
                    break;
                case "SAVE5":
                    discount += 5.0; // $5 off
                    break;
                case "FIRST20":
                    discount += calculateSubtotal() * 0.20; // 20% discount for first-time users
                    break;
            }
        }

        return Math.min(discount, calculateSubtotal()); // Don't exceed subtotal
    }

    private double calculateTotal() {
        return calculateSubtotal() + calculateTax() + DELIVERY_FEE - calculateDiscount();
    }

    private void showEmptyCartView() {
        emptyCartView.setVisibility(View.VISIBLE);
        recyclerCartItems.setVisibility(View.GONE);
        tvSubtotal.setText(currencyFormatter.format(0));
        tvTax.setText(currencyFormatter.format(0));
        tvDeliveryFee.setText(currencyFormatter.format(0));
        tvDiscount.setText(currencyFormatter.format(0));
        tvTotal.setText(currencyFormatter.format(0));

        // Make sure animation is playing
        if (menuIconAnimation != null) {
            menuIconAnimation.playAnimation();
        }
    }

    private void showClearCartConfirmation() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Clear Cart")
                .setMessage("Are you sure you want to remove all items from your cart?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    clearCart();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void clearCart() {
        cartViewModel.clearCart();
        cartItems.clear();
        if (cartAdapter != null) {
            cartAdapter.notifyDataSetChanged();
        }
        showEmptyCartView();
    }

    @Override
    public void onItemRemoved(CartItem cartItem) {
        // Remove from cart
        if (cartItem != null && cartItem.getProductId() != null) {
            cartViewModel.removeFromCart(cartItem);
            // The LiveData observer in loadCartItems should handle UI updates
        } else {
            Toast.makeText(getContext(), "Error: Could not remove item", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onQuantityChanged(CartItem cartItem, int newQuantity) {
        // Update cart
        if (cartItem != null && cartItem.getProductId() != null) {
            cartViewModel.updateCartItemQuantity(cartItem, newQuantity);
            // The LiveData observer in updatePriceSummary should handle UI updates
        } else {
            Toast.makeText(getContext(), "Error: Could not update quantity", Toast.LENGTH_SHORT).show();
        }
    } // These methods are part of the CartAdapter.CartItemListener interface
      // implemented by the fragment and are already properly implemented above

    private void observeViewModel() {
        // Observe loading state
        cartViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            // Loading indicator logic removed - we'll use a different approach
        });

        // Observe error messages
        cartViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
        });

        // Observe success messages
        cartViewModel.getSuccessMessage().observe(getViewLifecycleOwner(), successMessage -> {
            if (successMessage != null && !successMessage.isEmpty()) {
                Toast.makeText(getContext(), successMessage, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe cart item count for badge updates
        cartViewModel.getCartItemCount().observe(getViewLifecycleOwner(), itemCount -> {
            if (itemCount != null) {
                // Update cart badge in bottom navigation if needed
                updateCartBadge(itemCount);
            }
        });
    }

    private void updateCartBadge(int itemCount) {
        // Update cart badge in MainActivity
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateCartBadge(itemCount);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh cart when returning to this fragment
        loadCartItems();
    }
}
