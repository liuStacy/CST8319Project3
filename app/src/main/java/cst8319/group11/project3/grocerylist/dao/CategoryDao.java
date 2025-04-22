package cst8319.group11.project3.grocerylist.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import cst8319.group11.project3.grocerylist.models.Category;

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
public interface CategoryDao {
    @Insert
    long insert(Category category);

    @Query("SELECT * FROM categories")
    List<Category> getAllCategories();
}