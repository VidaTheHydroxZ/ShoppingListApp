package com.example.shoppinglistapp;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
{
    private shoppingListHelper shoppingListHelper;
    private ArrayList<String> shoppingList;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayAdapter<CharSequence> storeAdapter;
    private ArrayAdapter<CharSequence> daysAdapter;
    private ListView listView;
    private EditText shoppingItem;
    private Spinner storeSpinner;
    private Spinner daysSpinner;
    private String  selectedStore, selectedDay;
    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shoppingListHelper = new shoppingListHelper();

        storeSpinner = findViewById(R.id.spinnerStores);
        storeAdapter = ArrayAdapter.createFromResource(this, R.array.stores, R.layout.store_list_view_layout);
        storeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        storeSpinner.setAdapter(storeAdapter);

        addButton = findViewById(R.id.addItem);

        AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateListView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        daysSpinner = findViewById(R.id.spinnerDays);
        daysAdapter = ArrayAdapter.createFromResource(this, R.array.days, R.layout.store_list_view_layout);
        daysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daysSpinner.setAdapter(daysAdapter);
        storeSpinner.setOnItemSelectedListener(onItemSelectedListener);
        daysSpinner.setOnItemSelectedListener(onItemSelectedListener);

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

        shoppingItem = findViewById(R.id.shoppingItem);
        setTitle("Shopping List");

        addButton.setOnClickListener(v -> {
            String grocery = shoppingItem.getText().toString();
            if (!grocery.isEmpty())
            {
                shoppingListHelper.addGrocery(selectedStore, selectedDay, grocery);
                Toast.makeText(this, "Added " + grocery + "to the list!", Toast.LENGTH_SHORT).show();
                shoppingItem.setText("");
                updateListView();
            }
        });
    }


    public void updateListView()
    {
        selectedStore = storeSpinner.getSelectedItem().toString();
        selectedDay = daysSpinner.getSelectedItem().toString();
        shoppingList.clear();
        shoppingList.addAll(shoppingListHelper.getGroceries(selectedStore, selectedDay));
        arrayAdapter.notifyDataSetChanged();
    }
}
