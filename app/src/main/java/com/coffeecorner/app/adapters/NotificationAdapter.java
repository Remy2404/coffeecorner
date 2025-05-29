package com.coffeecorner.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.coffeecorner.app.R;
import com.coffeecorner.app.models.Notification;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private final Context context;
    private final List<Notification> notifications;
    private OnNotificationActionListener listener;

    public NotificationAdapter(Context context, List<Notification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    public void setOnNotificationActionListener(OnNotificationActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);

        holder.tvTitle.setText(notification.getTitle());
        holder.tvContent.setText(notification.getContent());
        holder.tvTimeAgo.setText(notification.getTimeAgo());
        holder.ivIcon.setImageResource(notification.getIconResource());

        // Set background tint for the icon
        holder.flIconBackground.setBackgroundTintList(
                ContextCompat.getColorStateList(context, notification.getBackgroundTint()));

        // Set unread indicator visibility
        holder.viewUnreadIndicator.setVisibility(notification.isRead() ? View.GONE : View.VISIBLE);

        // Set card background based on read status
        holder.itemView.setBackgroundColor(ContextCompat.getColor(context,
                notification.isRead() ? R.color.white : R.color.unread_notification_bg));

        // Set delete button click listener
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClicked(notification, position);
            }
        });

        // Set item click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNotificationClicked(notification, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < notifications.size()) {
            notifications.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, notifications.size());
        }
    }

    public void clearAll() {
        int size = notifications.size();
        notifications.clear();
        notifyItemRangeRemoved(0, size);
    }

    public interface OnNotificationActionListener {
        void onNotificationClicked(Notification notification, int position);

        void onDeleteClicked(Notification notification, int position);
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        View viewUnreadIndicator;
        FrameLayout flIconBackground;
        ImageView ivIcon;
        TextView tvTitle;
        TextView tvContent;
        TextView tvTimeAgo;
        ImageButton btnDelete;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            viewUnreadIndicator = itemView.findViewById(R.id.viewUnreadIndicator);
            flIconBackground = itemView.findViewById(R.id.flIconBackground);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvTimeAgo = itemView.findViewById(R.id.tvTimeAgo);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
