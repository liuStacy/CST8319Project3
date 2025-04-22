package cst8319.group11.project3.grocerylist.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

import cst8319.group11.project3.grocerylist.models.ItemPriceHistory;

/*
 * Author: Rongrong Liu
 * File Name: ItemPriceHistoryDao.java
 * Group: 11
 * Project: Grocery List
 * Due Date: 04/22/2025
 * Created Date: 04/10/2025
 *
 * */
@Dao
public interface ItemPriceHistoryDao {
    @Insert
    long insert(ItemPriceHistory priceHistory);

    @Query("SELECT * FROM item_price_history WHERE itemID = :itemID ORDER BY recordDate DESC")
    List<ItemPriceHistory> getPriceHistoryForItem(int itemID);

    @Query("SELECT * FROM item_price_history WHERE itemID = :itemID AND storeID = :storeID ORDER BY recordDate DESC LIMIT 1")
    ItemPriceHistory getLatestPriceForItemInStore(int itemID, int storeID);
}