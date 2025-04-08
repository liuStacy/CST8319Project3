package cst8319.group11.project3.grocerylist.activity;

/*
 * Author: Rongrong Liu
 * File Name: MainActivity.java
 * Group: 11
 * Project: Grocery List
 * Due Date: 04/08/2025
 * Created Date: 03/10/2025
 *
 * */
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.preference.PreferenceManager;

import cst8319.group11.project3.grocerylist.R;

public class MainActivity extends AppCompatActivity {

    private Button buttonListManagement;
    private Button buttonShoppingMode;
    private Button buttonSettings;
    private long currentUserId;
    private Button buttonLogout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load theme from SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = sharedPreferences.getString("theme_preference", "light");

        // Apply theme
        if ("dark".equalsIgnoreCase(theme)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_main);

        // Initialize buttons
        buttonListManagement = findViewById(R.id.buttonListManagement);
        buttonShoppingMode = findViewById(R.id.buttonShoppingMode);
        buttonSettings = findViewById(R.id.buttonSettings);
        buttonLogout = findViewById(R.id.buttonLogout);

        // Fetch user ID from intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("USER_ID")) {
            currentUserId = intent.getLongExtra("USER_ID", -1);
        }

        if (currentUserId == -1) {
            // Handle the case where currentUserId is not available
            finish();
            return;
        }

        // Use currentUserId as needed
        buttonListManagement.setOnClickListener(v -> {
            Intent listIntent = new Intent(MainActivity.this, ListManagementActivity.class);
            listIntent.putExtra("USER_ID", currentUserId); // Pass user ID
            startActivity(listIntent);
        });

        buttonShoppingMode.setOnClickListener(v -> {
            // Fetch list ID from intent
            Intent shoppingIntent = new Intent(MainActivity.this, ShoppingModeActivity.class);
            shoppingIntent.putExtra("LIST_ID", 123); // Replace with actual list ID
            startActivity(shoppingIntent);
        });

        buttonSettings.setOnClickListener(v -> {
            Intent settingsIntent = new Intent(MainActivity.this, SettingsPreferenceActivity.class);
            settingsIntent.putExtra("USER_ID", currentUserId);
            startActivity(settingsIntent);
        });

        buttonLogout.setOnClickListener(v -> {
            // Optional: Clear SharedPreferences if you store login data
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.clear(); // Or just remove specific login keys
            editor.apply();

            // Return to login activity
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clears back stack
            startActivity(loginIntent);
            finish(); // Finish MainActivity
        });
    }
}