package cst8319.group11.project3.grocerylist.workers;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.List;

import cst8319.group11.project3.grocerylist.R;
import cst8319.group11.project3.grocerylist.activity.MainActivity;
import cst8319.group11.project3.grocerylist.database.AppDatabase;
import cst8319.group11.project3.grocerylist.models.Item;
import cst8319.group11.project3.grocerylist.util.NotificationHelper;


/*
 * Author: Rongrong Liu
 * File Name: ReminderWorker.java
 * Group: 11
 * Project: Grocery List
 * Due Date: 04/22/2025
 * Created Date: 04/10/2025
 *
 * */
public class ReminderWorker extends Worker {

    private static final String TAG = "ReminderWorker";
    private static final int NOTIFICATION_ID = 1;

    public ReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        Log.d(TAG, "🔄 doWork() started");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "🚫 Missing POST_NOTIFICATIONS permission, skipping reminder");
            return Result.success();
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        long userId = prefs.getLong("logged_in_user_id", -1);
        if (userId == -1) {
            Log.w(TAG, "⚠️ No valid user ID found in preferences");
            return Result.success();
        }

        List<Item> unpurchasedItems;
        try {
            unpurchasedItems = AppDatabase.databaseWriteExecutor.submit(() ->
                    AppDatabase.getDatabase(context).itemDao().getUnpurchasedItemsForUser((int) userId)
            ).get();
        } catch (Exception e) {
            Log.e(TAG, "❌ Error fetching unpurchased items: " + e.getMessage());
            return Result.failure();
        }

        if (unpurchasedItems == null || unpurchasedItems.isEmpty()) {
            Log.d(TAG, "🧺 No unpurchased items, skipping notification");
            return Result.success();
        }

        // ✅ 统计总数量
        int totalQuantity = unpurchasedItems.stream()
                .mapToInt(Item::getQuantity)
                .sum();

        Log.d(TAG, "🛒 Found " + unpurchasedItems.size() + " unpurchased item types, total quantity = " + totalQuantity);
        for (Item item : unpurchasedItems) {
            Log.d(TAG, "   - " + item.getItemName() + " | qty = " + item.getQuantity() + " | purchased = " + item.isPurchased() + " | listID = " + item.getListID());
        }

        Intent intent = new Intent(context, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pi = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context, NotificationHelper.REMINDER_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notify_reminder)
                .setContentTitle("Grocery List Reminder")
                .setContentText("You have " + totalQuantity + " unpurchased items to buy.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pi)
                .setAutoCancel(true);

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build());
            Log.d(TAG, "✅ Reminder notification sent");
        } catch (SecurityException e) {
            Log.e(TAG, "❌ Failed to send notification: " + e.getMessage());
        }

        return Result.success();
    }
}
