package com.coffeecorner.app.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coffeecorner.app.R;
import com.coffeecorner.app.models.Product;
import com.coffeecorner.app.utils.ImageLoader;
import com.google.android.material.card.MaterialCardView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private final Context context;
    private OnProductClickListener listener;
    private OnAddToCartClickListener cartListener;

    public interface OnProductClickListener {
        void onProductClick(Product product, int position);
    }

    public interface OnAddToCartClickListener {
        void onAddToCartClick(Product product, int position);
    }

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.listener = listener;
    }

    public void setOnAddToCartClickListener(OnAddToCartClickListener listener) {
        this.cartListener = listener;
    }

    public void updateProducts(List<Product> newProducts) {
        this.productList = newProducts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_home, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // Set product name and subtitle
        holder.tvProductName.setText(product.getName());
        holder.tvProductSubtitle.setText(product.getDescription());

        // Set product rating
        if (product.getRating() > 0) {
            holder.tvRating.setText(String.format("%.1f", product.getRating()));
            holder.ratingBadge.setVisibility(View.VISIBLE);
        } else {
            holder.ratingBadge.setVisibility(View.GONE);
        }

        // Format and set price
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
        String formattedPrice = currencyFormatter.format(product.getPrice());
        holder.tvPrice.setText(formattedPrice); // Load image using ImageLoader utility
        ImageLoader.loadImage(
                context,
                product.getImageUrl(),
                holder.ivProductImage,
                R.drawable.coffee_coco,
                R.drawable.coffee_coco,
                new ImageLoader.ImageLoadListener() {
                    @Override
                    public void onSuccess() {
                        // Image loaded successfully
                    }

                    @Override
                    public void onError() {
                        // Log error but UI already shows fallback image
                        Log.w("ProductAdapter", "Failed to load image for product: " + product.getName());
                    }
                });

        // Set click listeners
        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(product, holder.getAdapterPosition());
            }
        });

        holder.btnAdd.setOnClickListener(v -> {
            if (cartListener != null) {
                cartListener.onAddToCartClick(product, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvProductSubtitle, tvRating, tvPrice;
        ImageButton btnAdd;
        MaterialCardView cardView;
        View ratingBadge;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            ivProductImage = itemView.findViewById(R.id.ivProductImageHome);
            tvProductName = itemView.findViewById(R.id.tvProductNameHome);
            tvProductSubtitle = itemView.findViewById(R.id.tvProductSubtitleHome);
            tvRating = itemView.findViewById(R.id.product_rating);
            tvPrice = itemView.findViewById(R.id.tvProductPriceHome);
            btnAdd = itemView.findViewById(R.id.btnAddHome);
            ratingBadge = itemView.findViewById(R.id.ratingBadge);
        }
    }
}
