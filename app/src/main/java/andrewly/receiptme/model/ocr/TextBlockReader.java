package andrewly.receiptme.model.ocr;

import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrew Ly on 5/22/2017.
 */

public class TextBlockReader {


    public static ArrayList<Double>  parsedPriceList;
    public static ArrayList<String> itemsList;

    public static void iterateAllTextBlocks(ArrayList<TextBlock> textBlocks) {
        parsedPriceList = new ArrayList<>();
        itemsList = new ArrayList<>();

        if (textBlocks != null) {
            for (int i = 0; i < textBlocks.size(); i++) {
                iterateThroughTextBlock(textBlocks.get(i));
            }
        }

    }
    public static void iterateAllTextBlocks(SparseArray<TextBlock> retBlock) {
        parsedPriceList = new ArrayList<>();
        itemsList = new ArrayList<>();

        if (retBlock != null) {
            for (int i = 0; i < retBlock.size(); i++) {
                Log.i("Recognize Text", "Iterating through current block" + i);
                iterateThroughTextBlock(retBlock.valueAt(i));
            }
        }
    }

    private static void iterateThroughTextBlock(TextBlock textComponents) {
        if (textComponents != null) {
            List<? extends Text> textValues = textComponents.getComponents();
            iterateThroughTextValues(textValues);
        }
    }

    private static void iterateThroughTextValues(List<? extends Text> textValues) {
        if (textValues != null) {
            for (int i = 0; i < textValues.size(); i++) {
                Log.i("Recognize Text", "Text Found: " + textValues.get(i).getValue());
                if (checkIfTextIsPrice(textValues.get(i).getValue())) {

                } else {
                    checkIfTextIsItem(textValues.get(i).getValue());
                }
            }
        }
    }

    public static boolean checkIfTextIsPrice(String value) {
        double tempValue;

        if (value.contains(".")) {
            value = value.replaceAll("[^0-9.]", "");
            Log.i("Recognize Text", "obtained value of " + value);

            if (!value.equals(".")) {
                String[] removeSpaces = value.split(" ");

                for (int i = 0; i < removeSpaces.length; i++) {
                    tempValue = Double.parseDouble(removeSpaces[i]);
                    parsedPriceList.add((Double) tempValue);
                }
                return true;
            }
        }

        return false;
    }

    public static void checkIfTextIsItem(String value) {
        value = value.replaceAll("[^A-Za-z,/]", "");
        Log.i("Recognize Text", "Item name of " + value);
        itemsList.add(value);
    }
}
