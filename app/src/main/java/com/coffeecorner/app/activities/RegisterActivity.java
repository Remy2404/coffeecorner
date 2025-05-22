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
import androidx.lifecycle.ViewModelProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.coffeecorner.app.R;
import com.coffeecorner.app.models.User;
import com.coffeecorner.app.repositories.UserRepository;
import com.coffeecorner.app.viewmodel.UserViewModel;
import com.coffeecorner.app.viewmodel.ViewModelFactory;

public class RegisterActivity extends AppCompatActivity {
    private EditText etFullName, etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvLoginLink;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        // Initialize ViewModel using ViewModelFactory
        UserRepository userRepository = UserRepository.getInstance(getApplicationContext());
        ViewModelFactory factory = new ViewModelFactory(userRepository);
        userViewModel = new ViewModelProvider(this, factory).get(UserViewModel.class);

        setupToolbar();
        initializeViews();
        setupListeners();
        observeViewModel();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
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

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> registerUser());

        tvLoginLink.setOnClickListener(v -> {
            // Navigate to LoginActivity
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void observeViewModel() {
        userViewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            btnRegister.setEnabled(!isLoading);
        });

        userViewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                userViewModel.getErrorMessage().getValue(); // Consume the error
            }
        });

        userViewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
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

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String uid = firebaseUser.getUid();
                            userViewModel.register(fullName, email, password, "");
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        btnRegister.setEnabled(true);
                        String errorMessage = task.getException() != null ? task.getException().getMessage()
                                : "Registration failed";
                        Toast.makeText(RegisterActivity.this,
                                "Registration failed: " + errorMessage,
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
