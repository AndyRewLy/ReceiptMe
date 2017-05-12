package andrewly.receiptme.model;

import andrewly.receiptme.model.categories.Category;

/**
 * Created by Andrew Ly on 5/9/2017.
 */

public class PurchasedItem {

    private String itemName;
    private double cost;
    private String category;

    public PurchasedItem() {

    }

    public PurchasedItem(String itemName, double cost, String category) {
        this.itemName = itemName;
        this.cost = cost;
        this.category = category;
    }

    public void setCategory(String category) {
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

    public String getCategory() {
        return this.category;
    }

    public String getItemName() {
        return this.itemName;
    }

}
