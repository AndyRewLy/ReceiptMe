package andrewly.receiptme.model.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

import andrewly.receiptme.model.PurchasedItem;

/**
 * Created by Andrew Ly on 5/22/2017.
 */

public class SQLDatabaseConnector extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "receiptme.db";
    private static final int DATABASE_VERSION = 1;

    private static SQLDatabaseConnector instance;
    private Context context;

    private static String createItems = "CREATE TABLE IF NOT EXISTS Items(" +
            "   itemName VARCHAR(64)," +
            "   itemPrice DOUBLE," +
            "   category VARCHAR(64)," +
            "   dateInput DATE" +
            ");";

    private static String createBudget = "CREATE TABLE IF NOT EXISTS Budget (" +
            "   weeklyBudget INT" +
            ");";

    private SQLDatabaseConnector (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public static SQLDatabaseConnector getInstance(Context context) {
        if (instance == null) {
            SQLDatabaseConnector instance = new SQLDatabaseConnector(context);
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createItems);
        db.execSQL(createBudget);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Items");
        db.execSQL("DROP TABLE IF EXISTS Budget");
        onCreate(db);
    }

    public void insertPurchasedItems(List<PurchasedItem> items) {
        ItemDao.insertItems(context, items);
    }
}
