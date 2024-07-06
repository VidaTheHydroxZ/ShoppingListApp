package com.example.shoppinglistapp;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
{
    private GroceryDatabase db;
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
    private ImageButton moreOptions;
    private static MainActivity instance;
    private boolean paintFlagsOn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;
        paintFlagsOn = false;

        db = GroceryDatabase.getDatabase(getApplicationContext());

        storeSpinner = findViewById(R.id.spinnerStores);
        storeAdapter = ArrayAdapter.createFromResource(this, R.array.stores, R.layout.store_list_view_layout);
        storeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        storeSpinner.setAdapter(storeAdapter);

        addButton = findViewById(R.id.addItem);
        moreOptions = findViewById(R.id.moreOptionsButton);

        AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateListView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
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

        registerForContextMenu(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view;
                if(!paintFlagsOn)
                {
                    textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    paintFlagsOn = true;
                }
                else
                {
                    textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    paintFlagsOn = false;
                }
            }
        });

        shoppingItem = findViewById(R.id.shoppingItem);
        setTitle("Shopping List");

        addButton.setOnClickListener(v -> {
            String grocery = shoppingItem.getText().toString();
            if (!grocery.isEmpty())
            {
                GroceryItem groceryItem = new GroceryItem(selectedStore, selectedDay, grocery);
                new InsertGroceryAsyncTask(db.groceryDao()).execute(groceryItem);
                Toast.makeText(this, "Added " + grocery + " to the list!", Toast.LENGTH_SHORT).show();
                shoppingItem.setText("");
                updateListView();
            }
        });

        moreOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v)
            {
                showPopupMenu(v);
            }
        });


    }
    public void showPopupMenu(View view)
    {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.popupmenu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return handleMenuItemClick(item);
            }
        });
        popupMenu.show();
    }
     private boolean handleMenuItemClick(MenuItem item)
     {
         if (item.getItemId() == R.id.deleteAll)
         {
             String selectedStore = storeSpinner.getSelectedItem().toString();
             new DeleteAllGroceriesAsyncTask(db.groceryDao()).execute(selectedStore);
             Toast.makeText(this, "Deleted all items for the selected store!", Toast.LENGTH_SHORT).show();
             return true;
         }
         return false;
     }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected (MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        String originalGrocery = shoppingList.get(position);
        switch (item.getItemId())
        {
            case R.id.EditItem:
                showEditDialog(position, originalGrocery);
                return true;
            case R.id.DeleteItem:
                shoppingList.remove(position);
                arrayAdapter.notifyDataSetChanged();
                new DeleteGroceryAsyncTask(db.groceryDao()).execute(selectedStore, selectedDay, originalGrocery);
                Toast.makeText(this, "Selected grocery deleted!", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void showEditDialog(int position, String originalGrocery)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit grocery");

        final EditText input = new EditText(this);
        input.setText(originalGrocery);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String newGrocery = input.getText().toString();
            if(!newGrocery.isEmpty())
            {
                new RetrieveGroceryAsyncTask(db.groceryDao(), originalGrocery, position, newGrocery).execute(selectedStore, selectedDay);
                shoppingList.set(position, newGrocery);
                arrayAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Updated grocery to " + newGrocery, Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private static class RetrieveGroceryAsyncTask extends AsyncTask<Object, Void, GroceryItem> {
        private GroceryDao groceryDao;
        private String originalGrocery;
        private int position;
        private String newGrocery;

        RetrieveGroceryAsyncTask(GroceryDao groceryDao, String originalGrocery, int position, String newGrocery)
        {
            this.groceryDao = groceryDao;
            this.originalGrocery = originalGrocery;
            this.position = position;
            this.newGrocery = newGrocery;
        }

        @Override
        protected GroceryItem doInBackground(Object... params)
        {
            String store = (String) params[0];
            String day = (String) params[1];
            List<GroceryItem> groceries = groceryDao.getGroceriesForStoreAndDay(store, day);
            for (GroceryItem grocery : groceries)
            {
                if (grocery.getItem().equals(originalGrocery))
                {
                    return grocery;
                }
            }
            return  null;
        }

        @Override
        protected void onPostExecute(GroceryItem groceryItem)
        {
            if (groceryItem != null)
            {
                groceryItem.setItem(newGrocery);
                new UpdateGroceryAsyncTask(groceryDao).execute(groceryItem);
            }
        }
    }

    private static class InsertGroceryAsyncTask extends AsyncTask<GroceryItem, Void, Void>  {
        private GroceryDao groceryDao;

        InsertGroceryAsyncTask(GroceryDao groceryDao)
        {
            this.groceryDao = groceryDao;
        }

        @Override
        protected Void doInBackground(GroceryItem... groceryItems)
        {
            groceryDao.insert(groceryItems[0]);
            return null;
        }
    }

    private static class LoadGroceriesAsyncTask extends AsyncTask<String, Void, List<GroceryItem>> {
        private GroceryDao groceryDao;
        private MainActivity activity;

        LoadGroceriesAsyncTask(GroceryDao groceryDao, MainActivity activity)
        {
            this.groceryDao = groceryDao;
            this.activity = activity;
        }

        @Override
        protected List<GroceryItem> doInBackground(String... params)
        {
            return groceryDao.getGroceriesForStoreAndDay(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(List<GroceryItem> groceryItems)
        {
            super.onPostExecute(groceryItems);
            activity.shoppingList.clear();
            for (GroceryItem item : groceryItems)
            {
                activity.shoppingList.add(item.getItem());
            }
            activity.arrayAdapter.notifyDataSetChanged();
        }
    }

    private static class DeleteGroceryAsyncTask extends AsyncTask<String, Void, Void> {
        private GroceryDao groceryDao;

        DeleteGroceryAsyncTask(GroceryDao groceryDao)
        {
            this.groceryDao = groceryDao;
        }

        @Override
        protected Void doInBackground(String... params)
        {
            String store = params[0];
            String day = params[1];
            String grocery = params[2];
            groceryDao.delete(store, day, grocery);
            return null;
        }
    }

    private static class DeleteAllGroceriesAsyncTask extends AsyncTask<String, Void, Void> {
        private GroceryDao groceryDao;

        DeleteAllGroceriesAsyncTask(GroceryDao groceryDao) {
            this.groceryDao = groceryDao;
        }

        @Override
        protected Void doInBackground(String... params) {
            String store = params[0];
            groceryDao.deleteAllGroceriesForStore(store);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            instance.updateListView();
        }
    }

    private static class UpdateGroceryAsyncTask extends AsyncTask<GroceryItem, Void, Void> {
        private GroceryDao groceryDao;

        UpdateGroceryAsyncTask(GroceryDao groceryDao)
        {
            this.groceryDao = groceryDao;
        }

        @Override
        protected Void doInBackground(GroceryItem... groceryItems)
        {
            groceryDao.update(groceryItems[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            instance.updateListView();
        }
    }


    public void updateListView()
    {
        selectedStore = storeSpinner.getSelectedItem().toString();
        selectedDay = daysSpinner.getSelectedItem().toString();
        new LoadGroceriesAsyncTask(db.groceryDao(), this).execute(selectedStore, selectedDay);
    }
}
