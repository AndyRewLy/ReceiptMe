package andrewly.receiptme.model;

import andrewly.receiptme.model.categories.Category;

/**
 * Created by Andrew Ly on 5/9/2017.
 */

public class PurchasedItem {

    private String itemName;
    private double cost;
    private Category category;

    public PurchasedItem(String itemName, double cost, Category category) {
        this.itemName = itemName;
        this.cost = cost;
        this.category = category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getCost() {
        return this.cost;
    }

}
