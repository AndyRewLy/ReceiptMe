package andrewly.receiptme.model;

import org.junit.Test;

import java.util.Date;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Andrew Ly on 5/31/2017.
 */

public class TodayTest {

    @Test
    public void todayTest() throws Exception {
        Today test = Today.getInstance();

        Date today = test.getToday();

        assertEquals("", today.toString());
    }
}
