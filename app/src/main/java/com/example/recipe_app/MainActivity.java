/**
 * MainActivity is the entry point of the application.
 * It handles the initial user interaction where users enter their name
 * before proceeding to the main app flow.
 */
package com.example.recipe_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    // UI Components
    private EditText nameInput;      // Input field for user's name
    private Button continueButton;   // Button to proceed to next screen
    private Button skipButton;       // Button to skip registration
    private Button loginButton;      // Button to go to login

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        nameInput = findViewById(R.id.nameInput);
        continueButton = findViewById(R.id.continueButton);
        skipButton = findViewById(R.id.skipButton);
        loginButton = findViewById(R.id.loginButton);

        // Set up click listener for continue button (Register)
        continueButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            if (!name.isEmpty()) {
                // Create intent to navigate to RegisterActivity
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                intent.putExtra("USER_NAME", name);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up click listener for skip button
        skipButton.setOnClickListener(v -> {
            try {
                // Navigate directly to RecipeRecommendationActivity
                Intent intent = new Intent(MainActivity.this, RecipeRecommendationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Set up click listener for login button
        loginButton.setOnClickListener(v -> {
            try {
                // Navigate to LoginActivity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}