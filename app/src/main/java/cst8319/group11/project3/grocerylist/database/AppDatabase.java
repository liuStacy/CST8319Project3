package cst8319.group11.project3.grocerylist.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cst8319.group11.project3.grocerylist.dao.CategoryDao;
import cst8319.group11.project3.grocerylist.dao.GroceryListDao;
import cst8319.group11.project3.grocerylist.dao.ItemDao;
import cst8319.group11.project3.grocerylist.dao.ItemPriceHistoryDao;
import cst8319.group11.project3.grocerylist.dao.SettingsDao;
import cst8319.group11.project3.grocerylist.dao.StoreDao;
import cst8319.group11.project3.grocerylist.dao.StoreSectionDao;
import cst8319.group11.project3.grocerylist.dao.UserDao;
import cst8319.group11.project3.grocerylist.models.Category;
import cst8319.group11.project3.grocerylist.models.GroceryList;
import cst8319.group11.project3.grocerylist.models.Item;
import cst8319.group11.project3.grocerylist.models.ItemPriceHistory;
import cst8319.group11.project3.grocerylist.models.Settings;
import cst8319.group11.project3.grocerylist.models.Store;
import cst8319.group11.project3.grocerylist.models.StoreSection;
import cst8319.group11.project3.grocerylist.models.User;

@Database(
        entities = {
                User.class,
                GroceryList.class,
                Item.class,
                Category.class,
                Settings.class,
                Store.class,
                StoreSection.class,
                ItemPriceHistory.class
        },
        version = 3,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    // Thread pool for DB operations
    private static final int NUMBER_OF_THREADS = 4;
    public abstract GroceryListDao groceryListDao();
    public abstract ItemDao itemDao();
    public abstract CategoryDao categoryDao();
    public abstract SettingsDao settingsDao();
    // --- Singleton setup ---
    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(), // ✅ Always use Application context
                                    AppDatabase.class,
                                    "grocery.sqlite"
                            )
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // ✅ Optional: Clear instance for testing or reset
    public static void resetDatabaseInstance() {
        INSTANCE = null;
    }

    // --- DAOs ---
    public abstract UserDao userDao();

    public abstract StoreDao storeDao();
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public abstract StoreSectionDao storeSectionDao();

    public abstract ItemPriceHistoryDao itemPriceHistoryDao();
}
