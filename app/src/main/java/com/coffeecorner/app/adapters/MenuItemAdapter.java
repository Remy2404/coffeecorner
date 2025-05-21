package com.coffeecorner.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.coffeecorner.app.R;
import com.coffeecorner.app.models.MenuItem; 

public class MenuItemAdapter extends ListAdapter<MenuItem, MenuItemAdapter.MenuItemViewHolder> {

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onAddToCartClick(MenuItem menuItem);
        // Add other click listeners if needed (e.g., for the whole item)
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public MenuItemAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<MenuItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<MenuItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull MenuItem oldItem, @NonNull MenuItem newItem) {
            // TODO: Implement proper item comparison
            return oldItem.getId().equals(newItem.getId()); // Assuming MenuItem has getId()
        }

        @Override
        public boolean areContentsTheSame(@NonNull MenuItem oldItem, @NonNull MenuItem newItem) {
            // TODO: Implement proper content comparison
            return oldItem.equals(newItem); // Assuming MenuItem has proper equals()
        }
    };

    @NonNull
    @Override
    public MenuItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu_product, parent, false); // Assuming item_menu_product.xml layout
        return new MenuItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuItemViewHolder holder, int position) {
        MenuItem menuItem = getItem(position);
        // TODO: Bind menu item data to views in the ViewHolder
        holder.tvMenuItemName.setText(menuItem.getName()); // Assuming MenuItem has getName()
        holder.tvMenuItemDescription.setText(menuItem.getDescription()); // Assuming MenuItem has getDescription()
        holder.tvMenuItemPrice.setText(String.format("$%.2f", menuItem.getPrice())); // Assuming MenuItem has getPrice()

        // Load image using Glide
        // Assuming you have Glide set up in your project and MenuItem has getImageUrl()
        // If not, you'll need to add the Glide dependency and initialize it.
        Glide.with(holder.itemView.getContext())
                .load(menuItem.getImageUrl())
                .placeholder(R.drawable.default_profile) // Add a placeholder drawable
                .error(R.drawable.default_profile) // Add an error drawable
                .into(holder.ivMenuItemImage);

        // TODO: Bind rating if MenuItem model includes rating data
        // holder.tvMenuItemRating.setText(String.valueOf(menuItem.getRating())); // Assuming MenuItem has getRating()

        // Set up click listener for Add to Cart button
        holder.btnAdd2Cart.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddToCartClick(menuItem);
            }
        });
        // Bind other data like price, image, etc.
    }

    static class MenuItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvMenuItemName;
        ImageView ivMenuItemImage;
        TextView tvMenuItemDescription;
        TextView tvMenuItemPrice;
        // TextView tvMenuItemRating; // Uncomment if binding rating
        Button btnAdd2Cart;

        public MenuItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMenuItemName = itemView.findViewById(R.id.tvMenuItemName); // Assuming TextView with this ID exists in item_menu_product.xml
            ivMenuItemImage = itemView.findViewById(R.id.product_image);
            tvMenuItemDescription = itemView.findViewById(R.id.product_description);
            tvMenuItemPrice = itemView.findViewById(R.id.product_price);
            // tvMenuItemRating = itemView.findViewById(R.id.product_rating); // Uncomment if binding rating
            btnAdd2Cart = itemView.findViewById(R.id.add_to_cart_button); // Assuming Button with this ID exists
        }
    }
} 