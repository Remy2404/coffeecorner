package com.coffeecorner.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.coffeecorner.app.R;
import com.coffeecorner.app.models.PointsHistory;

import java.util.List;

public class PointsHistoryAdapter extends RecyclerView.Adapter<PointsHistoryAdapter.PointsHistoryViewHolder> {

    private Context context;
    private List<PointsHistory> pointsHistoryList;

    public PointsHistoryAdapter(Context context, List<PointsHistory> pointsHistoryList) {
        this.context = context;
        this.pointsHistoryList = pointsHistoryList;
    }

    @NonNull
    @Override
    public PointsHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_points_history, parent, false);
        return new PointsHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PointsHistoryViewHolder holder, int position) {
        PointsHistory pointsHistory = pointsHistoryList.get(position);

        holder.tvTransactionTitle.setText(pointsHistory.getTitle());
        holder.tvTransactionDate.setText(pointsHistory.getDate());

        // Format and set points
        holder.tvPointsValue.setText(pointsHistory.getFormattedPoints());

        // Set appropriate color for points (green for earned, red for spent)
        int textColor;
        if (pointsHistory.isEarned()) {
            textColor = ContextCompat.getColor(context, R.color.color_02); // green
            holder.ivTransactionType.setImageResource(R.drawable.ic_points_earned);
        } else {
            textColor = ContextCompat.getColor(context, R.color.color_01); // red
            holder.ivTransactionType.setImageResource(R.drawable.ic_points_spent);
        }

        holder.tvPointsValue.setTextColor(textColor);
    }

    @Override
    public int getItemCount() {
        return pointsHistoryList.size();
    }

    static class PointsHistoryViewHolder extends RecyclerView.ViewHolder {
        ImageView ivTransactionType;
        TextView tvTransactionTitle;
        TextView tvTransactionDate;
        TextView tvPointsValue;

        public PointsHistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            ivTransactionType = itemView.findViewById(R.id.ivTransactionType);
            tvTransactionTitle = itemView.findViewById(R.id.tvTransactionTitle);
            tvTransactionDate = itemView.findViewById(R.id.tvTransactionDate);
            tvPointsValue = itemView.findViewById(R.id.tvPointsValue);
        }
    }
}
