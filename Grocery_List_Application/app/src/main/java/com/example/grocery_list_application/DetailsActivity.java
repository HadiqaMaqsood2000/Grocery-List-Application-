package com.example.grocery_list_application;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import data.DatabaseHandler;
import model.Grocery;


public class DetailsActivity extends AppCompatActivity {

    private TextView itemName;
    private TextView itemQuantity;
    private TextView itemAddedDate;
    private Button editButton;
    private Button deleteButton;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog alertDialog;
    private LayoutInflater inflater;

    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        itemName = (TextView) findViewById(R.id.itemName);
        itemQuantity = (TextView) findViewById(R.id.itemQuantity);
        itemAddedDate = (TextView) findViewById(R.id.itemAddedDate);
        editButton = (Button) findViewById(R.id.editButton);
        deleteButton = (Button) findViewById(R.id.deleteButton);

        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            id = extras.getInt("id");
            String name = extras.getString("name");
            int quantity = extras.getInt("quantity");
            String date = extras.getString("date");

            itemName.setText(name);
            itemQuantity.setText(String.valueOf(quantity));
            itemAddedDate.setText(date);
        }

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update grocery
                editItem();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete grocery
                deleteItem(id);
            }
        });
    }

    public void editItem(){
        dialogBuilder = new AlertDialog.Builder(DetailsActivity.this);
        inflater = LayoutInflater.from(DetailsActivity.this);
        View view = inflater.inflate(R.layout.popup,null);

        final EditText groceryItem = (EditText) view.findViewById(R.id.groceryItem);
        final EditText groceryQuantity = (EditText) view.findViewById(R.id.groceryQuantity);

        Button saveButton = (Button) view.findViewById(R.id.saveButton);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText("Edit Grocery Item");

        dialogBuilder.setView(view);

        alertDialog = dialogBuilder.create();
        alertDialog.show();

        final Grocery grocery = new Grocery();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( !(groceryItem.getText().toString().isEmpty()) && !(groceryQuantity.getText().toString().isEmpty()) ){
                    if(Integer.parseInt(groceryQuantity.getText().toString())>0) {
                        DatabaseHandler myDB = new DatabaseHandler(DetailsActivity.this);
                        grocery.setName(groceryItem.getText().toString());
                        grocery.setQuantity(Integer.parseInt(groceryQuantity.getText().toString()));
                        grocery.setId(id);
                        boolean isUpdated = myDB.updateGrocery(grocery);
                        if (isUpdated) {
                            Toast.makeText(DetailsActivity.this, "Grocery Item updated!", Toast.LENGTH_LONG).show();
                            alertDialog.dismiss();
                            Grocery grocery = myDB.getGrocery(id);
                            itemName.setText(grocery.getName());
                            itemQuantity.setText(String.valueOf(grocery.getQuantity()));
                            itemAddedDate.setText(grocery.getDateItemAdded());
                        } else {
                            Toast.makeText(DetailsActivity.this, "Updation Failed!", Toast.LENGTH_LONG).show();
                            alertDialog.dismiss();
                        }
                    }
                    else
                        Snackbar.make(v,"Quantity cannot be 0.", Snackbar.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(DetailsActivity.this,"Please fill both the fields!",Toast.LENGTH_LONG).show();
            }
        });
    }

    public void deleteItem(final int id){
        dialogBuilder = new AlertDialog.Builder(DetailsActivity.this);
        inflater = LayoutInflater.from(DetailsActivity.this);
        View view = inflater.inflate(R.layout.confirmation_dialog,null);

        Button noButton = (Button) view.findViewById(R.id.noButton);
        Button yesButton = (Button) view.findViewById(R.id.yesButton);

        dialogBuilder.setView(view);

        alertDialog = dialogBuilder.create();
        alertDialog.show();

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Delete the item
                DatabaseHandler myDB = new DatabaseHandler(DetailsActivity.this);
                boolean isDeleted = myDB.deleteGrocery(id);  //to use id in inner class we have to make it final
                if(isDeleted){
                    Toast.makeText(DetailsActivity.this,"Grocery Item Deleted!",Toast.LENGTH_LONG).show();
                    alertDialog.dismiss();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(DetailsActivity.this,ListActivity.class));
                            finish();
                        }
                    },1000);   //1second
                }
                else {
                    Toast.makeText(DetailsActivity.this, "Deletion Failed!", Toast.LENGTH_LONG).show();
                    alertDialog.dismiss();
                }


            }
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(DetailsActivity.this,ListActivity.class));
        finish();
    }
}
