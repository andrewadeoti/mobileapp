package com.example.recipe_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    // UI Components
    private TextInputEditText nameInput;        // Input field for name
    private TextInputEditText emailInput;       // Input field for email
    private TextInputEditText passwordInput;    // Input field for password
    private TextInputEditText confirmPasswordInput; // Input field for password confirmation
    private Button registerButton;              // Button to submit registration
    private TextView loginLink;                 // Link to login screen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize UI components
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        registerButton = findViewById(R.id.registerButton);
        loginLink = findViewById(R.id.loginLink);

        // Set up click listeners
        registerButton.setOnClickListener(v -> registerUser());
        loginLink.setOnClickListener(v -> {
            // Navigate to login screen
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Handles the registration process.
     * Validates user input and creates a new account.
     */
    private void registerUser() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        // Basic validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable button to prevent multiple clicks
        registerButton.setEnabled(false);

        // Show loading indicator
        // progressBar.setVisibility(View.VISIBLE);

        // Use Firebase for registration
        FirebaseManager.getInstance().registerUser(email, password, new FirebaseManager.FirebaseCallback<FirebaseUser>() {
            @Override
            public void onSuccess(FirebaseUser user) {
                // Save additional user data
                Map<String, Object> userData = new HashMap<>();
                userData.put("name", name);
                userData.put("email", email);
                userData.put("createdAt", System.currentTimeMillis());

                FirebaseManager.getInstance().saveUserProfile(userData, new FirebaseManager.FirebaseCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        // Hide loading indicator
                        // progressBar.setVisibility(View.GONE);
                        
                        // Show success message
                        Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                        
                        // Navigate to login screen
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    
                    @Override
                    public void onFailure(Exception e) {
                        // Hide loading indicator
                        // progressBar.setVisibility(View.GONE);
                        
                        // Re-enable register button
                        registerButton.setEnabled(true);
                        
                        // Show error message
                        Toast.makeText(RegisterActivity.this, "Error saving user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            
            @Override
            public void onFailure(Exception e) {
                // Hide loading indicator
                // progressBar.setVisibility(View.GONE);
                
                // Re-enable register button
                registerButton.setEnabled(true);
                
                // Show error message
                Toast.makeText(RegisterActivity.this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
