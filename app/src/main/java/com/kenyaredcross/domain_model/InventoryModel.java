package com.kenyaredcross.domain_model;

public class InventoryModel {
    private String itemName;
    private String category;
    private int count;

    // Default constructor required for calls to DataSnapshot.getValue(InventoryModel.class)
    public InventoryModel() {
    }

    public InventoryModel(String itemName, String category, int count) {
        this.itemName = itemName;
        this.category = category;
        this.count = count;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
