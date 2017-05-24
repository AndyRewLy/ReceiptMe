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

    @Test
    public void setCategoryTest() throws Exception {
        PurchasedItem p = new PurchasedItem();
        String expected, result;

        expected = "frozen";

        p.setCategory(expected);
        result = p.getCategory();

        assertEquals(expected, result);
    }

    @Test
    public void setItemNameTest() throws Exception {
        PurchasedItem p = new PurchasedItem();
        String expected, result;

        expected = "oranges";

        p.setCategory(expected);
        result = p.getCategory();

        assertEquals(expected, result);
    }
}
