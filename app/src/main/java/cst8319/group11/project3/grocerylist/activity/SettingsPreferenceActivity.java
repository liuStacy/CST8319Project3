package cst8319.group11.project3.grocerylist.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

import cst8319.group11.project3.grocerylist.R;
import cst8319.group11.project3.grocerylist.dao.SettingsDao;
import cst8319.group11.project3.grocerylist.database.AppDatabase;
import cst8319.group11.project3.grocerylist.models.Settings;
import cst8319.group11.project3.grocerylist.workers.ReminderWorker;

/*
 * Author: Rongrong Liu
 * File Name: SettingsPreferenceActivity.java
 * Group: 11
 * Project: Grocery List
 * Due Date: 04/22/2025
 * Created Date: 03/10/2025
 *
 * */
public class SettingsPreferenceActivity extends AppCompatActivity {

    private static final int REQUEST_POST_NOTIFICATIONS = 1002;

    private RadioButton radioLightTheme, radioDarkTheme;
    private Switch switchNotifications;
    private RadioButton radioSpendingTrackerOn, radioSpendingTrackerOff;
    private Button buttonSaveSettings;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private SettingsDao settingsDao;
    private Settings userSettings;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set theme based on SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = sharedPreferences.getString("theme_preference", "light");
        AppCompatDelegate.setDefaultNightMode(
                "dark".equalsIgnoreCase(theme)
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_preference);

        // Start ActionBar with back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        editor = sharedPreferences.edit();
        settingsDao = AppDatabase.getDatabase(this).settingsDao();

        // Fetch current user ID from intent
        currentUserId = (int) getIntent().getLongExtra("USER_ID", -1);

        radioLightTheme = findViewById(R.id.radioLightTheme);
        radioDarkTheme = findViewById(R.id.radioDarkTheme);
        switchNotifications = findViewById(R.id.switchNotifications);
        radioSpendingTrackerOn = findViewById(R.id.radioSpendingTrackerOn);
        radioSpendingTrackerOff = findViewById(R.id.radioSpendingTrackerOff);
        buttonSaveSettings = findViewById(R.id.buttonSaveSettings);

        // Set initial theme based on SharedPreferences
        if ("dark".equalsIgnoreCase(theme)) {
            radioDarkTheme.setChecked(true);
        } else {
            radioLightTheme.setChecked(true);
        }

        // Load settings from database
        loadSettings();

        // Request notification permission
        buttonSaveSettings.setOnClickListener(v -> {
            saveSettings();
            Toast.makeText(this, "Settings saved!", Toast.LENGTH_SHORT).show();
            recreate();
        });
    }

    // Load settings from database
    private void loadSettings() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userSettings = settingsDao.getSettingsForUser(currentUserId);
            runOnUiThread(() -> {
                if (userSettings != null) {
                    switchNotifications.setChecked(userSettings.isNotificationsEnabled());
                    if (userSettings.isTrackSpending()) {
                        radioSpendingTrackerOn.setChecked(true);
                    } else {
                        radioSpendingTrackerOff.setChecked(true);
                    }
                } else {
                    userSettings = new Settings(currentUserId, false, "light", false, 0, false);
                    switchNotifications.setChecked(false);
                    radioSpendingTrackerOff.setChecked(true);
                }
            });
        });
    }

    // Save settings to database
    private void saveSettings() {
        boolean notificationsEnabled = switchNotifications.isChecked();
        String theme = radioDarkTheme.isChecked() ? "dark" : "light";
        boolean trackSpending = radioSpendingTrackerOn.isChecked();

        editor.putString("theme_preference", theme).apply();

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

        WorkManager wm = WorkManager.getInstance(getApplicationContext());

        if (notificationsEnabled &&
                (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                        ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                                == PackageManager.PERMISSION_GRANTED)) {

            // Periodically trigger ReminderWorker, every day
            PeriodicWorkRequest periodic = new PeriodicWorkRequest.Builder(
                    ReminderWorker.class, 1, TimeUnit.DAYS).build();

            wm.enqueueUniquePeriodicWork("DailyReminder", ExistingPeriodicWorkPolicy.REPLACE, periodic);

            // Immediately trigger ReminderWorker, for testing purposes
            OneTimeWorkRequest oneTime = new OneTimeWorkRequest.Builder(ReminderWorker.class).build();
            wm.enqueue(oneTime);

        } else if (notificationsEnabled) {
            Toast.makeText(this, "Please grant notification permission in Settings", Toast.LENGTH_LONG).show();
        } else {
            wm.cancelUniqueWork("DailyReminder");
        }

        AppCompatDelegate.setDefaultNightMode(
                "dark".equalsIgnoreCase(theme)
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    // Request notification permission
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                new AlertDialog.Builder(this)
                        .setTitle("Notification Permission")
                        .setMessage("Grant notification permission to receive reminders.")
                        .setPositiveButton("Allow", (dialog, which) -> ActivityCompat
                                .requestPermissions(
                                        this,
                                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                                        REQUEST_POST_NOTIFICATIONS
                                ))
                        .setNegativeButton("Cancel", null)
                        .show();
            } else {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_POST_NOTIFICATIONS
                );
            }
        }
    }

    // Handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show();
                saveSettings(); // 👈 Re-trigger to enqueue
            } else {
                Toast.makeText(this, "Permission denied. No reminders.", Toast.LENGTH_LONG).show();
                switchNotifications.setChecked(false);
            }
        }
    }

    // Handle back button press
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}