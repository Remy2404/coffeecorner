package com.example.apsaraandroid;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

/**
 * LoginActivity - Handles user authentication
 * Provides email/password login and social login options
 */
public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private EditText etEmail;
    private EditText etPassword;
    private MaterialButton btnLogin;
    private MaterialButton btnSignUp;
    private TextView tvForgotPassword;
    private CheckBox cbRememberPassword;
    private ImageButton btnGoogleLogin;
    private ImageButton btnFacebookLogin;
    private ImageButton btnTwitterLogin;
    private ImageButton btnPinterestLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        initViews();

        // Set up click listeners
        setClickListeners();
    }

    /**
     * Initialize all UI elements
     */
    private void initViews() {
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        cbRememberPassword = findViewById(R.id.cbRememberPassword);
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin);
        btnFacebookLogin = findViewById(R.id.btnFacebookLogin);
        btnTwitterLogin = findViewById(R.id.btnTwitterLogin);
        btnPinterestLogin = findViewById(R.id.btnPinterestLogin);
    }

    /**
     * Set up click listeners for buttons and clickable text
     */
    private void setClickListeners() {
        // Login button click
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    performLogin();
                }
            }
        });

        // Sign Up button click
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToRegister();
            }
        });

        // Forgot password click
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Navigate to forgot password screen
                Toast.makeText(LoginActivity.this, "Forgot password clicked", Toast.LENGTH_SHORT).show();
            }
        });

        // Social login buttons
        btnGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement Google Sign In
                Toast.makeText(LoginActivity.this, "Google login clicked", Toast.LENGTH_SHORT).show();
                // For demo purposes, directly navigate to main screen
                navigateToMainActivity();
            }
        });

        btnFacebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement Facebook Sign In
                Toast.makeText(LoginActivity.this, "Facebook login clicked", Toast.LENGTH_SHORT).show();
                // For demo purposes, directly navigate to main screen
                navigateToMainActivity();
            }
        });

        btnTwitterLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement Twitter Sign In
                Toast.makeText(LoginActivity.this, "Twitter login clicked", Toast.LENGTH_SHORT).show();
                // For demo purposes, directly navigate to main screen
                navigateToMainActivity();
            }
        });

        btnPinterestLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement Pinterest Sign In
                Toast.makeText(LoginActivity.this, "Pinterest login clicked", Toast.LENGTH_SHORT).show();
                // For demo purposes, directly navigate to main screen
                navigateToMainActivity();
            }
        });
    }

    /**
     * Validate email and password inputs
     * 
     * @return true if inputs are valid, false otherwise
     */
    private boolean validateInputs() {
        boolean isValid = true;

        // Validate email
        String email = etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Enter a valid email address");
            isValid = false;
        } else {
            tilEmail.setError(null);
        }

        // Validate password
        String password = etPassword.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            tilPassword.setError("Password must be at least 6 characters");
            isValid = false;
        } else {
            tilPassword.setError(null);
        }

        return isValid;
    }

    /**
     * Perform login authentication
     */
    private void performLogin() {
        // TODO: Implement actual authentication with backend
        // For demo purposes, hardcode a success case
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.equals("user@example.com") && password.equals("password")) {
            // Login successful
            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
            navigateToMainActivity();
        } else {
            // Login failed
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Navigate to the registration screen
     */
    private void navigateToRegister() {
        // TODO: Navigate to RegisterActivity when it's created
        Toast.makeText(this, "Register clicked - will implement later", Toast.LENGTH_SHORT).show();
    }

    /**
     * Navigate to the main activity after successful login
     */
    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        // Clear back stack so user can't go back to login screen with back button
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}