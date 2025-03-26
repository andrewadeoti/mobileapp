package com.example.recipe_app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;

public class UserProfileActivity extends AppCompatActivity {
    private ImageView profileImage;
    private TextView nameText;
    private TextView emailText;
    private MaterialButton editProfileButton;
    private MaterialButton preferencesButton;
    private MaterialButton logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        initializeViews();
        setupClickListeners();
        loadUserData();
    }

    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profileImage = findViewById(R.id.profileImage);
        nameText = findViewById(R.id.nameText);
        emailText = findViewById(R.id.emailText);
        editProfileButton = findViewById(R.id.editProfileButton);
        preferencesButton = findViewById(R.id.preferencesButton);
        logoutButton = findViewById(R.id.logoutButton);
    }

    private void setupClickListeners() {
        editProfileButton.setOnClickListener(v -> {
            // TODO: Navigate to EditProfileActivity
            Toast.makeText(this, "Edit profile clicked", Toast.LENGTH_SHORT).show();
        });

        preferencesButton.setOnClickListener(v -> {
            // TODO: Navigate to PreferencesActivity
            Toast.makeText(this, "Preferences clicked", Toast.LENGTH_SHORT).show();
        });

        logoutButton.setOnClickListener(v -> logoutUser());
    }

    private void loadUserData() {
        // TODO: Implement local storage for user data
        nameText.setText("User");
        emailText.setText("user@example.com");
    }

    private void logoutUser() {
        // TODO: Implement local logout functionality
        Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}