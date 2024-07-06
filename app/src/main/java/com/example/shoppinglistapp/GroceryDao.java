package com.example.shoppinglistapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface GroceryDao {
    @Insert
    void insert(GroceryItem groceryItem);

    @Query("SELECT * FROM grocery_table WHERE store = :store AND day = :day")
    List<GroceryItem> getGroceriesForStoreAndDay(String store, String day);

    @Query("DELETE FROM grocery_table WHERE store = :store AND day = :day AND item = :item")
    void delete(String store, String day, String item);

    @Query("DELETE FROM grocery_table WHERE store =:store")
    void deleteAllGroceriesForStore(String store);

    @Update
    void update(GroceryItem groceryItem);
}
