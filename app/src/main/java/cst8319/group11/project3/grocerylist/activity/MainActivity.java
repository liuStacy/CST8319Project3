package cst8319.group11.project3.grocerylist.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import cst8319.group11.project3.grocerylist.R;

/*
 * Author: Rongrong Liu
 * File Name: MainActivity.java
 * Group: 11
 * Project: Grocery List
 * Due Date: 04/22/2025
 * Created Date: 03/10/2025
 *
 * */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Button buttonListManagement;
    private Button buttonShoppingMode;
    private Button buttonSettings;
    private Button buttonLogout;

    private long currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* -------------------------  Theme  ------------------------- */
        // Apply the theme selected in Settings (default: light)
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        String theme = sharedPreferences.getString("theme_preference", "light");
        if ("dark".equalsIgnoreCase(theme)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_main);

        /* --------------------  Retrieve USER_ID  ------------------- */
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("USER_ID")) {
            currentUserId = intent.getLongExtra("USER_ID", -1);
            Log.d(TAG, "✅ currentUserId set: " + currentUserId);

            // ✅ Store in SharedPreferences so other components (e.g., ReminderWorker) can access it
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putLong("logged_in_user_id", currentUserId)
                    .apply();
        }

        if (currentUserId == -1) {
            Toast.makeText(this,
                    "User ID not found. Please log in again.",
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class)); // Start LoginActivity
            finish();
            return;
        }

        /* --------------------  Bind UI controls  ------------------- */
        buttonListManagement = findViewById(R.id.buttonListManagement);
        buttonShoppingMode = findViewById(R.id.buttonShoppingMode);
        buttonSettings = findViewById(R.id.buttonSettings);
        buttonLogout = findViewById(R.id.buttonLogout);

        /* --------------------  List Management  -------------------- */
        buttonListManagement.setOnClickListener(v -> {
            Intent listIntent = new Intent(MainActivity.this, ListManagementActivity.class);
            listIntent.putExtra("USER_ID", currentUserId); // Pass USER_ID to ListManagementActivity
            startActivity(listIntent);
        });

        /* --------------------  Shopping Mode  ---------------------- */
        // Opens ShoppingModeActivity with store‑selection spinner
        buttonShoppingMode.setOnClickListener(v -> {
            // Fetch User ID from intent
            Intent shoppingIntent = new Intent(MainActivity.this, ShoppingModeActivity.class);
            shoppingIntent.putExtra("USER_ID", currentUserId); // Pass USER_ID to ShoppingModeActivity
            startActivity(shoppingIntent);
        });

        /* ----------------------  Settings  ------------------------- */
        buttonSettings.setOnClickListener(v -> {
            Intent settingsIntent = new Intent(MainActivity.this, SettingsPreferenceActivity.class);
            settingsIntent.putExtra("USER_ID", currentUserId);
            startActivity(settingsIntent);
        });

        /* -----------------------  Logout  -------------------------- */
        buttonLogout.setOnClickListener(v -> {
            // Optional: Clear SharedPreferences if you store login data
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            prefs.edit()
                    .remove("logged_in_user_id") // Just remove specific login keys
                    .apply();

            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // Clears back stack
            startActivity(loginIntent);
            finish(); // Finish MainActivity
        });

    }
}
