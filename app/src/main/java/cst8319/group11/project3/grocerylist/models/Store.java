package cst8319.group11.project3.grocerylist.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "stores")
public class Store {
    @PrimaryKey(autoGenerate = true)
    private int storeID;
    private String storeName;
    private String address;

    // Constructors
    public Store(int storeID, String storeName, String address) {
        this.storeID = storeID;
        this.storeName = storeName;
        this.address = address;
    }

    // Add a no-arg constructor if needed by Room
    public Store() {
    }

    // Getter and Setter methods
    public int getStoreID() {
        return storeID;
    }

    public void setStoreID(int storeID) {
        this.storeID = storeID;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // IMPORTANT: Override toString for proper display in spinner
    @Override
    public String toString() {
        return storeName;
    }
}