package com.example.a1212508_1211441_courseproject.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.a1212508_1211441_courseproject.DataBaseHelper;
import com.example.a1212508_1211441_courseproject.R;

public class ProfileFragment extends Fragment {

    private TextView textViewUsername, textViewEmail, textViewPassword;
    private EditText editTextEmail, editTextPassword;
    private Button buttonUpdateProfile;

    private DataBaseHelper dbHelper; // Database Helper
    private String loggedInUserEmail, newEmail, password;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        textViewEmail = rootView.findViewById(R.id.textViewEmail);
        textViewUsername = rootView.findViewById(R.id.textViewUsername);
        textViewPassword = rootView.findViewById(R.id.textViewPassword);
        editTextEmail = rootView.findViewById(R.id.editTextEmail);
        editTextPassword = rootView.findViewById(R.id.editTextPassword);
        buttonUpdateProfile = rootView.findViewById(R.id.buttonUpdateProfile);

        dbHelper = new DataBaseHelper(getContext()); // Initialize database helper

        // Retrieve user email from Intent
        Intent intent = getActivity().getIntent();
        loggedInUserEmail = intent.getStringExtra("email");

        if (loggedInUserEmail == null) {
            Toast.makeText(getContext(), "Error: User not logged in", Toast.LENGTH_SHORT).show();
            return rootView; // Return early if no user is logged in
        }

        // Fetch user data from the database
        Cursor cursor = dbHelper.getUsername(loggedInUserEmail);
        if (cursor != null && cursor.moveToFirst()) {
            String firstName = cursor.getString(1); // First name
            String lastName = cursor.getString(2);  // Last name
            password = cursor.getString(3);  // Password
            textViewUsername.setText("Username: " + firstName + " " + lastName);
            textViewEmail.setText("Current Email: " + loggedInUserEmail);
            textViewPassword.setText("Current Password: " + password);
        } else {
            textViewUsername.setText("Username");
            textViewEmail.setText("Email");
            textViewPassword.setText("Password");
        }
        if (cursor != null) {
            cursor.close();
        }

        buttonUpdateProfile.setOnClickListener(v -> {
            boolean isEmailUpdated = false, isPasswordUpdated = false;

            // Check if E-mail updated or not
            if(editTextEmail.getText().toString().isEmpty() || editTextEmail.getText().toString().equals(loggedInUserEmail)) {
                newEmail = loggedInUserEmail;
                Toast.makeText(getContext(), "Current Email kept not updated!", Toast.LENGTH_SHORT).show();
            }
            else {
                newEmail = editTextEmail.getText().toString();
                isEmailUpdated = true;
                Toast.makeText(getContext(), "Current Email updated!", Toast.LENGTH_SHORT).show();
            }

            // Check if Password updated or not
            if(editTextPassword.getText().toString().isEmpty() || editTextPassword.getText().toString().equals(password)) {
                Toast.makeText(getContext(), "Current Password kept not updated!", Toast.LENGTH_SHORT).show();
            }
            else {
                password = editTextPassword.getText().toString();
                isPasswordUpdated = true;
                Toast.makeText(getContext(), "Current Password updated!", Toast.LENGTH_SHORT).show();
            }

            // Check if E-mail or Password updated or not to update User Profile or not
            if (isEmailUpdated || isPasswordUpdated) {
                boolean isProfileUpdated = dbHelper.updateUser(loggedInUserEmail, newEmail, password);

                if (isProfileUpdated)
                    Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getContext(), "Error: Failed to update Profile!", Toast.LENGTH_SHORT).show();
            }

            // Cleanup editing fields
            editTextEmail.setText("");
            editTextPassword.setText("");

        });

        return rootView;
    }
}
