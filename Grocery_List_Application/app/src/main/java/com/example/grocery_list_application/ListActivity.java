package com.example.grocery_list_application;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import UI.RecyclerViewAdapter;
import data.DatabaseHandler;
import model.Grocery;

public class ListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private List<Grocery> groceryList;
    private List<Grocery> listItems;
    private DatabaseHandler myDB;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog alertDialog;
    private EditText groceryItem;
    private EditText groceryQuantity;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myDB = new DatabaseHandler(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        groceryList = new ArrayList<>();
        listItems = new ArrayList<>();

        groceryList = myDB.getAllGroceries();
        for(Grocery c: groceryList){
            Grocery grocery = new Grocery();
            grocery.setName(c.getName());
            grocery.setQuantity(c.getQuantity());
            grocery.setId(c.getId());
            grocery.setDateItemAdded("Added on: "+c.getDateItemAdded());
            listItems.add(grocery);
        }

        recyclerViewAdapter = new RecyclerViewAdapter(ListActivity.this,listItems);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                createPopupDialog();
            }
        });
    }


    private void createPopupDialog(){
        dialogBuilder = new AlertDialog.Builder(ListActivity.this);

        View view = getLayoutInflater().inflate(R.layout.popup,null);
        groceryItem = (EditText) view.findViewById(R.id.groceryItem);
        groceryQuantity = (EditText) view.findViewById(R.id.groceryQuantity);
        saveButton = (Button) view.findViewById(R.id.saveButton);

        dialogBuilder.setView(view);

        alertDialog = dialogBuilder.create();
        alertDialog.show();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToDo: Save to db
                //ToDo: Goto next screen
                if( !(groceryItem.getText().toString().isEmpty()) && !(groceryQuantity.getText().toString().isEmpty()) ){
                    if(Integer.parseInt(groceryQuantity.getText().toString())>0)
                        saveToDB(v);
                    else
                        Snackbar.make(v,"Quantity cannot be 0.",Snackbar.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(ListActivity.this,"Please fill both the fields!",Toast.LENGTH_LONG).show();

            }
        });
    }

    public void saveToDB(View v){
        String name = groceryItem.getText().toString();
        int quantity = Integer.parseInt(groceryQuantity.getText().toString());

        Grocery grocery = new Grocery(name,quantity);

        boolean isInserted = myDB.addGrocery(grocery);
        if(isInserted) {
            Snackbar.make(v, "Grocery Item Added!", Snackbar.LENGTH_LONG).show();
            alertDialog.dismiss();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    alertDialog.dismiss();
                    //start a new activity
                    startActivity(new Intent(ListActivity.this, ListActivity.class));
                    finish();
                }
            }, 1200); //  1 second.

        }
        else
            Toast.makeText(ListActivity.this,"Insertion Failed!",Toast.LENGTH_LONG).show();

    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

}
