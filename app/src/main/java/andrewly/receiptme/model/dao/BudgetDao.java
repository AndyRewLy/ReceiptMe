package andrewly.receiptme.model.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Andrew Ly on 6/4/2017.
 */

public class BudgetDao {

    public static double getBudget() {
        String queryString = "SELECT budget FROM Budget";

        double budget = 0;

        Cursor cursor = SQLDatabaseConnector.getInstance().getReadableDatabase().rawQuery(queryString, new String[0]);

        while(cursor.moveToNext()) {
            budget = cursor.getDouble(0);

            Log.d("AllCosts", "Budget is " + budget);

        }

        cursor.close();

        return budget;
    }

    public static void editBudget(double newBudget) {
        SQLiteDatabase db = SQLDatabaseConnector.getInstance().getWritableDatabase();
        ContentValues values = new ContentValues();

        db.execSQL("delete from Budget where 1=1");

        values.put("budget", newBudget);

        db.insertOrThrow("Budget", null, values);

        Log.d("Inserted buget", "budget inserted with" + newBudget);
        db.close();
    }
}
