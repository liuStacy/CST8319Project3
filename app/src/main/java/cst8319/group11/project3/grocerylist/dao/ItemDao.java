package cst8319.group11.project3.grocerylist.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import cst8319.group11.project3.grocerylist.models.Item;


@Dao
public interface ItemDao {

    @Insert
    long insert(Item item);

    @Update
    void update(Item item);

    @Query("DELETE FROM items WHERE itemID = :itemID")
    void delete(int itemID);

    @Query("SELECT * FROM items WHERE listID = :listID")
    List<Item> getItemsForList(int listID);

    @Query("UPDATE items SET purchased = :purchased WHERE itemID = :itemID")
    void updatePurchaseStatus(int itemID, boolean purchased);

    @Query("UPDATE items SET price = :price, quantity = :quantity, brand = :brand WHERE itemID = :itemID")
    void updateItemDetails(int itemID, double price, int quantity, String brand);

    // ✅ For budget/spending tracker
    @Query("SELECT * FROM items WHERE listID = :listID AND purchased = 1")
    List<Item> getPurchasedItemsForList(int listID);

    // ✅ For reminder notification
    @Query("SELECT * FROM items WHERE listID = :listID AND purchased = 0")
    List<Item> getUnpurchasedItemsForList(int listID);


    // ✅ In ItemDao.java
    @Query("SELECT * FROM items WHERE purchased = 0 AND listID IN (SELECT listID FROM grocery_lists WHERE userID = :userId)")
    List<Item> getUnpurchasedItemsForUser(int userId);


}
