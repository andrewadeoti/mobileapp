/**
 * LoginActivity handles user authentication.
 * It provides a form for users to enter their email and password,
 * and includes functionality for password recovery.
 */
package com.example.recipe_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    // UI Components
    private TextInputEditText emailInput;        // Input field for email
    private TextInputEditText passwordInput;     // Input field for password
    private Button loginButton;                  // Button to submit login
    private TextView forgotPasswordLink;         // Link to password recovery

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI components
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        forgotPasswordLink = findViewById(R.id.forgotPasswordLink);
        TextView registerLink = findViewById(R.id.registerLink);

        // Set up click listeners
        loginButton.setOnClickListener(v -> loginUser());
        forgotPasswordLink.setOnClickListener(v -> {
            // Navigate to password recovery screen
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
        registerLink.setOnClickListener(v -> {
            // Navigate to registration screen
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Handles the login process.
     * Validates user input and performs authentication.
     */
    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Basic validation
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable login button to prevent multiple clicks
        loginButton.setEnabled(false);

        // Show loading indicator
        // progressBar.setVisibility(View.VISIBLE);

        // Use Firebase Authentication
        FirebaseManager.getInstance().loginUser(email, password, new FirebaseManager.FirebaseCallback<FirebaseUser>() {
            @Override
            public void onSuccess(FirebaseUser user) {
                // Hide loading indicator
                // progressBar.setVisibility(View.GONE);
                
                // Show success message
                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                
                // Navigate to recipe recommendation screen
                Intent intent = new Intent(LoginActivity.this, RecipeRecommendationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                
                // Close this activity
                finish();
            }
            
            @Override
            public void onFailure(Exception e) {
                // Hide loading indicator
                // progressBar.setVisibility(View.GONE);
                
                // Re-enable login button
                loginButton.setEnabled(true);
                
                // Show error message
                Toast.makeText(LoginActivity.this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}