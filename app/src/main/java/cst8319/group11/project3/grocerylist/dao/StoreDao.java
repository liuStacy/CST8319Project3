package cst8319.group11.project3.grocerylist.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;


import java.util.List;

import cst8319.group11.project3.grocerylist.models.Store;


@Dao
public interface StoreDao {
    @Insert
    long insert(Store store);

    @Query("SELECT * FROM stores")
    List<Store> getAllStores();

    @Query("SELECT * FROM stores WHERE storeID = :storeID")
    Store getStoreById(int storeID);
}
