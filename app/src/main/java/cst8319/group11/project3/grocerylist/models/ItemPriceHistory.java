package cst8319.group11.project3.grocerylist.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

// New ItemPriceHistory.java model
@Entity(tableName = "item_price_history",
        foreignKeys = {@ForeignKey(entity = Item.class,
                parentColumns = "itemID",
                childColumns = "itemID",
                onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Store.class,
                        parentColumns = "storeID",
                        childColumns = "storeID",
                        onDelete = ForeignKey.CASCADE)})
public class ItemPriceHistory {
    @PrimaryKey(autoGenerate = true)
    private int historyID;
    private int itemID;
    private int storeID;
    private double price;
    private long recordDate;

    // Constructors, getters, setters

    public ItemPriceHistory(int historyID, int itemID, int storeID, double price, long recordDate) {
        this.historyID = historyID;
        this.itemID = itemID;
        this.storeID = storeID;
        this.price = price;
        this.recordDate = recordDate;
    }

    public int getHistoryID() {
        return historyID;
    }

    public void setHistoryID(int historyID) {
        this.historyID = historyID;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public int getStoreID() {
        return storeID;
    }

    public void setStoreID(int storeID) {
        this.storeID = storeID;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(long recordDate) {
        this.recordDate = recordDate;
    }
}