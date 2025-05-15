package com.coffeecorner.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.coffeecorner.app.R;
import com.coffeecorner.app.models.Product;
import com.google.android.material.card.MaterialCardView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private final Context context;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product, int position);
    }
    
    // Constructor without listener for HomeFragment
    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    // Constructor with listener
    public ProductAdapter(Context context, List<Product> productList, OnProductClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }
    
    // Method to update products list - needed by HomeFragment
    public void updateProducts(List<Product> newProducts) {
        this.productList = newProducts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_home, parent, false);
        return new ProductViewHolder(view);
    }    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        
        // Set product name
        holder.tvProductName.setText(product.getName());
        
        // Set product rating if available
        if (holder.tvRating != null) {
            holder.tvRating.setText(String.valueOf(product.getRating()));
        }
        
        // Format and set price
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
        holder.tvPrice.setText(currencyFormatter.format(product.getPrice()));
        
        // Load image using Glide
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(context)
                 .load(product.getImageUrl())
                 .placeholder(R.drawable.coffee_placeholder)
                 .error(R.drawable.coffee_placeholder)
                 .centerCrop()
                 .into(holder.ivProductImage);
        } else {
            holder.ivProductImage.setImageResource(R.drawable.coffee_placeholder);
        }
        
        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(product, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvRating, tvPrice;
        MaterialCardView cardView;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImageHome);
            tvProductName = itemView.findViewById(R.id.tvProductNameHome);
            tvRating = itemView.findViewById(R.id.product_rating);
            tvPrice = itemView.findViewById(R.id.tvProductPriceHome);
            cardView = (MaterialCardView) itemView;
        }
    }
}
