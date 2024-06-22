package com.example.shoppinglistapp;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {GroceryItem.class}, version = 1)
public abstract class GroceryDatabase extends RoomDatabase {
    public abstract  GroceryDao groceryDao();

    private static volatile GroceryDatabase INSTANCE;

    public static GroceryDatabase getDatabase(final Context context)
    {
        if (INSTANCE == null)
        {
            synchronized (GroceryDatabase.class)
            {
                if (INSTANCE == null)
                {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), GroceryDatabase.class, "grocery_database").fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }
}
