/**
 * WelcomeActivity - The First Screen You See
 * 
 * This is like the front door of our app. When you open it, you can:
 * - Log in if you already have an account
 * - Create a new account if you're new
 * - Look at recipes without logging in
 * 
 * It shows a friendly welcome message and big, easy-to-find buttons
 * for each option.
 */
package com.example.recipe_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {
    // The text that shows the welcome message
    private TextView welcomeText;
    // Button for existing users to log in
    private Button loginButton;
    // Button for new users to create an account
    private Button registerButton;
    // Button to use the app without an account
    private Button continueWithoutLoginButton;

    /**
     * Sets up the welcome screen when it first opens
     * 
     * This method:
     * - Shows the welcome message
     * - Sets up the login button
     * - Sets up the register button
     * - Sets up the continue without login button
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Get user's name from intent
        String userName = getIntent().getStringExtra("USER_NAME");

        // Initialize views
        welcomeText = findViewById(R.id.welcomeText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        continueWithoutLoginButton = findViewById(R.id.continueWithoutLoginButton);

        // Set welcome message
        welcomeText.setText(getString(R.string.welcome_subtitle));

        // Set up login button click listener
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // Set up register button click listener
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Set up continue without login button click listener
        continueWithoutLoginButton.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, RecipeRecommendationActivity.class);
            intent.putExtra("USER_NAME", "Guest");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
