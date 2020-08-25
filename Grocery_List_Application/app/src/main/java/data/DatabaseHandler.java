package data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.Grocery;
import util.Constants;

public class DatabaseHandler extends SQLiteOpenHelper {

    private Context context;

    public DatabaseHandler(@Nullable Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_GROCERY_TABLE = "CREATE TABLE "+Constants.TABLE_NAME+"("+Constants.KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+Constants.GROCERY_ITEM+
                                      " TEXT,"+Constants.GROCERY_QUANTITY+" INTEGER,"+Constants.DATE+" LONG);";
        db.execSQL(CREATE_GROCERY_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+Constants.TABLE_NAME);
        onCreate(db);
    }

    /**
     * CRUD Operations: Create, Read, Update, Delete
     */

    //Add Grocery
    public boolean addGrocery(Grocery grocery){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.GROCERY_ITEM,grocery.getName());
        contentValues.put(Constants.GROCERY_QUANTITY,grocery.getQuantity());
        contentValues.put(Constants.DATE, System.currentTimeMillis());

        long i = db.insert(Constants.TABLE_NAME,null,contentValues);
        db.close();
        if(i!=-1)
            return true;
        return false;
    }

    //Get Grocery
    public Grocery getGrocery(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Constants.TABLE_NAME,new String[]{String.valueOf(Constants.KEY_ID),Constants.GROCERY_ITEM, String.valueOf(Constants.GROCERY_QUANTITY),
                                Constants.DATE}, Constants.KEY_ID+" = ? ",new String[]{String.valueOf(id)},null,null,null,null);

        if(cursor!=null){
            cursor.moveToNext();

            Grocery grocery = new Grocery();
            grocery.setId(cursor.getInt(cursor.getColumnIndex(Constants.KEY_ID)));
            grocery.setName(cursor.getString(cursor.getColumnIndex(Constants.GROCERY_ITEM)));
            grocery.setQuantity(cursor.getInt(cursor.getColumnIndex(Constants.GROCERY_QUANTITY)));

            //convert timestamp to something readable
            DateFormat dateFormat = DateFormat.getDateInstance();
            String formatedDate = dateFormat.format(new Date( cursor.getLong(cursor.getColumnIndex(Constants.DATE)) ).getTime());
            grocery.setDateItemAdded(formatedDate);

            return grocery;
        }

        return null;
    }

    //Get All Groceries
    public List<Grocery> getAllGroceries(){
        SQLiteDatabase db = this.getReadableDatabase();

        List<Grocery> groceryList = new ArrayList<>();

        Cursor cursor = db.query(Constants.TABLE_NAME,new String[]{String.valueOf(Constants.KEY_ID),Constants.GROCERY_ITEM, String.valueOf(Constants.GROCERY_QUANTITY),
                                 Constants.DATE},null,null,null,null,Constants.DATE+" DESC");

        if(cursor.moveToFirst()){
            do{
                Grocery grocery = new Grocery();
                grocery.setId(cursor.getInt(cursor.getColumnIndex(Constants.KEY_ID)));
                grocery.setName(cursor.getString(cursor.getColumnIndex(Constants.GROCERY_ITEM)));
                grocery.setQuantity(cursor.getInt(cursor.getColumnIndex(Constants.GROCERY_QUANTITY)));

                //convert timestamp to something readable
                DateFormat dateFormat = DateFormat.getDateInstance();
                String formatedDate = dateFormat.format(new Date( cursor.getLong(cursor.getColumnIndex(Constants.DATE)) ).getTime());
                grocery.setDateItemAdded(formatedDate);

                //Add to the groceryList
                groceryList.add(grocery);

            }while (cursor.moveToNext());
        }
        return groceryList;
    }

    //Update Grocery
    public boolean updateGrocery(Grocery grocery){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.GROCERY_ITEM,grocery.getName());
        contentValues.put(Constants.GROCERY_QUANTITY,grocery.getQuantity());
        contentValues.put(Constants.DATE, System.currentTimeMillis());

        int i = db.update(Constants.TABLE_NAME,contentValues,Constants.KEY_ID+" = ?",new String[]{String.valueOf(grocery.getId())});
        if(i>0)
            return true;
        return false;
    }

    //Delete Grocery
    public boolean deleteGrocery(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        int i = db.delete(Constants.TABLE_NAME,Constants.KEY_ID+" = ?",new String[]{String.valueOf(id)});
        db.close();

        if(i>0)
            return true;
        return false;
    }

    //Get Count of Groceries
    public int getGroceriesCount(){
        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "SELECT * FROM "+Constants.TABLE_NAME;
        Cursor cursor = db.rawQuery(countQuery,null);
        return cursor.getCount();
    }
}
