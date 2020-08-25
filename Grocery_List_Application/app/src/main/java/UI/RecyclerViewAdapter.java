package UI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.example.grocery_list_application.DetailsActivity;
import com.example.grocery_list_application.R;

import java.util.List;

import data.DatabaseHandler;
import model.Grocery;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<Grocery> groceryItems;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog alertDialog;
    private LayoutInflater inflater;

    public RecyclerViewAdapter(Context context, List<Grocery> groceryItems) {
        this.context = context;
        this.groceryItems = groceryItems;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row,parent,false);
        return new ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        Grocery grocery = groceryItems.get(position);

        holder.groceryItem.setText(grocery.getName());
        holder.groceryQuantity.setText(String.valueOf(grocery.getQuantity()));
        holder.groceryAddedDate.setText(grocery.getDateItemAdded());
    }

    @Override
    public int getItemCount() {
        return groceryItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView groceryItem;
        public TextView groceryQuantity;
        public TextView groceryAddedDate;
        public Button editButton;
        public Button deleteButton;
        //public int id;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;

            groceryItem = (TextView) itemView.findViewById(R.id.txtName);
            groceryQuantity = (TextView) itemView.findViewById(R.id.txtQuantityNo);
            groceryAddedDate = (TextView) itemView.findViewById(R.id.txtDateAdded);
            editButton = (Button) itemView.findViewById(R.id.editButton);
            deleteButton = (Button) itemView.findViewById(R.id.deleteButton);

            editButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //goto next screen --> Details Activity
                    int position = getAdapterPosition();
                    Grocery grocery = groceryItems.get(position);
                    Intent intent = new Intent(context, DetailsActivity.class);
                    intent.putExtra("id",grocery.getId());
                    intent.putExtra("name", grocery.getName());
                    intent.putExtra("quantity",grocery.getQuantity());
                    intent.putExtra("date",grocery.getDateItemAdded());

                    context.startActivity(intent);

                }
            });
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.editButton:
                    int position1 = getAdapterPosition();
                    Grocery grocery1 = groceryItems.get(position1);
                    editItem(grocery1);
                    break;
                case R.id.deleteButton:
                    int position2 = getAdapterPosition();
                    Grocery grocery2 = groceryItems.get(position2);
                    deleteItem(grocery2.getId());
                    break;
            }
        }

        public void editItem(final Grocery grocery){
            dialogBuilder = new AlertDialog.Builder(context);
            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.popup,null);

            final EditText groceryItem = (EditText) view.findViewById(R.id.groceryItem);
            final EditText groceryQuantity = (EditText) view.findViewById(R.id.groceryQuantity);

            Button saveButton = (Button) view.findViewById(R.id.saveButton);

            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText("Edit Grocery Item");

            dialogBuilder.setView(view);

            alertDialog = dialogBuilder.create();
            alertDialog.show();

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if( !(groceryItem.getText().toString().isEmpty()) && !(groceryQuantity.getText().toString().isEmpty()) ){
                        if(Integer.parseInt(groceryQuantity.getText().toString())>0) {
                            DatabaseHandler myDB = new DatabaseHandler(context);
                            grocery.setName(groceryItem.getText().toString());
                            grocery.setQuantity(Integer.parseInt(groceryQuantity.getText().toString()));
                            boolean isUpdated = myDB.updateGrocery(grocery);
                            if (isUpdated) {
                                Toast.makeText(context, "Grocery Item updated!", Toast.LENGTH_LONG).show();
                                notifyItemChanged(getAdapterPosition(), grocery);
                                alertDialog.dismiss();
                            } else {
                                Toast.makeText(context, "Updation Failed!", Toast.LENGTH_LONG).show();
                                alertDialog.dismiss();
                            }
                        }
                        else
                            Snackbar.make(v,"Quantity cannot be 0.", Snackbar.LENGTH_LONG).show();
                    }
                    else
                        Toast.makeText(context,"Please fill both the fields!", Toast.LENGTH_LONG).show();
                }
            });
        }

        public void deleteItem(final int id){
            dialogBuilder = new AlertDialog.Builder(context);
            inflater = LayoutInflater.from(context);
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
                    DatabaseHandler myDB = new DatabaseHandler(context);
                    boolean isDeleted = myDB.deleteGrocery(id);  //to use id in inner class we have to make it final
                    if(isDeleted){
                        Toast.makeText(context,"Grocery Item Deleted!", Toast.LENGTH_LONG).show();
                        groceryItems.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                        alertDialog.dismiss();
                    }
                    else {
                        Toast.makeText(context, "Deletion Failed!", Toast.LENGTH_LONG).show();
                        alertDialog.dismiss();
                    }


                }
            });

        }
    }
}
