package andrewly.receiptme.model.class_storage;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Andrew Ly on 5/11/2017.
 */

@IgnoreExtraProperties
public class Item {

    public String itemName;
    public double cost;
    public String category;

    public Item() {

    }

    public Item(String itemName, double cost, String category) {
        this.itemName = itemName;
        this.cost = cost;
        this.category = category;
    }
}
