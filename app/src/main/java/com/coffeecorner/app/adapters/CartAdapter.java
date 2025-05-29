package com.coffeecorner.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;

import com.bumptech.glide.Glide;
import com.coffeecorner.app.R;
import com.coffeecorner.app.models.CartItem;
import com.coffeecorner.app.models.Product;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartItems;
    private CartItemListener listener;
    private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);

    public interface CartItemListener {
        void onItemRemoved(CartItem cartItem);

        void onQuantityChanged(CartItem cartItem, int newQuantity);
    }

    public CartAdapter(Context context, List<CartItem> cartItems, CartItemListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        Product product = cartItem.getProduct();

        // Check if product is null
        if (product == null) {
            holder.tvProductName.setText("Unknown Product");
            holder.imgProduct.setImageResource(R.drawable.coffee_placeholder);
            holder.tvProductVariant.setText("");
            holder.tvPrice.setText(currencyFormatter.format(0.0));
            holder.tvQuantity.setText(String.valueOf(cartItem.getQuantity()));

            // Disable buttons for invalid products
            holder.btnRemove.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemRemoved(cartItem);
                }
            });
            holder.btnIncreaseQuantity.setOnClickListener(null);
            holder.btnDecreaseQuantity.setOnClickListener(null);
            return;
        }

        // Set product name and image
        holder.tvProductName.setText(product.getName());

        // Load product image
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.coffee_placeholder)
                    .error(R.drawable.coffee_placeholder)
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.drawable.coffee_placeholder);
        }

        // Set product variant info (size, temperature, etc.)
        StringBuilder variantBuilder = new StringBuilder();
        if (cartItem.getSize() != null && !cartItem.getSize().isEmpty()) {
            variantBuilder.append(cartItem.getSize());
        }

        if (cartItem.getTemperature() != null && !cartItem.getTemperature().isEmpty()) {
            if (variantBuilder.length() > 0) {
                variantBuilder.append(", ");
            }
            variantBuilder.append(cartItem.getTemperature());
        }

        // Add any customizations
        if (cartItem.getCustomizations() != null && !cartItem.getCustomizations().isEmpty()) {
            if (variantBuilder.length() > 0) {
                variantBuilder.append(", ");
            }
            variantBuilder.append(cartItem.getCustomizations());
        }

        holder.tvProductVariant.setText(variantBuilder.toString());

        // Set price
        double itemPrice = product.getPrice() * cartItem.getQuantity();
        holder.tvPrice.setText(currencyFormatter.format(itemPrice));

        // Set quantity
        holder.tvQuantity.setText(String.valueOf(cartItem.getQuantity()));

        // Set click listeners
        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemRemoved(cartItem);
            }
        });
        holder.btnIncreaseQuantity.setOnClickListener(v -> {
            int newQuantity = cartItem.getQuantity() + 1;
            if (newQuantity <= 10 && product != null) { // Set a reasonable maximum and check product
                cartItem.setQuantity(newQuantity);
                holder.tvQuantity.setText(String.valueOf(newQuantity));

                // Update price
                double newPrice = product.getPrice() * newQuantity;
                holder.tvPrice.setText(currencyFormatter.format(newPrice));

                if (listener != null) {
                    listener.onQuantityChanged(cartItem, newQuantity);
                }
            }
        });

        holder.btnDecreaseQuantity.setOnClickListener(v -> {
            int newQuantity = cartItem.getQuantity() - 1;
            if (newQuantity >= 1 && product != null) {
                cartItem.setQuantity(newQuantity);
                holder.tvQuantity.setText(String.valueOf(newQuantity));

                // Update price
                double newPrice = product.getPrice() * newQuantity;
                holder.tvPrice.setText(currencyFormatter.format(newPrice));

                if (listener != null) {
                    listener.onQuantityChanged(cartItem, newQuantity);
                }
            } else {
                // If quantity would be less than 1, remove the item
                if (listener != null) {
                    listener.onItemRemoved(cartItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void updateCartItems(List<CartItem> newCartItems) {
        this.cartItems = newCartItems;
        notifyDataSetChanged();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvProductName, tvProductVariant, tvPrice, tvQuantity;
        ImageButton btnRemove, btnDecreaseQuantity, btnIncreaseQuantity; // Changed back to ImageButton

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductVariant = itemView.findViewById(R.id.tvProductVariant);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            btnDecreaseQuantity = itemView.findViewById(R.id.btnDecreaseQuantity);
            btnIncreaseQuantity = itemView.findViewById(R.id.btnIncreaseQuantity);
        }
    }
}
