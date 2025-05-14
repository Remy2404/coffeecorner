package com.coffeecorner.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coffeecorner.app.R;
import com.coffeecorner.app.activities.CheckoutActivity;
import com.coffeecorner.app.adapters.CartAdapter;
import com.coffeecorner.app.models.CartItem;
import com.coffeecorner.app.utils.CartManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartFragment extends Fragment implements CartAdapter.CartItemListener {

    private RecyclerView recyclerCartItems;
    private TextView tvSubtotal, tvTax, tvDeliveryFee, tvDiscount, tvTotal;
    private Button btnCheckout, btnBrowseMenu;
    private LinearLayout emptyCartView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems = new ArrayList<>();
    private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);

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
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        recyclerCartItems = view.findViewById(R.id.recyclerCartItems);
        tvSubtotal = view.findViewById(R.id.tvSubtotal);
        tvTax = view.findViewById(R.id.tvTax);
        tvDeliveryFee = view.findViewById(R.id.tvDeliveryFee);
        tvDiscount = view.findViewById(R.id.tvDiscount);
        tvTotal = view.findViewById(R.id.tvTotal);
        btnCheckout = view.findViewById(R.id.btnCheckout);
        btnBrowseMenu = view.findViewById(R.id.btnBrowseMenu);
        emptyCartView = view.findViewById(R.id.emptyCartView);

        // Set up RecyclerView
        recyclerCartItems.setLayoutManager(new LinearLayoutManager(requireContext()));

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
            // Navigate to menu
            if (getActivity() != null) {
                // Navigate to the menu tab in the main activity
                // This will depend on your specific navigation setup
                // For example, if using a bottom navigation view:
                // ((MainActivity) getActivity()).navigateToMenuTab();

                // Or using navigation component:
                Navigation.findNavController(view).navigate(R.id.action_to_menu);
            }
        });

        view.findViewById(R.id.btnClearCart).setOnClickListener(v -> {
            if (!cartItems.isEmpty()) {
                showClearCartConfirmation();
            }
        });
    }

    private void loadCartItems() {
        // In a real app, get cart items from a CartManager or repository
        cartItems = CartManager.getInstance().getCartItems();

        if (cartItems.isEmpty()) {
            showEmptyCartView();
            return;
        }

        // Set up adapter
        cartAdapter = new CartAdapter(requireContext(), cartItems, this);
        recyclerCartItems.setAdapter(cartAdapter);

        // Update price summary
        updatePriceSummary();
    }

    private void updatePriceSummary() {
        double subtotal = calculateSubtotal();
        double tax = calculateTax();
        double discount = calculateDiscount();
        double total = calculateTotal();

        tvSubtotal.setText(currencyFormatter.format(subtotal));
        tvTax.setText(currencyFormatter.format(tax));
        tvDeliveryFee.setText(currencyFormatter.format(DELIVERY_FEE));
        tvDiscount.setText(discount > 0 ? "-" + currencyFormatter.format(discount) : currencyFormatter.format(0));
        tvTotal.setText(currencyFormatter.format(total));
    }

    private double calculateSubtotal() {
        double subtotal = 0;
        for (CartItem item : cartItems) {
            subtotal += item.getProduct().getPrice() * item.getQuantity();
        }
        return subtotal;
    }

    private double calculateTax() {
        return calculateSubtotal() * TAX_RATE;
    }

    private double calculateDiscount() {
        // In a real app, implement discount logic here
        // For example, apply promo codes, loyalty discounts, etc.
        return 0;
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
        CartManager.getInstance().clearCart();
        cartItems.clear();
        if (cartAdapter != null) {
            cartAdapter.notifyDataSetChanged();
        }
        showEmptyCartView();
    }

    @Override
    public void onItemRemoved(CartItem cartItem) {
        // Remove from cart
        CartManager.getInstance().removeFromCart(cartItem);
        cartItems.remove(cartItem);
        cartAdapter.notifyDataSetChanged();

        if (cartItems.isEmpty()) {
            showEmptyCartView();
        } else {
            updatePriceSummary();
        }
    }

    @Override
    public void onQuantityChanged(CartItem cartItem, int newQuantity) {
        // Update cart
        CartManager.getInstance().updateCartItemQuantity(cartItem, newQuantity);
        updatePriceSummary();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh cart when returning to this fragment
        loadCartItems();
    }
}
