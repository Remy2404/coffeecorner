package com.coffeecorner.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.coffeecorner.app.R;
import com.coffeecorner.app.models.Product;

public class MenuItemAdapter extends ListAdapter<Product, MenuItemAdapter.MenuItemViewHolder> {

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onAddToCartClick(Product product);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public MenuItemAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Product> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getDescription().equals(newItem.getDescription()) &&
                    oldItem.getPrice() == newItem.getPrice() &&
                    (oldItem.getImageUrl() == null ? newItem.getImageUrl() == null : oldItem.getImageUrl().equals(newItem.getImageUrl()));
        }
    };

    @NonNull
    @Override
    public MenuItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu_product, parent, false);
        return new MenuItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuItemViewHolder holder, int position) {
        Product product = getItem(position);

        holder.tvMenuItemName.setText(product.getName());
        holder.tvMenuItemDescription.setText(product.getDescription());
        holder.tvMenuItemPrice.setText(String.format("$%.2f", product.getPrice()));

        // Load image if available
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.default_profile)
                    .error(R.drawable.default_profile)
                    .into(holder.ivMenuItemImage);
        } else {
            holder.ivMenuItemImage.setImageResource(R.drawable.default_profile);
        }

        if (holder.btnAdd2Cart != null) {
            holder.btnAdd2Cart.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddToCartClick(product);
                }
            });
        } else {
            // Optionally, log an error or handle the case where the button is not found
            // For example: android.util.Log.e("MenuItemAdapter", "btnAdd2Cart is null for
            // product: " + product.getName());
        }
    }

    static class MenuItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvMenuItemName;
        ImageView ivMenuItemImage;
        TextView tvMenuItemDescription;
        TextView tvMenuItemPrice;
        Button btnAdd2Cart;

        public MenuItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMenuItemName = itemView.findViewById(R.id.product_name);
            ivMenuItemImage = itemView.findViewById(R.id.product_image);
            tvMenuItemDescription = itemView.findViewById(R.id.product_description);
            tvMenuItemPrice = itemView.findViewById(R.id.product_price);
            btnAdd2Cart = itemView.findViewById(R.id.add_to_cart_button);
        }
    }
}