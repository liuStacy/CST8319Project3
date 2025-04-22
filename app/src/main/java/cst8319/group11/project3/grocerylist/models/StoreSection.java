package cst8319.group11.project3.grocerylist.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;


// New StoreSection.java model
@Entity(tableName = "store_sections",
        foreignKeys = @ForeignKey(entity = Store.class,
                parentColumns = "storeID",
                childColumns = "storeID",
                onDelete = ForeignKey.CASCADE))
public class StoreSection {
    @PrimaryKey(autoGenerate = true)
    private int sectionID;
    private int storeID;
    private String sectionName;
    private int displayOrder;

    // Constructors, getters, setters
    public StoreSection(int sectionID, int storeID, String sectionName, int displayOrder) {
        this.sectionID = sectionID;
        this.storeID = storeID;
        this.sectionName = sectionName;
        this.displayOrder = displayOrder;
    }

    public int getSectionID() {
        return sectionID;
    }

    public void setSectionID(int sectionID) {
        this.sectionID = sectionID;
    }

    public int getStoreID() {
        return storeID;
    }

    public void setStoreID(int storeID) {
        this.storeID = storeID;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;

    }


}