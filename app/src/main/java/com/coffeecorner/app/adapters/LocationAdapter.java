package com.coffeecorner.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coffeecorner.app.R;
import com.coffeecorner.app.models.Location;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    private Context context;
    private List<Location> locations;

    public LocationAdapter(Context context, List<Location> locations) {
        this.context = context;
        this.locations = locations;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        Location location = locations.get(position);

        holder.tvLocationName.setText(location.getName());
        holder.tvAddress.setText(location.getAddress());
        holder.tvHours.setText(location.getHours());

        // Set image (would typically use image loading library like Glide or Picasso)
        // Glide.with(context).load(R.drawable.location_placeholder).into(holder.ivLocation);

        // Set up click listeners for buttons
        holder.btnDirections.setOnClickListener(v -> openMapsForDirection(location.getCoordinates()));
        holder.btnCall.setOnClickListener(v -> callStore(location.getPhone()));
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    private void openMapsForDirection(String coordinates) {
        if (coordinates != null && !coordinates.isEmpty()) {
            Uri gmmIntentUri = Uri.parse("geo:" + coordinates + "?q=" + coordinates);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(mapIntent);
            }
        }
    }

    private void callStore(String phoneNumber) {
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            context.startActivity(intent);
        }
    }

    static class LocationViewHolder extends RecyclerView.ViewHolder {
        ImageView ivLocation;
        TextView tvLocationName;
        TextView tvAddress;
        TextView tvHours;
        MaterialButton btnDirections;
        MaterialButton btnCall;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);

            ivLocation = itemView.findViewById(R.id.ivLocation);
            tvLocationName = itemView.findViewById(R.id.tvLocationName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvHours = itemView.findViewById(R.id.tvHours);
            btnDirections = itemView.findViewById(R.id.btnDirections);
            btnCall = itemView.findViewById(R.id.btnCall);
        }
    }
}
