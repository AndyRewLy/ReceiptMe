package andrewly.receiptme.model;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Andrew Ly on 5/10/2017.
 */

public class PurchasedItemTest {

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
