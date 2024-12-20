package com.example.a1212508_1211441_courseproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.a1212508_1211441_courseproject.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;

    private DataBaseHelper dbHelper; // Database Helper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up binding and layout
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Load saved theme preference
        SharedPreferences prefs = getSharedPreferences("theme", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("is_dark_mode", false);

        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        // Set up the toolbar
        setSupportActionBar(binding.appBarHome.toolbar);
        binding.appBarHome.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Set up AppBarConfiguration
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_new_task, R.id.nav_all_tasks,
                R.id.nav_completed, R.id.nav_search, R.id.nav_profile, R.id.nav_logout)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Retrieve user email from Intent
        Intent intent = getIntent();
        String email = intent.getStringExtra("email");

        // Initialize database helper
        dbHelper = new DataBaseHelper(this);

        // Populate navigation header with user information
        populateNavHeader(navigationView, email);
    }

    /**
     * Populate the navigation drawer header with the user's name and email.
     */
    private void populateNavHeader(NavigationView navigationView, String email) {
        // Access the header view of the navigation drawer
        View headerView = navigationView.getHeaderView(0);

        // Initialize header TextViews
        TextView usernameTextView = headerView.findViewById(R.id.username_in_nav);
        TextView emailTextView = headerView.findViewById(R.id.user_email_in_nav);

        // Fetch user data from the database
        Cursor cursor = dbHelper.getUsername(email);
        if (cursor != null && cursor.moveToFirst()) {
            String firstName = cursor.getString(1); // First name
            String lastName = cursor.getString(2);  // Last name
            usernameTextView.setText(firstName + " " + lastName);
            emailTextView.setText(email);
        } else {
            usernameTextView.setText("User");
            emailTextView.setText(email);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);

        // Initialize the SwitchCompat
        MenuItem themeItem = menu.findItem(R.id.action_theme);
        View actionView = themeItem.getActionView();
        SwitchCompat switchTheme = actionView.findViewById(R.id.switch_theme);

        // Load the current theme state
        SharedPreferences prefs = getSharedPreferences("theme", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("is_dark_mode", false);

        switchTheme.setChecked(isDarkMode);

        // Set the switch listener
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Save the preference
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("is_dark_mode", isChecked);
            editor.apply();

            // Apply the theme
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );

            // Restart the activity to apply the theme
            recreate();
        });

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
