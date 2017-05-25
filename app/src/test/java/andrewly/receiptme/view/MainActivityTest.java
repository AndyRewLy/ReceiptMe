package andrewly.receiptme.view;

import org.junit.Rule;
import org.junit.Test;

import andrewly.receiptme.model.PurchasedItem;
import android.support.test.rule.ActivityTestRule;
import static junit.framework.Assert.assertEquals;

/**
 * Created by Andrew Ly on 5/24/2017.
 */

public class MainActivityTest {

    @Rule
    public ActivityTestRule mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Test
    public void setCostTest() throws Exception {


        PurchasedItem p = new PurchasedItem("testItem", 2.00, null);
        double expected, result;

        p.setCost(0);

        expected = 0;
        result = p.getCost();

        assertEquals(expected, result, 0.000001);
    }
}
