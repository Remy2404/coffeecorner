package com.coffeecorner.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.coffeecorner.app.R;
import com.coffeecorner.app.network.ApiResponse;
import com.coffeecorner.app.models.User;
import com.coffeecorner.app.repositories.UserRepository;
import com.coffeecorner.app.viewmodel.LoginViewModel;
import com.coffeecorner.app.viewmodel.ViewModelFactory;

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
    private ImageButton btnGoogleLogin;
    private ImageButton btnFacebookLogin;
    private ImageButton btnTwitterLogin;
    private ImageButton btnPinterestLogin;

    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        initViews();

        // Initialize ViewModel
        UserRepository userRepository = UserRepository.getInstance(this);
        loginViewModel = new ViewModelProvider(this, new ViewModelFactory(userRepository)).get(LoginViewModel.class);

        // Set up click listeners
        setClickListeners();

        // Observe login result
        observeLoginResult();
    }    /**
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
        findViewById(R.id.cbRememberPassword);
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
        btnLogin.setOnClickListener(v -> {
            if (validateInputs()) {
                performLogin();
            }
        });

        // Sign Up button click
        btnSignUp.setOnClickListener(v -> navigateToRegister()); // Forgot password click
        tvForgotPassword.setOnClickListener(v -> {
            Intent forgotPasswordIntent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(forgotPasswordIntent);
        }); // Social login buttons
        btnGoogleLogin.setOnClickListener(v -> {
            // Implement Google Sign In
            performGoogleSignIn();
        });

        btnFacebookLogin.setOnClickListener(v -> {
            // Implement Facebook Sign In
            performFacebookSignIn();
        });

        btnTwitterLogin.setOnClickListener(v -> {
            // Implement Twitter Sign In
            performTwitterSignIn();
        });

        btnPinterestLogin.setOnClickListener(v -> {
            // Implement Pinterest Sign In
            performPinterestSignIn();
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
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Call login method in ViewModel
        loginViewModel.login(email, password);
    }

    /**
     * Observe the login result from the ViewModel
     */
    private void observeLoginResult() {
        loginViewModel.getLoginResult().observe(this, new Observer<ApiResponse<User>>() {
            @Override
            public void onChanged(ApiResponse<User> apiResponse) {
                if (apiResponse != null) {
                    if (apiResponse.isSuccess()) {
                        // Login successful
                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        navigateToMainActivity();
                    } else {
                        // Login failed
                        String errorMessage = apiResponse.getMessage() != null ? apiResponse.getMessage()
                                : "Login failed.";
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle null response if necessary
                    Toast.makeText(LoginActivity.this, "Login failed: Unknown error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Navigate to the registration screen
     */
    private void navigateToRegister() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
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

    /**
     * Perform Google Sign In
     */
    private void performGoogleSignIn() {
        // In a real app, this would integrate with Google Sign In API
        Toast.makeText(LoginActivity.this, "Google Sign In integration pending", Toast.LENGTH_SHORT).show();

        // For demonstration purposes, simulate successful login
        Toast.makeText(LoginActivity.this, "Login successful with Google", Toast.LENGTH_SHORT).show();
        navigateToMainActivity();
    }

    /**
     * Perform Facebook Sign In
     */
    private void performFacebookSignIn() {
        // In a real app, this would integrate with Facebook Login SDK
        Toast.makeText(LoginActivity.this, "Facebook Sign In integration pending", Toast.LENGTH_SHORT).show();

        // For demonstration purposes, simulate successful login
        Toast.makeText(LoginActivity.this, "Login successful with Facebook", Toast.LENGTH_SHORT).show();
        navigateToMainActivity();
    }

    /**
     * Perform Twitter Sign In
     */
    private void performTwitterSignIn() {
        // In a real app, this would integrate with Twitter Login SDK
        Toast.makeText(LoginActivity.this, "Twitter Sign In integration pending", Toast.LENGTH_SHORT).show();

        // For demonstration purposes, simulate successful login
        Toast.makeText(LoginActivity.this, "Login successful with Twitter", Toast.LENGTH_SHORT).show();
        navigateToMainActivity();
    }

    /**
     * Perform Pinterest Sign In
     */
    private void performPinterestSignIn() {
        // In a real app, this would integrate with Pinterest SDK
        Toast.makeText(LoginActivity.this, "Pinterest Sign In integration pending", Toast.LENGTH_SHORT).show();

        // For demonstration purposes, simulate successful login
        Toast.makeText(LoginActivity.this, "Login successful with Pinterest", Toast.LENGTH_SHORT).show();
        navigateToMainActivity();
    }
}