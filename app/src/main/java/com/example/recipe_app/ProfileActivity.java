package com.example.recipe_app;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.SharedPreferences;
import android.widget.Toast;

public class ProfileActivity extends AppCompatActivity {
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText bioEditText;
    private ImageView profileImage;
    private Button saveButton;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Profile");
        }

        // Initialize views
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        bioEditText = findViewById(R.id.bioEditText);
        profileImage = findViewById(R.id.profileImage);
        saveButton = findViewById(R.id.saveButton);

        // Load saved profile data
        preferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
        loadProfileData();

        // Setup save button
        saveButton.setOnClickListener(v -> saveProfileData());
    }

    private void loadProfileData() {
        nameEditText.setText(preferences.getString("name", ""));
        emailEditText.setText(preferences.getString("email", ""));
        bioEditText.setText(preferences.getString("bio", ""));
    }

    private void saveProfileData() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("name", nameEditText.getText().toString());
        editor.putString("email", emailEditText.getText().toString());
        editor.putString("bio", bioEditText.getText().toString());
        editor.apply();

        Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
