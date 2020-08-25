package com.example.grocery_list_application;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import data.DatabaseHandler;
import model.Grocery;

public class MainActivity extends AppCompatActivity {

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog alertDialog;
    private EditText groceryItem;
    private EditText groceryQuantity;
    private Button saveButton;

    private DatabaseHandler myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myDB = new DatabaseHandler(this);
        byPassActivity();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createPopupDialog(){
        dialogBuilder = new AlertDialog.Builder(MainActivity.this);

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
                if( !(groceryItem.getText().toString().isEmpty()) && !(groceryQuantity.getText().toString().isEmpty()) )
                    if(Integer.parseInt(groceryQuantity.getText().toString())>0)
                        saveToDB(v);
                    else
                        Snackbar.make(v,"Quantity cannot be 0.",Snackbar.LENGTH_LONG).show();
                else
                    Toast.makeText(MainActivity.this,"Please fill both the fields!",Toast.LENGTH_LONG).show();

            }
        });
    }

    public void saveToDB(View v){
        String name = groceryItem.getText().toString();
        int quantity = Integer.parseInt(groceryQuantity.getText().toString());

        Grocery grocery = new Grocery(name,quantity);

        boolean isInserted = myDB.addGrocery(grocery);
        if(isInserted)
            Snackbar.make(v,"Grocery Item Added!",Snackbar.LENGTH_LONG).show();
        else
            Toast.makeText(MainActivity.this,"Insertion Failed!",Toast.LENGTH_LONG).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                alertDialog.dismiss();
                startActivity(new Intent(MainActivity.this,ListActivity.class));
            }
        },1500);   //1.5 second
    }

    public void byPassActivity(){
        //Check if database is empty; if not, then we just go to ListActivity and show all added items
        if(myDB.getGroceriesCount()>0){
            startActivity(new Intent(MainActivity.this,ListActivity.class));
            finish();   //previous activity finish hujyegi or back krengy tou previous activity pe ni jyega
        }
    }
}

