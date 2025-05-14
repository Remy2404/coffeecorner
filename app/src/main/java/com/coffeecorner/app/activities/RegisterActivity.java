package com.coffeecorner.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.coffeecorner.app.R;
import com.coffeecorner.app.models.User;
import com.coffeecorner.app.utils.SupabaseClientManager;

public class RegisterActivity extends AppCompatActivity {
    private EditText etFullName, etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvLoginLink;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

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
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvLoginLink);
        progressBar = findViewById(R.id.progressBar);
    }

    // private void initializeSupabase() {
    // String supabaseUrl = "YOUR_SUPABASE_URL";
    // String supabaseKey = "YOUR_SUPABASE_ANON_KEY";
    // supabaseClient = new SupabaseClient(supabaseUrl, supabaseKey, new
    // GoTrue.Config(), new PostgrestClient.Config());
    // }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> {
            registerUser();
        });

        tvLoginLink.setOnClickListener(v -> {
            // Navigate to LoginActivity
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private boolean validateInput(String fullName, String email, String password, String confirmPassword) {
        if (fullName.isEmpty()) {
            etFullName.setError("Full name is required.");
            etFullName.requestFocus();
            return false;
        }
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
        if (password.isEmpty()) {
            etPassword.setError("Password is required.");
            etPassword.requestFocus();
            return false;
        }
        if (password.length() < 6) { // Firebase default minimum
            etPassword.setError("Password should be at least 6 characters.");
            etPassword.requestFocus();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match.");
            etConfirmPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void registerUser() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (!validateInput(fullName, email, password, confirmPassword)) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String uid = firebaseUser.getUid();
                            saveUserToSupabase(uid, fullName, email);
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        btnRegister.setEnabled(true);
                        Toast.makeText(RegisterActivity.this,
                                "Registration failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserToSupabase(String uid, String fullName, String email) {
        User newUser = new User(uid, fullName, email, "", "");

        try {
            SupabaseClientManager.getInstance()
                    .from("users")
                    .insert(newUser)
                    .execute(response -> runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        btnRegister.setEnabled(true);

                        if (response.getError() == null) {
                            Toast.makeText(RegisterActivity.this,
                                    "Registration successful!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            // If Supabase save fails, delete the Firebase user
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                user.delete().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this,
                                                "Failed to save user data. Please try again.",
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    }));
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            btnRegister.setEnabled(true);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
