package com.example.recipe_app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class  ForgotPasswordActivity extends AppCompatActivity {
    private TextInputEditText emailInput;
    private Button resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailInput = findViewById(R.id.emailInput);
        resetButton = findViewById(R.id.resetButton);

        resetButton.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        String email = emailInput.getText().toString().trim();

        if (email.isEmpty()) {
            emailInput.setError(getString(R.string.email_required));
            return;
        }

        // TODO: Implement local password reset functionality
        Toast.makeText(ForgotPasswordActivity.this,
                "Password reset functionality not implemented yet",
                Toast.LENGTH_SHORT).show();
        finish();
    }
}