package com.coffeecorner.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coffeecorner.app.R;
import com.coffeecorner.app.models.FeedbackItem;
import com.coffeecorner.app.utils.AppUtils;

import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {

    private Context context;
    private List<FeedbackItem> feedbackItems;
    private OnFeedbackInteractionListener listener;

    // Interface for handling interactions
    public interface OnFeedbackInteractionListener {
        void onHelpfulClicked(FeedbackItem item, int position);

        void onReplyClicked(FeedbackItem item, int position);
    }

    public FeedbackAdapter(Context context, List<FeedbackItem> feedbackItems) {
        this.context = context;
        this.feedbackItems = feedbackItems;

        // Try to cast context to listener if it implements the interface
        if (context instanceof OnFeedbackInteractionListener) {
            this.listener = (OnFeedbackInteractionListener) context;
        }
    }

    // Alternative constructor with explicit listener
    public FeedbackAdapter(Context context, List<FeedbackItem> feedbackItems, OnFeedbackInteractionListener listener) {
        this.context = context;
        this.feedbackItems = feedbackItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback, parent, false);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        FeedbackItem item = feedbackItems.get(position);

        holder.tvUserName.setText(item.getUserName());
        holder.tvFeedbackContent.setText(item.getContent());
        holder.ratingBar.setRating(item.getRating());
        holder.tvTimeAgo.setText(item.getTimeAgo());

        // Load user photo if available
        if (item.getUserPhotoUrl() != null && !item.getUserPhotoUrl().isEmpty()) {
            // Use image loading library like Glide or Picasso
            // Glide.with(context).load(item.getUserPhotoUrl()).into(holder.ivUserPhoto);

            // For now we'll use a placeholder
            holder.ivUserPhoto.setImageResource(R.drawable.ic_default_user);
        } else {
            // Set default user photo
            holder.ivUserPhoto.setImageResource(R.drawable.ic_default_user);
        }        // Set up interaction listeners
        holder.tvHelpful.setOnClickListener(v -> {
            if (listener != null) {
                listener.onHelpfulClicked(item, holder.getAdapterPosition());
            } else {
                // Fallback if no listener is set
                Toast.makeText(context, "Marked as helpful", Toast.LENGTH_SHORT).show();
            }
        });

        holder.tvReply.setOnClickListener(v -> {
            if (listener != null) {
                listener.onReplyClicked(item, holder.getAdapterPosition());
            } else {
                // Fallback if no listener is set
                Toast.makeText(context, "Reply option coming soon", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to update feedback items
    public void updateFeedbackItems(List<FeedbackItem> newItems) {
        this.feedbackItems.clear();
        this.feedbackItems.addAll(newItems);
        notifyDataSetChanged();
    }

    // Method to add a single feedback item
    public void addFeedbackItem(FeedbackItem item) {
        this.feedbackItems.add(0, item); // Add to the top of the list
        notifyItemInserted(0);
    }

    @Override
    public int getItemCount() {
        return feedbackItems.size();
    }

    static class FeedbackViewHolder extends RecyclerView.ViewHolder {
        ImageView ivUserPhoto;
        TextView tvUserName;
        TextView tvFeedbackContent;
        RatingBar ratingBar;
        TextView tvTimeAgo;
        TextView tvHelpful;
        TextView tvReply;
        LinearLayout layoutPhotoContainer;

        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);

            ivUserPhoto = itemView.findViewById(R.id.ivUserPhoto);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvFeedbackContent = itemView.findViewById(R.id.tvFeedbackContent);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            tvTimeAgo = itemView.findViewById(R.id.tvTimeAgo);
            tvHelpful = itemView.findViewById(R.id.tvHelpful);
            tvReply = itemView.findViewById(R.id.tvReply);
            layoutPhotoContainer = itemView.findViewById(R.id.layoutPhotoContainer);
        }
    }
}