package com.example.recipe_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;

public class SettingsActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "AppSettings";
    
    // UI Components
    private SwitchMaterial darkModeSwitch;
    private SwitchMaterial metricUnitsSwitch;
    private SwitchMaterial notificationsSwitch;
    private SwitchMaterial biometricLoginSwitch;
    private SwitchMaterial twoFactorAuthSwitch;
    private SwitchMaterial offlineAccessSwitch;
    private Spinner languageSpinner;
    private Spinner notificationFrequencySpinner;
    private MaterialButton editProfileButton;
    private MaterialButton changePasswordButton;
    private MaterialButton clearCacheButton;
    private MaterialButton logoutButton;
    private MaterialButton deleteAccountButton;
    private TextView appVersionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Settings");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize views
        initializeViews();
        setupSpinners();
        loadSettings();
        setupClickListeners();
    }

    private void initializeViews() {
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        metricUnitsSwitch = findViewById(R.id.metricUnitsSwitch);
        notificationsSwitch = findViewById(R.id.notificationsSwitch);
        biometricLoginSwitch = findViewById(R.id.biometricLoginSwitch);
        twoFactorAuthSwitch = findViewById(R.id.twoFactorAuthSwitch);
        offlineAccessSwitch = findViewById(R.id.offlineAccessSwitch);
        languageSpinner = findViewById(R.id.languageSpinner);
        notificationFrequencySpinner = findViewById(R.id.notificationFrequencySpinner);
        editProfileButton = findViewById(R.id.editProfileButton);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        clearCacheButton = findViewById(R.id.clearCacheButton);
        logoutButton = findViewById(R.id.logoutButton);
        deleteAccountButton = findViewById(R.id.deleteAccountButton);
        appVersionText = findViewById(R.id.appVersionText);

        // Set app version using PackageInfo
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            appVersionText.setText("Version " + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            appVersionText.setText("Version Unknown");
        }
    }

    private void setupSpinners() {
        // Setup language spinner
        ArrayAdapter<CharSequence> languageAdapter = ArrayAdapter.createFromResource(this,
                R.array.languages, android.R.layout.simple_spinner_item);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(languageAdapter);

        // Setup notification frequency spinner
        ArrayAdapter<CharSequence> frequencyAdapter = ArrayAdapter.createFromResource(this,
                R.array.notification_frequencies, android.R.layout.simple_spinner_item);
        frequencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        notificationFrequencySpinner.setAdapter(frequencyAdapter);
    }

    private void loadSettings() {
        // Load saved settings
        darkModeSwitch.setChecked(sharedPreferences.getBoolean("dark_mode", false));
        metricUnitsSwitch.setChecked(sharedPreferences.getBoolean("metric_units", true));
        notificationsSwitch.setChecked(sharedPreferences.getBoolean("notifications_enabled", true));
        biometricLoginSwitch.setChecked(sharedPreferences.getBoolean("biometric_login", false));
        twoFactorAuthSwitch.setChecked(sharedPreferences.getBoolean("two_factor_auth", false));
        offlineAccessSwitch.setChecked(sharedPreferences.getBoolean("offline_access", false));
        
        languageSpinner.setSelection(sharedPreferences.getInt("language_index", 0));
        notificationFrequencySpinner.setSelection(sharedPreferences.getInt("notification_frequency", 0));
    }

    private void setupClickListeners() {
        // Theme switch
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(isChecked ? 
                    AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        });

        // Units switch
        metricUnitsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("metric_units", isChecked).apply();
            Toast.makeText(this, "Measurement units updated", Toast.LENGTH_SHORT).show();
        });

        // Notifications switch
        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("notifications_enabled", isChecked).apply();
            notificationFrequencySpinner.setEnabled(isChecked);
            Toast.makeText(this, isChecked ? "Notifications enabled" : "Notifications disabled", 
                    Toast.LENGTH_SHORT).show();
        });

        // Edit Profile
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });

        // Change Password
        changePasswordButton.setOnClickListener(v -> showChangePasswordDialog());

        // Clear Cache
        clearCacheButton.setOnClickListener(v -> showClearCacheDialog());

        // Logout
        logoutButton.setOnClickListener(v -> showLogoutDialog());

        // Delete Account
        deleteAccountButton.setOnClickListener(v -> showDeleteAccountDialog());

        // Spinners
        languageSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, 
                    int position, long id) {
                sharedPreferences.edit().putInt("language_index", position).apply();
                if (position > 0) { // 0 is default language
                    Toast.makeText(SettingsActivity.this, "Language setting will apply after restart", 
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        notificationFrequencySpinner.setOnItemSelectedListener(
                new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, 
                    int position, long id) {
                sharedPreferences.edit().putInt("notification_frequency", position).apply();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void showChangePasswordDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        TextInputEditText currentPasswordInput = dialogView.findViewById(R.id.currentPasswordInput);
        TextInputEditText newPasswordInput = dialogView.findViewById(R.id.newPasswordInput);
        TextInputEditText confirmPasswordInput = dialogView.findViewById(R.id.confirmPasswordInput);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle("Change Password")
                .setView(dialogView)
                .setPositiveButton("Change", null)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(view -> {
                String currentPassword = currentPasswordInput.getText().toString().trim();
                String newPassword = newPasswordInput.getText().toString().trim();
                String confirmPassword = confirmPasswordInput.getText().toString().trim();

                // Validate inputs
                if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(SettingsActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (newPassword.length() < 6) {
                    Toast.makeText(SettingsActivity.this, "New password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(SettingsActivity.this, "New passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get stored password from SharedPreferences
                String storedPassword = sharedPreferences.getString("user_password", "");
                if (!currentPassword.equals(storedPassword)) {
                    Toast.makeText(SettingsActivity.this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Update password in SharedPreferences
                sharedPreferences.edit()
                    .putString("user_password", newPassword)
                    .apply();
                Toast.makeText(SettingsActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    private void clearAppCache() {
        try {
            // Clear app cache
            File cacheDir = getCacheDir();
            File appDir = new File(cacheDir.getParent());
            if (appDir.exists()) {
                String[] children = appDir.list();
                for (String s : children) {
                    if (!s.equals("lib")) {
                        deleteDir(new File(appDir, s));
                    }
                }
            }
            // Clear shared preferences except user credentials
            String userEmail = sharedPreferences.getString("user_email", "");
            String userPassword = sharedPreferences.getString("user_password", "");
            sharedPreferences.edit().clear().apply();
            // Restore user credentials
            sharedPreferences.edit()
                    .putString("user_email", userEmail)
                    .putString("user_password", userPassword)
                    .apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String child : children) {
                    boolean success = deleteDir(new File(dir, child));
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        return dir != null && dir.delete();
    }

    private void showClearCacheDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Clear Cache")
                .setMessage("Are you sure you want to clear the app cache?")
                .setPositiveButton("Clear", (dialog, which) -> {
                    clearAppCache();
                    Toast.makeText(this, "Cache cleared successfully", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showLogoutDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    // Clear user data
                    sharedPreferences.edit()
                        .remove("user_token")
                        .remove("user_id")
                        .apply();

                    // Return to login screen
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteUserAccount() {
        try {
            // Clear all user data
            clearAppCache();
            
            // Clear all SharedPreferences
            sharedPreferences.edit().clear().apply();
            
            // Clear database entries for the user
            // Note: This is a placeholder - implement actual database deletion based on your backend
            
            // Clear any saved files
            File filesDir = getFilesDir();
            deleteDir(filesDir);
            
            // Return to welcome screen
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error deleting account", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteAccountDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Show confirmation dialog with custom layout
                    View confirmView = getLayoutInflater().inflate(R.layout.dialog_confirm_delete, null);
                    TextInputEditText confirmInput = confirmView.findViewById(R.id.confirmDeleteInput);
                    
                    new MaterialAlertDialogBuilder(this)
                            .setTitle("Confirm Deletion")
                            .setMessage("Please type 'DELETE' to confirm account deletion")
                            .setView(confirmView)
                            .setPositiveButton("Confirm", (confirmDialog, confirmWhich) -> {
                                if (confirmInput != null && 
                                    "DELETE".equals(confirmInput.getText().toString().trim())) {
                                    deleteUserAccount();
                                } else {
                                    Toast.makeText(this, "Incorrect confirmation text", 
                                        Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                })
                .setNegativeButton("Cancel", null)
                .show();
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