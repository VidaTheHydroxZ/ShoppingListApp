package com.example.shoppinglistapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GroceryDao {
    @Insert
    void insert(GroceryItem groceryItem);

    @Query("SELECT * FROM grocery_table WHERE store = :store AND day = :day")
    List<GroceryItem> getGroceriesForStoreAndDay(String store, String day);

    @Query("DELETE FROM grocery_table")
    void deleteAll();
}
