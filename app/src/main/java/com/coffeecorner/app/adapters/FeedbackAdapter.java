package com.coffeecorner.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coffeecorner.app.R;
import com.coffeecorner.app.models.FeedbackItem;

import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {

    private Context context;
    private List<FeedbackItem> feedbackItems;

    public FeedbackAdapter(Context context, List<FeedbackItem> feedbackItems) {
        this.context = context;
        this.feedbackItems = feedbackItems;
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_feedback, parent, false);
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
        } else {
            // Set default user photo
            holder.ivUserPhoto.setImageResource(R.drawable.ic_default_user);
        }
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

        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            
            ivUserPhoto = itemView.findViewById(R.id.ivUserPhoto);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvFeedbackContent = itemView.findViewById(R.id.tvFeedbackContent);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            tvTimeAgo = itemView.findViewById(R.id.tvTimeAgo);
        }
    }
}
