package com.coffeecorner.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coffeecorner.app.R;
import com.coffeecorner.app.models.Reward;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class RewardAdapter extends RecyclerView.Adapter<RewardAdapter.RewardViewHolder> {

    private Context context;
    private List<Reward> rewards;
    private OnRewardActionListener listener;

    public RewardAdapter(Context context, List<Reward> rewards) {
        this.context = context;
        this.rewards = rewards;
    }

    public void setOnRewardActionListener(OnRewardActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RewardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reward, parent, false);
        return new RewardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RewardViewHolder holder, int position) {
        Reward reward = rewards.get(position);

        holder.tvRewardTitle.setText(reward.getTitle());
        holder.tvRewardDescription.setText(reward.getDescription());
        holder.tvPointsRequired.setText(reward.getPointsRequired() + " points");
        holder.ivRewardIcon.setImageResource(reward.getIconResourceId());

        holder.btnRedeem.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRedeemClicked(reward, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rewards.size();
    }

    public interface OnRewardActionListener {
        void onRedeemClicked(Reward reward, int position);
    }

    static class RewardViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRewardIcon;
        TextView tvRewardTitle;
        TextView tvRewardDescription;
        TextView tvPointsRequired;
        MaterialButton btnRedeem;

        public RewardViewHolder(@NonNull View itemView) {
            super(itemView);

            ivRewardIcon = itemView.findViewById(R.id.ivRewardIcon);
            tvRewardTitle = itemView.findViewById(R.id.tvRewardTitle);
            tvRewardDescription = itemView.findViewById(R.id.tvRewardDescription);
            tvPointsRequired = itemView.findViewById(R.id.tvPointsRequired);
            btnRedeem = itemView.findViewById(R.id.btnRedeem);
        }
    }
}
