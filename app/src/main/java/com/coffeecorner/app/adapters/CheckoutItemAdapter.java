package com.coffeecorner.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.coffeecorner.app.R;
import com.coffeecorner.app.models.CartItem;
import java.util.List;

public class CheckoutItemAdapter extends RecyclerView.Adapter<CheckoutItemAdapter.ViewHolder> {

    private Context context;
    private List<CartItem> items;

    public CheckoutItemAdapter(Context context, List<CartItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
            .inflate(R.layout.item_checkout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = items.get(position);
        
        holder.tvItemName.setText(item.getProduct().getName());
        holder.tvQuantity.setText(String.format("x%d", item.getQuantity()));
        holder.tvSize.setText(item.getSize());
        holder.tvMilkOption.setText(item.getMilkOption());
        
        double totalPrice = item.getTotalPrice();
        holder.tvPrice.setText(String.format("$%.2f", totalPrice));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvQuantity, tvSize, tvMilkOption, tvPrice;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvSize = itemView.findViewById(R.id.tvSize);
            tvMilkOption = itemView.findViewById(R.id.tvMilkOption);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}
