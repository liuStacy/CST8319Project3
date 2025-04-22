package cst8319.group11.project3.grocerylist.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java. util. List;

import cst8319.group11.project3.grocerylist.models.StoreSection;

/*
 * Author: Rongrong Liu
 * File Name: StoreSectionDao.java
 * Group: 11
 * Project: Grocery List
 * Due Date: 04/22/2025
 * Created Date: 03/10/2025
 *
 * */
@Dao
public interface StoreSectionDao {
    @Insert
    long insert(StoreSection section);

    @Query("SELECT * FROM store_sections WHERE storeID = :storeID ORDER BY displayOrder")
    List<StoreSection> getSectionsForStore(int storeID);
}
