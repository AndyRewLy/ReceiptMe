package andrewly.receiptme.model.dao;

import android.database.Cursor;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import andrewly.receiptme.model.Today;

/**
 * Created by Andrew Ly on 5/31/2017.
 */

public class CostDao {

    public static Double todaysExpenditures;

    public static void getExpenditures(Number[] costs) {
        int idx;
        String queryString =
                "SELECT SUM(itemPrice), dateInput FROM Items " +
                        "WHERE dateInput >= (SELECT DATETIME('now', '-7 day'))" +
                        "GROUP BY dateInput " +
                        "ORDER BY dateInput DESC ";

        idx = 6;

        Cursor cursor = SQLDatabaseConnector.getInstance().getReadableDatabase().rawQuery(queryString, new String[0]);

        Log.d("AllCosts", cursor.toString());

        for (int i = 0; i < costs.length; i++) {
            costs[i] = 0;
        }

        Log.d("ParsedDate", cursor.getCount() + " number of items");
        SimpleDateFormat format = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
        while(cursor.moveToNext()) {
            double sum = cursor.getDouble(0);
            String date = cursor.getString(1);

            try {
                Date parsedDate = format.parse(date);

                Log.d("ParsedDate", "oarsed date is " + parsedDate + " " + Today.getInstance().getIndexFromDate(parsedDate) + " cost of " + sum);

                costs[Today.getInstance().getIndexFromDate(parsedDate)] = sum;
            }
            catch (ParseException e) {
                e.printStackTrace();
            }


            Log.d("AllCosts", sum + " " + date);
        }

        if (costs[6] instanceof Integer) {
            todaysExpenditures = ((int)costs[6]) + 0.0;
        }
        else if (costs[6] instanceof Double) {
            todaysExpenditures = ((double)costs[6]) + 0.0;
        }

        cursor.close();
    }
}
