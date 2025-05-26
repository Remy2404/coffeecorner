package com.coffeecorner.app.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.coffeecorner.app.R;

public class AboutUsFragment extends Fragment {

    private TextView tvCompanyName;
    private TextView tvCompanyDescription;
    private ImageButton btnFacebook;
    private ImageButton btnInstagram;
    private ImageButton btnTwitter;

    public AboutUsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about_us, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupClickListeners();
        loadCompanyData();
    }

    private void initViews(View view) {
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        tvCompanyName = view.findViewById(R.id.tvCompanyName);
        tvCompanyDescription = view.findViewById(R.id.tvOurStory);
        btnFacebook = view.findViewById(R.id.btnFacebook);
        btnInstagram = view.findViewById(R.id.btnInstagram);
        btnTwitter = view.findViewById(R.id.btnTwitter);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        }
    }

    private void setupClickListeners() {
        // Social media buttons
        if (btnFacebook != null) {
            btnFacebook.setOnClickListener(v -> openUrl("https://www.facebook.com/coffeecorner"));
        }

        if (btnInstagram != null) {
            btnInstagram.setOnClickListener(v -> openUrl("https://www.instagram.com/coffeecorner"));
        }

        if (btnTwitter != null) {
            btnTwitter.setOnClickListener(v -> openUrl("https://www.twitter.com/coffeecorner"));
        }
    }

    private void loadCompanyData() {
        // Company basic information
        if (tvCompanyName != null) {
            tvCompanyName.setText("Coffee Corner");
        }

        if (tvCompanyDescription != null) {
            tvCompanyDescription.setText("Premium coffee experience since 2010. We are dedicated to serving " +
                    "the finest quality coffee while creating a warm and welcoming environment for our customers. " +
                    "From sourcing the best beans to crafting the perfect brew, every cup tells a story of " +
                    "passion and excellence.");
        }
    }

    private void openUrl(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Unable to open link", Toast.LENGTH_SHORT).show();
        }
    }

    public void refreshData() {
        loadCompanyData();
    }
}
