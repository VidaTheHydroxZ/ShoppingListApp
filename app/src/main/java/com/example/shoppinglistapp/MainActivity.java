package com.example.shoppinglistapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity
{
    List<String> shoppingList;
    ArrayAdapter<String> arrayAdapter;
    ArrayAdapter<CharSequence> storeAdapter;
    ArrayAdapter<CharSequence> daysAdapter;
    ListView listView;
    EditText editText;
    Spinner storeSpinner;
    Spinner daysSpinner;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storeSpinner = findViewById(R.id.spinnerStores);
        storeAdapter = ArrayAdapter.createFromResource(this, R.array.stores, R.layout.store_list_view_layout);
        storeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        storeSpinner.setAdapter(storeAdapter);
        storeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String store = adapterView.getItemAtPosition(position).toString();
                Toast.makeText(MainActivity.this, "Selected store is: " + store, Toast.LENGTH_SHORT).show();
                shoppingList.clear();
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        daysSpinner = findViewById(R.id.spinnerDays);
        daysAdapter = ArrayAdapter.createFromResource(this, R.array.days, R.layout.store_list_view_layout);
        daysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daysSpinner.setAdapter(daysAdapter);
        daysSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String day = adapterView.getItemAtPosition(position).toString();
                Toast.makeText(MainActivity.this, "Selected day is: " + day, Toast.LENGTH_SHORT).show();
                shoppingList.clear();
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        shoppingList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, R.layout.list_view_layout, shoppingList);
        listView = findViewById(R.id.shoppingListView);

        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view;
                textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        });

        editText = findViewById(R.id.shoppingItem);
        setTitle("Shopping List");
    }

    public void addItemToList(View view)
    {
        String input = editText.getText().toString();

        shoppingList.add(input);
        arrayAdapter.notifyDataSetChanged();
        Toast.makeText(this, "You added " + input +
                " to the list!", Toast.LENGTH_SHORT).show();

        editText.setText("");
    }

    public void saveView(View view)
    {

    }
}
