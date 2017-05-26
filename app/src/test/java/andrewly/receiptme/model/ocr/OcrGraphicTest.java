package andrewly.receiptme.model.ocr;

import android.content.Context;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Andrew Ly on 5/25/2017.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class OcrGraphicTest {

    @Mock
    Context mMockContext;

    @Test
    public void ocrGraphicTest() throws Exception {
        OcrGraphic testGraphic = new OcrGraphic(null, null);
        int expected, result;

        expected = 10;

        testGraphic.setId(expected);
        result = testGraphic.getId();

        assertEquals(expected, result);


    }
}
