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
import com.coffeecorner.app.models.PaymentMethod;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class PaymentMethodAdapter extends RecyclerView.Adapter<PaymentMethodAdapter.PaymentMethodViewHolder> {

    private Context context;
    private List<PaymentMethod> paymentMethods;
    private OnPaymentMethodActionListener listener;

    public PaymentMethodAdapter(List<PaymentMethod> paymentMethods, OnPaymentMethodActionListener listener) {
        this.paymentMethods = paymentMethods;
        this.listener = listener;
    }

    public PaymentMethodAdapter(Context context, List<PaymentMethod> paymentMethods) {
        this.context = context;
        this.paymentMethods = paymentMethods;
    }

    public void setOnPaymentMethodActionListener(OnPaymentMethodActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public PaymentMethodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment_method, parent, false);
        return new PaymentMethodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentMethodViewHolder holder, int position) {
        PaymentMethod paymentMethod = paymentMethods.get(position);

        holder.tvPaymentName.setText(paymentMethod.getDisplayName());
        holder.tvPaymentDetails.setText("**** " + paymentMethod.getLastFourDigits());
        holder.ivPaymentIcon.setImageResource(paymentMethod.getIconResourceId());

        if (paymentMethod.isDefault()) {
            holder.tvDefaultLabel.setVisibility(View.VISIBLE);
            holder.btnSetDefault.setVisibility(View.GONE);
        } else {
            holder.tvDefaultLabel.setVisibility(View.GONE);
            holder.btnSetDefault.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPaymentMethodClicked(paymentMethod, position);
            }
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditPaymentMethod(paymentMethod, position);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeletePaymentMethod(paymentMethod, position);
            }
        });

        holder.btnSetDefault.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSetDefaultPaymentMethod(paymentMethod, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return paymentMethods.size();
    }

    public void updatePaymentMethods(List<PaymentMethod> newPaymentMethods) {
        this.paymentMethods = newPaymentMethods;
        notifyDataSetChanged();
    }

    public interface OnPaymentMethodActionListener {
        void onPaymentMethodClicked(PaymentMethod paymentMethod, int position);

        void onEditPaymentMethod(PaymentMethod paymentMethod, int position);

        void onDeletePaymentMethod(PaymentMethod paymentMethod, int position);

        void onSetDefaultPaymentMethod(PaymentMethod paymentMethod, int position);
    }

    static class PaymentMethodViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPaymentIcon;
        TextView tvPaymentName;
        TextView tvPaymentDetails;
        TextView tvDefaultLabel;
        MaterialButton btnEdit;
        MaterialButton btnDelete;
        MaterialButton btnSetDefault;

        public PaymentMethodViewHolder(@NonNull View itemView) {
            super(itemView);

            ivPaymentIcon = itemView.findViewById(R.id.ivPaymentIcon);
            tvPaymentName = itemView.findViewById(R.id.tvPaymentName);
            tvPaymentDetails = itemView.findViewById(R.id.tvPaymentDetails);
            tvDefaultLabel = itemView.findViewById(R.id.tvDefaultLabel);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnSetDefault = itemView.findViewById(R.id.btnSetDefault);
        }
    }
}
