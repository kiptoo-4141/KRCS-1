package com.kenyaredcross.domain_model;

public class OrgInventoryModel {
    private String itemName;
    private String category;
    private int count;

    // Default constructor required for calls to DataSnapshot.getValue(OrgInventoryModel.class)
    public OrgInventoryModel() {
    }

    // Constructor to initialize the model
    public OrgInventoryModel(String itemName, String category, int count) {
        this.itemName = itemName;
        this.category = category;
        this.count = count;
    }

    // Getters and Setters
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
