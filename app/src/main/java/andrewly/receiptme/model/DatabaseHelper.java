package andrewly.receiptme.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrew Ly on 5/11/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ReceiptMe.db";
    public static final String TABLE_NAME = "item_table";
    public static final String COL_1 = "item";
    public static final String COL_2 = "cost";
    public static final String COL_3 = "category";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE table " + TABLE_NAME +
                   "(id integer primary key autoincrement, item String, " +
                   "cost Float, category String)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }


    public void insertPurchasedItems(List<PurchasedItem> items) {

        for (int i = 0; i < items.size(); i++) {
            PurchasedItem item = items.get(i);
            boolean result = insertItemData(item.getItemName(), item.getCost(), item.getCategory());

            if (result) {
                Log.d("Inserting into Table", "insert:" + item.getItemName() + " " + item.getCost() + " " + item.getCategory() + " success");
            }
            else {
                Log.d("Inserting into Table", "insert:" + item.getItemName() + " " + item.getCost() + " " + item.getCategory() + " failed");
            }

        }

    }

    private boolean insertItemData(String item, double cost, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_1, item);
        contentValues.put(COL_2, cost);
        contentValues.put(COL_3, category);

        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1) {
            return false;
        }

        return true;
    }
}
