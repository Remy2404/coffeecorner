package com.coffeecorner.app.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coffeecorner.app.R;
import com.coffeecorner.app.adapters.PaymentMethodAdapter;
import com.coffeecorner.app.models.PaymentMethod;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class PaymentMethodsFragment extends Fragment implements PaymentMethodAdapter.OnPaymentMethodActionListener {

    private RecyclerView recyclerViewPaymentMethods;
    private LinearLayout layoutEmptyPaymentMethods;
    private FloatingActionButton fabAddPaymentMethod;
    private PaymentMethodAdapter paymentMethodAdapter;
    private List<PaymentMethod> paymentMethods;

    public PaymentMethodsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payment_methods, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupRecyclerView();
        loadPaymentMethods();
        updateUI();
    }

    private void initializeViews(View view) {
        // Set up the back button
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        }
        recyclerViewPaymentMethods = view.findViewById(R.id.rvPaymentMethods);
        layoutEmptyPaymentMethods = view.findViewById(R.id.layoutNoPaymentMethods);
        fabAddPaymentMethod = view.findViewById(R.id.fabAddPaymentMethod);

        fabAddPaymentMethod.setOnClickListener(v -> showAddPaymentMethodDialog());
    }

    private void setupRecyclerView() {
        paymentMethods = new ArrayList<>();
        paymentMethodAdapter = new PaymentMethodAdapter(paymentMethods, this);
        recyclerViewPaymentMethods.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewPaymentMethods.setAdapter(paymentMethodAdapter);
    }

    private void loadPaymentMethods() {
        // Sample payment methods - replace with actual data loading from database/API
        paymentMethods.clear();

        // Add some sample payment methods for demo
        paymentMethods.add(new PaymentMethod("1", "Visa", "**** **** **** 1234", "12/25", true));
        paymentMethods.add(new PaymentMethod("2", "MasterCard", "**** **** **** 5678", "08/26", false));
        paymentMethods.add(new PaymentMethod("3", "PayPal", "user@email.com", "", false));

        paymentMethodAdapter.notifyDataSetChanged();
    }

    private void updateUI() {
        if (paymentMethods.isEmpty()) {
            recyclerViewPaymentMethods.setVisibility(View.GONE);
            layoutEmptyPaymentMethods.setVisibility(View.VISIBLE);
        } else {
            recyclerViewPaymentMethods.setVisibility(View.VISIBLE);
            layoutEmptyPaymentMethods.setVisibility(View.GONE);
        }
    }

    private void showAddPaymentMethodDialog() {
        String[] options = { "Credit/Debit Card", "PayPal", "Google Pay", "Apple Pay" };

        new AlertDialog.Builder(requireContext())
                .setTitle("Add Payment Method")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            showAddCardDialog();
                            break;
                        case 1:
                            showAddPayPalDialog();
                            break;
                        case 2:
                            Toast.makeText(requireContext(), "Google Pay integration coming soon", Toast.LENGTH_SHORT)
                                    .show();
                            break;
                        case 3:
                            Toast.makeText(requireContext(), "Apple Pay integration coming soon", Toast.LENGTH_SHORT)
                                    .show();
                            break;
                    }
                })
                .show();
    }

    private void showAddCardDialog() {
        // Create a simple form dialog for adding a card
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_card, null);

        builder.setView(dialogView)
                .setTitle("Add Credit/Debit Card")
                .setPositiveButton("Add", (dialog, which) -> {
                    // Add new card logic here
                    String newId = String.valueOf(System.currentTimeMillis());
                    PaymentMethod newCard = new PaymentMethod(newId, "Visa", "**** **** **** 9999", "12/28", false);
                    paymentMethods.add(newCard);
                    paymentMethodAdapter.notifyItemInserted(paymentMethods.size() - 1);
                    updateUI();
                    Toast.makeText(requireContext(), "Card added successfully", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showAddPayPalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_paypal, null);

        builder.setView(dialogView)
                .setTitle("Add PayPal Account")
                .setPositiveButton("Add", (dialog, which) -> {
                    // Add new PayPal account logic here
                    String newId = String.valueOf(System.currentTimeMillis());
                    PaymentMethod newPayPal = new PaymentMethod(newId, "PayPal", "newuser@email.com", "", false);
                    paymentMethods.add(newPayPal);
                    paymentMethodAdapter.notifyItemInserted(paymentMethods.size() - 1);
                    updateUI();
                    Toast.makeText(requireContext(), "PayPal account added successfully", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onPaymentMethodClicked(PaymentMethod paymentMethod, int position) {
        Toast.makeText(requireContext(), "Selected: " + paymentMethod.getType(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditPaymentMethod(PaymentMethod paymentMethod, int position) {
        Toast.makeText(requireContext(), "Edit: " + paymentMethod.getType(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeletePaymentMethod(PaymentMethod paymentMethod, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Payment Method")
                .setMessage("Are you sure you want to delete this payment method?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    paymentMethods.remove(position);
                    paymentMethodAdapter.notifyItemRemoved(position);
                    updateUI();
                    Toast.makeText(requireContext(), "Payment method deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onSetDefaultPaymentMethod(PaymentMethod paymentMethod, int position) {
        for (PaymentMethod pm : paymentMethods) {
            pm.setDefault(false);
        }
        paymentMethod.setDefault(true);
        paymentMethodAdapter.notifyDataSetChanged();
        Toast.makeText(requireContext(), paymentMethod.getType() + " set as default", Toast.LENGTH_SHORT).show();
    }
}
