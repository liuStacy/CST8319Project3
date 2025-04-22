package cst8319.group11.project3.grocerylist.app;

import android.app.Application;
import cst8319.group11.project3.grocerylist.util.NotificationHelper;

public class GroceryApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        NotificationHelper.createNotificationChannels(this);
    }
}
