package com.example.a1212508_1211441_courseproject;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextFirstName, editTextLastName, editTextPassword, editTextConfirmPassword;
    private DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize views
        dbHelper = new DataBaseHelper(this);
        editTextEmail = findViewById(R.id.et_email);
        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextPassword = findViewById(R.id.et_password);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        Button signUpButton = findViewById(R.id.btn_sign_up);

        // Handle Sign Up button click
        signUpButton.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String firstName = editTextFirstName.getText().toString().trim();
            String lastName = editTextLastName.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String confirmPassword = editTextConfirmPassword.getText().toString().trim();

            if (validateInputs(email, firstName, lastName, password, confirmPassword)) {
                // Attempt to save user to database
                boolean isInserted = dbHelper.registerUser(email, firstName, lastName, password);
                if (isInserted) {
                    Toast.makeText(this, "Sign up successful! Welcome " + firstName, Toast.LENGTH_SHORT).show();
                    finish(); // Go back or close the activity
                } else {
                    Toast.makeText(this, "Registration failed. User already exists.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateInputs(String email, String firstName, String lastName, String password, String confirmPassword) {
        // Check for empty fields
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Email validation
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Invalid email format.");
            return false;
        }
        // First name validation
        if (firstName.length() < 5 || firstName.length() > 20) {
            editTextFirstName.setError("First name must be 5-20 characters.");
            return false;
        }
        // Last name validation
        if (lastName.length() < 5 || lastName.length() > 20) {
            editTextLastName.setError("Last name must be 5-20 characters.");
            return false;
        }
        // Password validation
        if (password.length() < 6 || password.length() > 12 || !password.matches(".*\\d.*") ||
                !password.matches(".*[a-z].*") || !password.matches(".*[A-Z].*")) {
            editTextPassword.setError("Password must be 6-12 characters, include one number, one lowercase, and one uppercase letter.");
            return false;
        }
        // Confirm password validation
        if (!password.equals(confirmPassword)) {
            editTextConfirmPassword.setError("Passwords do not match.");
            return false;
        }
        return true;
    }
}
