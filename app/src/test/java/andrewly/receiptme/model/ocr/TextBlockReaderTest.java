package andrewly.receiptme.model.ocr;

import android.content.Context;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by Andrew Ly on 5/26/2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class TextBlockReaderTest {

    @Mock
    Context mMockContext;


    @Test
    public void checkIfTextIsPriceTest() throws Exception {
        TextBlockReader.itemsList = new ArrayList<>();
        TextBlockReader.parsedPriceList = new ArrayList<>();
        String input = "10.00";


        assertTrue(TextBlockReader.checkIfTextIsPrice(input));
    }

    @Test
    public void checkIfTextIsNotPriceTest() throws Exception {
        TextBlockReader.itemsList = new ArrayList<>();
        TextBlockReader.parsedPriceList = new ArrayList<>();
        String input = "abc1000";


        assertFalse(TextBlockReader.checkIfTextIsPrice(input));
    }

    @Test
    public void checkIfTextIsItemTest() throws Exception {
        TextBlockReader.itemsList = new ArrayList<>();
        String input = "orange";


        TextBlockReader.checkIfTextIsItem(input);

        assertEquals(1, TextBlockReader.itemsList.size());
    }

}
