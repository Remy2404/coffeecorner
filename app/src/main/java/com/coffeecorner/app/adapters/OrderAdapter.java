package com.coffeecorner.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.coffeecorner.app.R;
import com.coffeecorner.app.activities.OrderTrackingActivity;
import com.coffeecorner.app.models.Order;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orders;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy • h:mm a", Locale.getDefault());
    private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);

    public OrderAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);

        // Set order ID with null check
        String orderId = order.getOrderId();
        if (orderId != null && orderId.length() >= 8) {
            holder.tvOrderId.setText("Order #" + orderId.substring(0, 8).toUpperCase());
        } else if (orderId != null && orderId.length() > 0) {
            holder.tvOrderId.setText("Order #" + orderId.toUpperCase());
        } else {
            holder.tvOrderId.setText("Order #N/A");
        }

        // Set order date
        if (order.getOrderDate() != null) {
            holder.tvOrderDate.setText(dateFormat.format(order.getOrderDate()));
        }

        // Set order amount
        double total = order.getSubtotal() + order.getTax() + order.getDeliveryFee() - order.getDiscount();
        holder.tvOrderAmount.setText(currencyFormatter.format(total));

        // Set order status
        holder.tvOrderStatus.setText(getFormattedStatus(order.getStatus()));
        holder.tvOrderStatus.setTextColor(getStatusColor(order.getStatus()));

        // Set number of items
        int itemCount = order.getItems() != null ? order.getItems().size() : 0;
        holder.tvItemCount.setText(itemCount + " item" + (itemCount != 1 ? "s" : ""));

        // Configure primary button based on order status
        configureActionButton(holder.btnPrimary, order); // Set click listener for the entire item
        holder.itemView.setOnClickListener(v -> {
            // Navigate to order details/tracking screen
            if (orderId != null && !orderId.isEmpty()) {
                Intent intent = new Intent(context, OrderTrackingActivity.class);
                intent.putExtra("orderId", orderId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    private String getFormattedStatus(String status) {
        switch (status) {
            case Order.STATUS_CONFIRMED:
                return "Confirmed";
            case Order.STATUS_PREPARING:
                return "Preparing";
            case Order.STATUS_READY:
                return "Ready for Pickup";
            case Order.STATUS_DELIVERING:
                return "Out for Delivery";
            case Order.STATUS_DELIVERED:
                return "Delivered";
            case Order.STATUS_COMPLETED:
                return "Completed";
            case Order.STATUS_CANCELLED:
                return "Cancelled";
            default:
                return "Unknown";
        }
    }

    private int getStatusColor(String status) {
        switch (status) {
            case Order.STATUS_CONFIRMED:
            case Order.STATUS_PREPARING:
            case Order.STATUS_READY:
            case Order.STATUS_DELIVERING:
                return ContextCompat.getColor(context, R.color.primary);
            case Order.STATUS_DELIVERED:
            case Order.STATUS_COMPLETED:
                return ContextCompat.getColor(context, R.color.notification_news); // Green
            case Order.STATUS_CANCELLED:
                return ContextCompat.getColor(context, R.color.notification_promotion); // Red
            default:
                return ContextCompat.getColor(context, R.color.text_secondary);
        }
    }

    private void configureActionButton(Button button, Order order) {
        String orderId = order.getOrderId();

        switch (order.getStatus()) {
            case Order.STATUS_CONFIRMED:
            case Order.STATUS_PREPARING:
            case Order.STATUS_DELIVERING:
                button.setText("Track Order");
                button.setVisibility(View.VISIBLE);
                button.setOnClickListener(v -> {
                    if (orderId != null && !orderId.isEmpty()) {
                        Intent intent = new Intent(context, OrderTrackingActivity.class);
                        intent.putExtra("orderId", orderId);
                        context.startActivity(intent);
                    }
                });
                break;
            case Order.STATUS_READY:
                button.setText("Pick Up");
                button.setVisibility(View.VISIBLE);
                button.setOnClickListener(v -> {
                    if (orderId != null && !orderId.isEmpty()) {
                        Intent intent = new Intent(context, OrderTrackingActivity.class);
                        intent.putExtra("orderId", orderId);
                        context.startActivity(intent);
                    }
                });
                break;
            case Order.STATUS_DELIVERED:
            case Order.STATUS_COMPLETED:
                button.setText("Reorder");
                button.setVisibility(View.VISIBLE);
                button.setOnClickListener(v -> {
                    // Implement reorder functionality
                    // Add items to cart and navigate to cart
                });
                break;
            case Order.STATUS_CANCELLED:
                button.setVisibility(View.GONE);
                break;
            default:
                button.setVisibility(View.GONE);
                break;
        }
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvOrderStatus, tvOrderAmount, tvItemCount;
        Button btnPrimary;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvOrderAmount = itemView.findViewById(R.id.tvOrderAmount);
            tvItemCount = itemView.findViewById(R.id.tvItemCount);
            btnPrimary = itemView.findViewById(R.id.btnPrimary);
        }
    }
}
