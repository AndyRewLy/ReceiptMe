package andrewly.receiptme.model;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Andrew Ly on 5/31/2017.
 */

public class Today {

    public static Today instance;

    private Date todaysDate;
    private Date[] pastSevenDays;

    private Today() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0); //anything 0 - 23
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);



        todaysDate = today.getTime();
    }

    public static Today getInstance() {
        if (instance == null) {
            instance = new Today();
        }

        return instance;
    }

    public Date getToday() {
        return todaysDate;
    }

    public void getPastSevenDays(Date[] dates) {

        pastSevenDays = dates;

        dates[6] = todaysDate;

        for (int i = 5; i >= 0; i--) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dates[i + 1]);
            cal.add(Calendar.DAY_OF_MONTH, -1);

            Log.d("Last7Days", cal.getTime() + " is the time");
            dates[i] = cal.getTime();
        }
    }

    public int getIndexFromDate(Date date) {

        for (int i = 0; i < pastSevenDays.length; i++) {
            if (date.before(pastSevenDays[i])) {
                return i;
            }
        }

        return -1;
    }
}
