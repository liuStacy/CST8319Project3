package cst8319.group11.project3.grocerylist.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import cst8319.group11.project3.grocerylist.models.Settings;

/*
 * Author: Rongrong Liu
 * File Name: SettingsDao.java
 * Group: 11
 * Project: Grocery List
 * Due Date: 04/22/2025
 * Created Date: 04/10/2025
 *
 * */
@Dao
public interface SettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(Settings settings);

    @Query("SELECT * FROM settings WHERE userID = :userID")
    Settings getSettingsForUser(long userID);
}