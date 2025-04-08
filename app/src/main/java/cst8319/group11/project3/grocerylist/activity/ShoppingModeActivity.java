package cst8319.group11.project3.grocerylist.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.EditText;

import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import cst8319.group11.project3.grocerylist.R;
import cst8319.group11.project3.grocerylist.models.Item;
import cst8319.group11.project3.grocerylist.repositories.ItemRepository;

public class ShoppingModeActivity extends AppCompatActivity {

    private int listID;
    private ItemRepository itemRepository;
    private List<Item> itemList;
    private ListView listViewItems;
    private Button buttonCompleteShopping;
    private TextView tvTotalSpending;
    private EditText etPriceInput;

    //  Budget tracking constants and variable for current spending.
    private static final double BUDGET_LIMIT = 300.0;
    private double currentSpending = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_mode);

        listID = getIntent().getIntExtra("listID", -1);
        itemRepository = new ItemRepository(getApplication());

        listViewItems = findViewById(R.id.listViewItems);
        buttonCompleteShopping = findViewById(R.id.buttonCompleteShopping);
        tvTotalSpending = findViewById(R.id.tvTotalSpending);
        etPriceInput = findViewById(R.id.etPriceInput);

        loadItemsForList(listID);

        buttonCompleteShopping.setOnClickListener(view -> {
            Toast.makeText(this, "Shopping Completed!", Toast.LENGTH_SHORT).show();
            finish();
        });

        listViewItems.setOnItemClickListener((parent, view, position, id) -> {
            Item selectedItem = itemList.get(position);
            selectedItem.togglePurchased();
            itemRepository.updateItem(selectedItem);
            Toast.makeText(this, selectedItem.getItemName() + " purchased = " + selectedItem.isPurchased(),
                    Toast.LENGTH_SHORT).show();
            // Reload items after update
            loadItemsForList(listID);
        });
    }

    private void loadItemsForList(int listID) {
        itemList = itemRepository.getItemsForList(listID);
        if (itemList != null) {
            String[] itemNames = new String[itemList.size()];
            for (int i = 0; i < itemList.size(); i++) {
                Item it = itemList.get(i);
                itemNames[i] = it.getItemName()
                        + " | Brand: " + it.getBrand()
                        + " | Price: $" + it.getPrice()
                        + " | Qty: " + it.getQuantity()
                        + " | " + (it.isPurchased() ? "[Purchased]" : "[Not yet Purchased]");
            }
            android.widget.ArrayAdapter<String> adapter =
                    new android.widget.ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemNames);
            listViewItems.setAdapter(adapter);
            //  Update budget spending display.
            updateBudgetStatus();
        }
    }

    // Calculates the total spending, then updates the UI and checks against the budget limit.

    private void updateBudgetStatus() {
        List<Item> purchasedItems = itemRepository.getPurchasedItemsForList(listID);
        double total = 0.0;
        if (purchasedItems != null) {
            for (Item item : purchasedItems) {
                total += item.getPrice();
            }
        }
        currentSpending = total;
        tvTotalSpending.setText(String.format("Total Spending: $%.2f", currentSpending));
        // Check if spending exceeds the budget limit and alert the user
        if (currentSpending >= BUDGET_LIMIT) {
            showBudgetAlert();
        }
    }

    //Displays an alert dialog if the current spending exceeds the budget limit.

    private void showBudgetAlert() {
        new AlertDialog.Builder(this)
                .setTitle("Budget Alert")
                .setMessage("You exceeded your budget limit, $" + BUDGET_LIMIT)
                .setPositiveButton("OK", null)
                .show();
    }
}