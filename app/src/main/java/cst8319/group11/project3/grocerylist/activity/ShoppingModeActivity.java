package cst8319.group11.project3.grocerylist.activity;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cst8319.group11.project3.grocerylist.R;
import cst8319.group11.project3.grocerylist.adapters.ItemAdapter;
import cst8319.group11.project3.grocerylist.database.AppDatabase;
import cst8319.group11.project3.grocerylist.models.GroceryList;
import cst8319.group11.project3.grocerylist.models.Item;
import cst8319.group11.project3.grocerylist.models.ItemPriceHistory;
import cst8319.group11.project3.grocerylist.models.Settings;
import cst8319.group11.project3.grocerylist.models.Store;
import cst8319.group11.project3.grocerylist.repositories.ItemRepository;

/*
 * Author: Rongrong Liu
 * File Name: SettingsPreferenceActivity.java
 * Group: 11
 * Project: Grocery List
 * Due Date: 04/10/2025
 * Created Date: 04/22/2025
 *
 * */
public class ShoppingModeActivity extends AppCompatActivity {

    private Spinner listSpinner, storeSpinner;
    private ListView itemsListView;
    private Button completeShoppingButton, resetBudgetButton, priceCompareButton;
    private ProgressBar budgetProgressBar;
    private TextView budgetStatusText, totalSpentText;

    private long userId;
    private int selectedListId, selectedStoreId;

    private ItemRepository itemRepo;
    private ItemAdapter itemAdapter;
    private List<Item> itemList;
    private Map<Integer, List<ItemPriceHistory>> storePriceMap = new HashMap<>();

    private double listBudget = 0.0, totalSpent = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("GroceryList");
        }

        String theme = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("theme_preference", "light");
        AppCompatDelegate.setDefaultNightMode("dark".equalsIgnoreCase(theme) ?
                AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.activity_shopping_mode);

        listSpinner = findViewById(R.id.spinnerLists);
        storeSpinner = findViewById(R.id.spinnerStores);
        itemsListView = findViewById(R.id.listViewItems);
        completeShoppingButton = findViewById(R.id.buttonCompleteShopping);
        resetBudgetButton = findViewById(R.id.buttonResetBudget);
        priceCompareButton = findViewById(R.id.buttonShowPriceComparison);
        budgetProgressBar = findViewById(R.id.progressBarBudget);
        budgetStatusText = findViewById(R.id.textViewBudgetStatus);
        totalSpentText = findViewById(R.id.textViewTotalSpent);

        userId = getIntent().getLongExtra("USER_ID", -1);
        if (userId == -1) {
            Toast.makeText(this, "User ID missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        itemRepo = new ItemRepository(getApplication());
        itemList = new ArrayList<>();
        itemAdapter = new ItemAdapter(this, itemList);

        itemAdapter.setOnPurchaseToggleListener((item, isChecked) -> {
            if (isChecked) {
                new Thread(() -> {
                    itemRepo.updatePurchaseStatus(item.getItemID(), true);
                    updateBudgetTracking(item.getPrice() * item.getQuantity());
                    runOnUiThread(this::loadUnpurchasedItems);
                }).start();
            }
        });

        itemsListView.setAdapter(itemAdapter);
        loadGroceryLists();
        setupStores();

        listSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                GroceryList chosen = (GroceryList) parent.getItemAtPosition(pos);
                selectedListId = chosen.getListID();
                loadUnpurchasedItems();
                loadBudgetData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        storeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedStoreId = ((Store) parent.getItemAtPosition(pos)).getStoreID();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        completeShoppingButton.setOnClickListener(v -> new Thread(() -> {
            double totalCost = 0;
            for (Item item : itemList) {
                totalCost += item.getPrice() * item.getQuantity();
                itemRepo.updatePurchaseStatus(item.getItemID(), true);
            }
            updateBudgetTracking(totalCost);
            runOnUiThread(this::loadUnpurchasedItems);

            // Spending Insights logic
            AppDatabase.databaseWriteExecutor.execute(() -> {
                Settings settings = AppDatabase.getDatabase(getApplicationContext()).settingsDao().getSettingsForUser((int) userId);
                if (settings != null && settings.isTrackSpending()) {
                    runOnUiThread(() -> {
                        String tip;
                        double percentage = (totalSpent / listBudget) * 100;
                        if (percentage > 100)
                            tip = "You went over budget. Consider reviewing your list next time.";
                        else if (percentage > 80)
                            tip = "You spent over 80% of your budget. You should consider reviewing your list.";
                        else
                            tip = "Great job staying within budget!";

                        new AlertDialog.Builder(this)
                                .setTitle("Spending Insight")
                                .setMessage(tip)
                                .setPositiveButton("OK", null)
                                .show();
                    });
                }
            });
        }).start());


        resetBudgetButton.setOnClickListener(v -> {
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(50, 40, 50, 10);

            final EditText budgetInput = new EditText(this);
            budgetInput.setHint("New budget limit (optional)");
            budgetInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            layout.addView(budgetInput);

            final CheckBox resetSpentCheckBox = new CheckBox(this);
            resetSpentCheckBox.setText("Reset total spent amount");
            layout.addView(resetSpentCheckBox);

            new AlertDialog.Builder(this)
                    .setTitle("Reset Budget & Spent")
                    .setView(layout)
                    .setPositiveButton("Confirm", (dialog, which) -> {
                        try {
                            String budgetVal = budgetInput.getText().toString().trim();
                            if (!budgetVal.isEmpty()) {
                                listBudget = Double.parseDouble(budgetVal);
                            }
                            if (resetSpentCheckBox.isChecked()) {
                                totalSpent = 0;
                            }
                            saveBudgetData();
                            updateBudgetUI();
                            Toast.makeText(this, "Budget updated successfully.", Toast.LENGTH_SHORT).show();
                        } catch (NumberFormatException e) {
                            Toast.makeText(this, "Invalid input.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        priceCompareButton.setOnClickListener(v -> {
            new Thread(() -> {
                StringBuilder sb = new StringBuilder();
                for (Item it : itemList) {
                    List<ItemPriceHistory> histList = storePriceMap.get(selectedStoreId);
                    if (histList != null) {
                        histList.stream().filter(h -> h.getItemID() == it.getItemID()).findFirst()
                                .ifPresent(h -> sb.append(it.getItemName()).append(": $").append(h.getPrice()).append("\n"));
                    }
                }
                runOnUiThread(() -> new AlertDialog.Builder(this)
                        .setTitle("Price Comparison")
                        .setMessage(sb.length() > 0 ? sb.toString() : "No price history.")
                        .setPositiveButton("OK", null)
                        .show());
            }).start();
        });
    }

    private void setupStores() {
        List<Store> defaultStores = Arrays.asList(
                new Store(1, "Walmart", ""),
                new Store(2, "Loblaws", ""),
                new Store(3, "SuperStore", ""),
                new Store(4, "No Frills", "")
        );
        ArrayAdapter<Store> storeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, defaultStores);
        storeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        storeSpinner.setAdapter(storeAdapter);
        selectedStoreId = defaultStores.get(0).getStoreID();
    }

    private void loadBudgetData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        listBudget = prefs.getFloat("custom_budget_" + selectedListId, 100f);
        totalSpent = prefs.getFloat("custom_spent_" + selectedListId, 0f);
        runOnUiThread(this::updateBudgetUI);
    }

    private void saveBudgetData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit()
                .putFloat("custom_budget_" + selectedListId, (float) listBudget)
                .putFloat("custom_spent_" + selectedListId, (float) totalSpent)
                .apply();
    }

    private void updateBudgetTracking(double expense) {
        totalSpent += expense;
        saveBudgetData();
        runOnUiThread(this::updateBudgetUI);
    }

    private void updateBudgetUI() {
        int progress = (int) ((totalSpent / listBudget) * 100);
        budgetProgressBar.setProgress(Math.min(progress, 100));
        totalSpentText.setText(String.format("Spent: $%.2f of $%.2f", totalSpent, listBudget));

        if (totalSpent > listBudget) {
            budgetStatusText.setText("OVER BUDGET");
            budgetStatusText.setTextColor(getResources().getColor(R.color.colorError, getTheme()));
            budgetProgressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorError, getTheme())));
        } else if (totalSpent > (listBudget * 0.8)) {
            budgetStatusText.setText("APPROACHING LIMIT");
            budgetStatusText.setTextColor(getResources().getColor(R.color.colorWarning, getTheme()));
            budgetProgressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWarning, getTheme())));
        } else {
            budgetStatusText.setText("WITHIN BUDGET");
            budgetStatusText.setTextColor(getResources().getColor(R.color.colorSuccess, getTheme()));
            budgetProgressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSuccess, getTheme())));
        }
    }


    private void loadGroceryLists() {
        new Thread(() -> {
            List<GroceryList> lists = AppDatabase.getDatabase(getApplicationContext())
                    .groceryListDao().getListsForUser((int) userId);
            runOnUiThread(() -> {
                ArrayAdapter<GroceryList> adapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, lists);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                listSpinner.setAdapter(adapter);
            });
        }).start();
    }

    private void loadUnpurchasedItems() {
        new Thread(() -> {
            List<Item> items = itemRepo.getUnpurchasedItemsForList(selectedListId);
            runOnUiThread(() -> {
                itemList.clear();
                if (items != null) itemList.addAll(items);
                itemAdapter.notifyDataSetChanged();

                generatePriceHistoryForItems(items);

            });
        }).start();

    }

    private void generatePriceHistoryForItems(List<Item> items) {
        long now = System.currentTimeMillis();
        storePriceMap.clear();

        for (int storeId = 1; storeId <= 4; storeId++) {
            List<ItemPriceHistory> histories = new ArrayList<>();
            for (Item item : items) {
                double price = 1 + new Random().nextInt(10); // Random generated price
                histories.add(new ItemPriceHistory(0, item.getItemID(), storeId, price, now));
            }
            storePriceMap.put(storeId, histories);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
