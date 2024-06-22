package com.example.shoppinglistapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "grocery_table")
public class GroceryItem {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String store;
    private String day;
    private String item;


    public GroceryItem(String store, String day, String item)
    {
        this.store = store;
        this.day = day;
        this.item = item;
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public String getStore() { return store;}
    public void setStore(String store) {this.store = store;}

    public String getDay() {return day;}
    public void setDay (String day) {this.day = day;}

    public String getItem() {return item;}
    public void setItem(String item) {this.item = item;}

}
