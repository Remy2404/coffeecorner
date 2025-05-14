package com.coffeecorner.app.activities;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
// import com.google.firebase.auth.FirebaseAuth;
import com.coffeecorner.app.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etEmail;
    private Button btnResetPassword;
    // private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // mAuth = FirebaseAuth.getInstance(); // Initialize Firebase Auth

        setupToolbar();
        initializeViews();
        setupListeners();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> onBackPressed());
        }
    }

    private void initializeViews() {
        etEmail = findViewById(R.id.etEmail); // Ensure this ID exists in your layout
        btnResetPassword = findViewById(R.id.btnResetPassword);
    }

    private void setupListeners() {
        btnResetPassword.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (validateEmail(email)) {
                sendPasswordResetEmail(email);
            }
        });
    }

    private boolean validateEmail(String email) {
        if (email.isEmpty()) {
            etEmail.setError("Email is required.");
            etEmail.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email address.");
            etEmail.requestFocus();
            return false;
        }
        return true;
    }

    private void sendPasswordResetEmail(String email) {
        // mAuth.sendPasswordResetEmail(email)
        // .addOnCompleteListener(task -> {
        // if (task.isSuccessful()) {
        // Toast.makeText(ForgotPasswordActivity.this, "Password reset email sent.",
        // Toast.LENGTH_LONG).show();
        // // Optionally, navigate back to login or show a success message
        // } else {
        // Toast.makeText(ForgotPasswordActivity.this, "Failed to send reset email: " +
        // task.getException().getMessage(), Toast.LENGTH_LONG).show();
        // }
        // });
        Toast.makeText(this, "Sending password reset email (Firebase integration pending)...", Toast.LENGTH_SHORT)
                .show();
    }
}
