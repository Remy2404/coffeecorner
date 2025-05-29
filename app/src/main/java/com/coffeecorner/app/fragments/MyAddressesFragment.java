package com.coffeecorner.app.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coffeecorner.app.R;
import com.coffeecorner.app.adapters.AddressAdapter;
import com.coffeecorner.app.models.Address;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MyAddressesFragment extends Fragment implements AddressAdapter.OnAddressActionListener {

    private RecyclerView recyclerViewAddresses;
    private LinearLayout layoutEmptyAddresses;
    private FloatingActionButton fabAddAddress;
    private AddressAdapter addressAdapter;
    private List<Address> addresses;

    public MyAddressesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_addresses, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupRecyclerView();
        loadAddresses();
        updateUI();
    }

    private void initializeViews(View view) { // Set up the back button
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        }

        recyclerViewAddresses = view.findViewById(R.id.recyclerViewAddresses);
        layoutEmptyAddresses = view.findViewById(R.id.layoutEmptyAddresses);
        fabAddAddress = view.findViewById(R.id.fabAddAddress);

        fabAddAddress.setOnClickListener(v -> showAddAddressDialog());
    }

    private void setupRecyclerView() {
        addresses = new ArrayList<>();
        addressAdapter = new AddressAdapter(addresses, this);
        recyclerViewAddresses.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAddresses.setAdapter(addressAdapter);
    }

    private void loadAddresses() {
        // Sample addresses - replace with actual data loading from database/API
        addresses.clear();

        // Add some sample addresses for demo
        addresses.add(new Address("1", "Home", "123 Main Street", "Apt 4B", "New York", "NY", "10001", "USA", true));
        addresses.add(
                new Address("2", "Work", "456 Business Ave", "Suite 100", "New York", "NY", "10002", "USA", false));
        addresses.add(new Address("3", "Other", "789 Friend Street", "", "Brooklyn", "NY", "11201", "USA", false));

        addressAdapter.notifyDataSetChanged();
    }

    private void updateUI() {
        if (addresses.isEmpty()) {
            recyclerViewAddresses.setVisibility(View.GONE);
            layoutEmptyAddresses.setVisibility(View.VISIBLE);
        } else {
            recyclerViewAddresses.setVisibility(View.VISIBLE);
            layoutEmptyAddresses.setVisibility(View.GONE);
        }
    }

    private void showAddAddressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_address, null);

        // Get references to dialog views
        EditText etAddressLine1 = dialogView.findViewById(R.id.etAddressLine1);
        EditText etAddressLine2 = dialogView.findViewById(R.id.etAddressLine2);
        EditText etCity = dialogView.findViewById(R.id.etCity);
        EditText etState = dialogView.findViewById(R.id.etState);
        EditText etZipCode = dialogView.findViewById(R.id.etZipCode);
        Spinner spinnerAddressType = dialogView.findViewById(R.id.spinnerAddressType);

        builder.setView(dialogView)
                .setTitle("Add New Address")
                .setPositiveButton("Add", (dialog, which) -> {
                    String addressLine1 = etAddressLine1.getText().toString().trim();
                    String addressLine2 = etAddressLine2.getText().toString().trim();
                    String city = etCity.getText().toString().trim();
                    String state = etState.getText().toString().trim();
                    String zipCode = etZipCode.getText().toString().trim();
                    String addressType = spinnerAddressType.getSelectedItem().toString();

                    if (validateAddressInput(addressLine1, city, state, zipCode)) {
                        String newId = String.valueOf(System.currentTimeMillis());
                        boolean isDefault = addresses.isEmpty(); // First address becomes default

                        Address newAddress = new Address(newId, addressType, addressLine1, addressLine2,
                                city, state, zipCode, "USA", isDefault);
                        addresses.add(newAddress);
                        addressAdapter.notifyItemInserted(addresses.size() - 1);
                        updateUI();
                        Toast.makeText(requireContext(), "Address added successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private boolean validateAddressInput(String addressLine1, String city, String state, String zipCode) {
        if (addressLine1.isEmpty()) {
            Toast.makeText(requireContext(), "Address line 1 is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (city.isEmpty()) {
            Toast.makeText(requireContext(), "City is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (state.isEmpty()) {
            Toast.makeText(requireContext(), "State is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (zipCode.isEmpty()) {
            Toast.makeText(requireContext(), "Zip code is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showEditAddressDialog(Address address, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_address, null);

        // Get references to dialog views and populate with existing data
        EditText etAddressLine1 = dialogView.findViewById(R.id.etAddressLine1);
        EditText etAddressLine2 = dialogView.findViewById(R.id.etAddressLine2);
        EditText etCity = dialogView.findViewById(R.id.etCity);
        EditText etState = dialogView.findViewById(R.id.etState);
        EditText etZipCode = dialogView.findViewById(R.id.etZipCode);
        Spinner spinnerAddressType = dialogView.findViewById(R.id.spinnerAddressType);

        // Populate fields with existing data
        etAddressLine1.setText(address.getAddressLine1());
        etAddressLine2.setText(address.getAddressLine2());
        etCity.setText(address.getCity());
        etState.setText(address.getState());
        etZipCode.setText(address.getZipCode());

        // Set spinner selection based on address type
        for (int i = 0; i < spinnerAddressType.getCount(); i++) {
            if (spinnerAddressType.getItemAtPosition(i).toString().equals(address.getType())) {
                spinnerAddressType.setSelection(i);
                break;
            }
        }

        builder.setView(dialogView)
                .setTitle("Edit Address")
                .setPositiveButton("Update", (dialog, which) -> {
                    String addressLine1 = etAddressLine1.getText().toString().trim();
                    String addressLine2 = etAddressLine2.getText().toString().trim();
                    String city = etCity.getText().toString().trim();
                    String state = etState.getText().toString().trim();
                    String zipCode = etZipCode.getText().toString().trim();
                    String addressType = spinnerAddressType.getSelectedItem().toString();

                    if (validateAddressInput(addressLine1, city, state, zipCode)) {
                        address.setType(addressType);
                        address.setAddressLine1(addressLine1);
                        address.setAddressLine2(addressLine2);
                        address.setCity(city);
                        address.setState(state);
                        address.setZipCode(zipCode);

                        addressAdapter.notifyDataSetChanged();
                        Toast.makeText(requireContext(), "Address updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteConfirmationDialog(Address address, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Address")
                .setMessage("Are you sure you want to delete this address?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    addresses.remove(position);
                    addressAdapter.notifyItemRemoved(position);
                    updateUI();
                    Toast.makeText(requireContext(), "Address deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onAddressClicked(Address address, int position) {
        Toast.makeText(requireContext(), "Selected: " + address.getType() + " address", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditAddress(Address address, int position) {
        showEditAddressDialog(address, position);
    }

    @Override
    public void onDeleteAddress(Address address, int position) {
        showDeleteConfirmationDialog(address, position);
    }

    @Override
    public void onSetDefaultAddress(Address address, int position) {
        for (Address addr : addresses) {
            addr.setDefault(false);
        }
        address.setDefault(true);
        addressAdapter.notifyDataSetChanged();
        Toast.makeText(requireContext(), "Default address updated", Toast.LENGTH_SHORT).show();
    }
}
