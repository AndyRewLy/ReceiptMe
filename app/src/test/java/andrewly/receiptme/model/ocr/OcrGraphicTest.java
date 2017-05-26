package andrewly.receiptme.model.ocr;

import org.junit.Test;

import andrewly.receiptme.controller.camera.GraphicOverlay;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

/**
 * Created by Andrew Ly on 5/25/2017.
 */
public class OcrGraphicTest {

    @Test
    public void setIdTest() throws Exception {
        OcrGraphic graphicTest = new OcrGraphic(new GraphicOverlay(null, null), null);

        int expected, result;

        expected = 1;

        graphicTest.setId(expected);
        result = graphicTest.getId();

        assertEquals(expected, result);
    }

    @Test
    public void getTextBlockTest() throws Exception {
        OcrGraphic graphicTest = new OcrGraphic(new GraphicOverlay(null, null), null);

        assertEquals(null, graphicTest.getTextBlock());
    }

    @Test
    public void blockContainsTest() throws Exception {
        OcrGraphic graphicTest = new OcrGraphic(new GraphicOverlay(null, null), null);

        assertFalse(graphicTest.contains(0, 0));
    }
}
