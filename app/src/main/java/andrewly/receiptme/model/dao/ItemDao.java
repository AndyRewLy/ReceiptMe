package andrewly.receiptme.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import andrewly.receiptme.model.PurchasedItem;
import andrewly.receiptme.model.class_storage.Item;

/**
 * Created by Andrew Ly on 4/28/2017.
 */

public class ItemDao {

    private static final String NAME = "itemName";
    private static final String PRICE = "itemPrice";
    private static final String CATEGORY = "category";

    private static final String[] titles = {"sup sup sup", "ok ok ok", "hi hi hi"};
    //private static final int[] icons;

    //Change this class to get data from the parsedImage (hopefully)
    public static List<PurchasedItem> getListData() {
        List<PurchasedItem> data = new ArrayList<>();

        //Adding items into the arraylist

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < titles.length; j++) {
                PurchasedItem input = new PurchasedItem(titles[j], 0.0, "Other");

                data.add(input);
            }
        }

        return data;
    }

    //Takes in a list of String and Cost
    public static List<PurchasedItem> getCreatedListData(List<String> itemNames, List<Double> itemCosts) {
        List<PurchasedItem> data = new ArrayList<>();
        int maxSize;

        maxSize = itemNames.size() >= itemCosts.size() ? itemNames.size(): itemCosts.size();

        //Adding items into the arraylist

        for (int i = 0; i < maxSize; i++) {
            PurchasedItem input = new PurchasedItem();

            input.setCategory(null);
            if (i < itemNames.size() && itemNames.get(i) != null) {
                input.setItemName(itemNames.get(i));
            }

            if (i < itemCosts.size() && itemCosts.get(i) != null) {
                input.setCost(itemCosts.get(i));
            }

            data.add(input);
        }

        return data;
    }

    public static void insertItems(Context context, List<PurchasedItem> items) {

        for (int i = 0; i < items.size(); i++) {
            PurchasedItem current = items.get(i);
            insertItem(context, current.getItemName(), current.getCost(), current.getCategory());

        }
    }
    public static void insertItem(Context c, String itemName, Double itemPrice, String itemCategory) {
        SQLiteDatabase db = SQLDatabaseConnector.getInstance(c).getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(NAME, itemName);
        values.put(PRICE, itemPrice);
        values.put(CATEGORY, itemCategory);

        db.insertOrThrow("Items", null, values);

        db.close();
    }

}
