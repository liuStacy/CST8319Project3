package cst8319.group11.project3.grocerylist.activity;
/*
 * Author: Rongrong Liu
 * File Name: SettingsPreferenceActivity.java
 * Group: 11
 * Project: Grocery List
 * Due Date: 04/08/2025
 * Created Date: 03/10/2025
 *
 * */
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import cst8319.group11.project3.grocerylist.R;
import cst8319.group11.project3.grocerylist.dao.SettingsDao;
import cst8319.group11.project3.grocerylist.database.AppDatabase;
import cst8319.group11.project3.grocerylist.models.Settings;

public class SettingsPreferenceActivity extends AppCompatActivity {

    private RadioButton radioLightTheme;
    private RadioButton radioDarkTheme;
    private Switch switchNotifications;
    private RadioButton radioSpendingTrackerOn;
    private RadioButton radioSpendingTrackerOff;
    private Button buttonSaveSettings;
    private SettingsDao settingsDao;
    private int currentUserId = -1; // 从登录信息或 Intent 获取
    private Settings userSettings;
    private RadioGroup themeRadioGroup;
    private RadioGroup spendingTrackerRadioGroup;
    private boolean isSettingsLoaded = false;

    // Use SharedPreferences to store and retrieve user preferences
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set theme based on SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = sharedPreferences.getString("theme_preference", "light");
        if ("dark".equalsIgnoreCase(theme)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_preference);

        // Start ActionBar with back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        editor = sharedPreferences.edit();
        settingsDao = AppDatabase.getDatabase(getApplicationContext()).settingsDao();

        // Fetch current user ID from intent
        currentUserId = (int)getIntent().getLongExtra("USER_ID", -1);

        radioLightTheme = findViewById(R.id.radioLightTheme);
        radioDarkTheme = findViewById(R.id.radioDarkTheme);
        switchNotifications = findViewById(R.id.switchNotifications);
        radioSpendingTrackerOn = findViewById(R.id.radioSpendingTrackerOn);
        radioSpendingTrackerOff = findViewById(R.id.radioSpendingTrackerOff);
        buttonSaveSettings = findViewById(R.id.buttonSaveSettings);
        themeRadioGroup = findViewById(R.id.themeRadioGroup);
        spendingTrackerRadioGroup = findViewById(R.id.spendingTrackerRadioGroup);

        // Set initial theme based on SharedPreferences
        if ("dark".equalsIgnoreCase(theme)) {
            radioDarkTheme.setChecked(true);
            radioLightTheme.setChecked(false);
        } else {
            radioLightTheme.setChecked(true);
            radioDarkTheme.setChecked(false);
        }

        // Load settings from database
        loadSettings();

        themeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (isSettingsLoaded) {
                if (checkedId == R.id.radioLightTheme) {
                    Log.d("SettingsActivity", "Light Theme selected");
                } else if (checkedId == R.id.radioDarkTheme) {
                    Log.d("SettingsActivity", "Dark Theme selected");
                }
            }
        });

        spendingTrackerRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (isSettingsLoaded) {
                if (checkedId == R.id.radioSpendingTrackerOn) {
                    Log.d("SettingsActivity", "Spending Tracker On selected");
                } else if (checkedId == R.id.radioSpendingTrackerOff) {
                    Log.d("SettingsActivity", "Spending Tracker Off selected");
                }
            }
        });

        buttonSaveSettings.setOnClickListener(view -> {
            saveSettings();
            Toast.makeText(this, "Settings saved!", Toast.LENGTH_SHORT).show();
            // Reload settings after saving
            recreate();
        });
    }

    private void loadSettings() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userSettings = settingsDao.getSettingsForUser(currentUserId);
            runOnUiThread(() -> {
                if (userSettings != null) {
                    switchNotifications.setChecked(userSettings.isNotificationsEnabled());
                    if (userSettings.isTrackSpending()) {
                        radioSpendingTrackerOn.setChecked(true);
                        radioSpendingTrackerOff.setChecked(false);
                    } else {
                        radioSpendingTrackerOff.setChecked(true);
                        radioSpendingTrackerOn.setChecked(false);
                    }
                } else {
                    // Initialize with default values
                    userSettings = new Settings(currentUserId, false, "light", false, 0, false);
                    switchNotifications.setChecked(false);
                    radioSpendingTrackerOff.setChecked(true);
                    radioSpendingTrackerOn.setChecked(false);
                }
                isSettingsLoaded = true;
            });
        });
    }

    private void saveSettings() {
        boolean notificationsEnabled = switchNotifications.isChecked();
        // Set theme based on radio buttons
        String theme = radioDarkTheme.isChecked() ? "dark" : "light";
        boolean trackSpending = radioSpendingTrackerOn.isChecked();

        // Save theme to SharedPreferences
        editor.putString("theme_preference", theme);
        editor.apply();

        // Save settings to database
        if (userSettings == null) {
            userSettings = new Settings(currentUserId, notificationsEnabled, theme, false, 0, trackSpending);
        } else {
            userSettings.setNotificationsEnabled(notificationsEnabled);
            userSettings.setTheme(theme);
            userSettings.setTrackSpending(trackSpending);
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
            settingsDao.insertOrUpdate(userSettings);
        });

        // Apply theme based on SharedPreferences
        if ("dark".equalsIgnoreCase(theme)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    // Handle back button press
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(SettingsPreferenceActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
