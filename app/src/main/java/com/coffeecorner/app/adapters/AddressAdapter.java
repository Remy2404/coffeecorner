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
import com.coffeecorner.app.models.Address;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private Context context;
    private List<Address> addresses;
    private OnAddressActionListener listener;

    public AddressAdapter(List<Address> addresses, OnAddressActionListener listener) {
        this.addresses = addresses;
        this.listener = listener;
    }

    public AddressAdapter(Context context, List<Address> addresses) {
        this.context = context;
        this.addresses = addresses;
    }

    public void setOnAddressActionListener(OnAddressActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Address address = addresses.get(position);

        holder.tvAddressTitle.setText(address.getTitle());
        holder.tvAddressDetails.setText(address.getFullAddress());
        holder.tvAddressType.setText(address.getType());

        // Set icon based on address type
        int iconResource = getIconForAddressType(address.getType());
        holder.ivAddressIcon.setImageResource(iconResource);

        if (address.isDefault()) {
            holder.tvDefaultLabel.setVisibility(View.VISIBLE);
            holder.btnSetDefault.setVisibility(View.GONE);
        } else {
            holder.tvDefaultLabel.setVisibility(View.GONE);
            holder.btnSetDefault.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddressClicked(address, position);
            }
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditAddress(address, position);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteAddress(address, position);
            }
        });

        holder.btnSetDefault.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSetDefaultAddress(address, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    public void updateAddresses(List<Address> newAddresses) {
        this.addresses = newAddresses;
        notifyDataSetChanged();
    }

    private int getIconForAddressType(String type) {
        switch (type.toLowerCase()) {
            case "home":
                return R.drawable.ic_home;
            case "work":
                return R.drawable.ic_work;
            case "other":
            default:
                return R.drawable.ic_location_on;
        }
    }

    public interface OnAddressActionListener {
        void onAddressClicked(Address address, int position);

        void onEditAddress(Address address, int position);

        void onDeleteAddress(Address address, int position);

        void onSetDefaultAddress(Address address, int position);
    }

    static class AddressViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAddressIcon;
        TextView tvAddressTitle;
        TextView tvAddressDetails;
        TextView tvAddressType;
        TextView tvDefaultLabel;
        MaterialButton btnEdit;
        MaterialButton btnDelete;
        MaterialButton btnSetDefault;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);

            ivAddressIcon = itemView.findViewById(R.id.ivAddressIcon);
            tvAddressTitle = itemView.findViewById(R.id.tvAddressTitle);
            tvAddressDetails = itemView.findViewById(R.id.tvAddressDetails);
            tvAddressType = itemView.findViewById(R.id.tvAddressType);
            tvDefaultLabel = itemView.findViewById(R.id.tvDefaultLabel);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnSetDefault = itemView.findViewById(R.id.btnSetDefault);
        }
    }
}
