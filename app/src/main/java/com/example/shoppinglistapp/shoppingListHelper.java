package com.example.shoppinglistapp;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.ArrayList;

public class shoppingListHelper {
    private HashMap<String, HashMap<String, ArrayList<String>>> storeDayGroceries;

    public shoppingListHelper()
    {
        storeDayGroceries = new HashMap<>();
    }

    public void addGrocery(String store, String day, String grocery)
    {
        storeDayGroceries.computeIfAbsent(store, k -> new HashMap<>()).computeIfAbsent(day, k -> new ArrayList<>()).add(grocery);
    }

    public ArrayList<String> getGroceries(String store, String day)
    {
        return storeDayGroceries.getOrDefault(store, new HashMap<>()).getOrDefault(day, new ArrayList<>());
    }
}
