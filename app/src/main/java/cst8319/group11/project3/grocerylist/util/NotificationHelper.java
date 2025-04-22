package cst8319.group11.project3.grocerylist.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

/*
 * Author: Rongrong Liu
 * File Name: NotificationHelper.java
 * Group: 11
 * Project: Grocery List
 * Due Date: 04/22/2025
 * Created Date: 04/10/2025
 *
 * */
public class NotificationHelper {
    public static final String REMINDER_CHANNEL_ID = "REMINDER_CHANNEL";
    private static final String TAG = "NotificationHelper";

    public static void createNotificationChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = context.getSystemService(NotificationManager.class);

            if (manager == null) {
                Log.e(TAG, "NotificationManager is null");
                return;
            }

            NotificationChannel existing = manager.getNotificationChannel(REMINDER_CHANNEL_ID);
            if (existing != null) {
                Log.d(TAG, "Notification channel already exists. Importance: " + existing.getImportance());
                return; // Optional: skip re-creating if it exists
            }

            NotificationChannel reminderChannel = new NotificationChannel(
                    REMINDER_CHANNEL_ID,
                    "Reminders",
                    NotificationManager.IMPORTANCE_HIGH // 🔥 make it more visible
            );

            reminderChannel.setDescription("Daily grocery list reminders");
            reminderChannel.enableLights(true);
            reminderChannel.setLightColor(Color.BLUE);
            reminderChannel.enableVibration(true);
            reminderChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});
            reminderChannel.setShowBadge(true);

            manager.createNotificationChannel(reminderChannel);
            Log.d(TAG, "Notification channel created (IMPORTANCE_HIGH)");
        }
    }

    // Helper method to check if notifications are enabled
    public static boolean areNotificationsEnabled(Context context) {
        // For Android 13+, check permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) ==
                    android.content.pm.PackageManager.PERMISSION_GRANTED;
        }

        // For older versions, notifications are enabled by default
        return true;
    }

    // Add a test notification method for debugging
    public static void sendTestNotification(Context context) {
        // Check permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) !=
                    android.content.pm.PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "Cannot send test notification - no permission");
                return;
            }
        }

        androidx.core.app.NotificationCompat.Builder builder =
                new androidx.core.app.NotificationCompat.Builder(context, REMINDER_CHANNEL_ID)
                        .setSmallIcon(cst8319.group11.project3.grocerylist.R.drawable.ic_notify_reminder)
                        .setContentTitle("Test Notification")
                        .setContentText("This is a test notification to verify settings")
                        .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH);

        androidx.core.app.NotificationManagerCompat notificationManager =
                androidx.core.app.NotificationManagerCompat.from(context);

        try {
            notificationManager.notify(9999, builder.build());
            Log.d(TAG, "Test notification sent successfully");
        } catch (SecurityException e) {
            Log.e(TAG, "Error sending test notification: " + e.getMessage());
        }
    }
}