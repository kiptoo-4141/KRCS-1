package com.kenyaredcross.domain_model;

public class Item {
    private String itemName;
    private String category;
    private int count;

    public Item() {
        // Default constructor required for Firebase
    }

    public Item(String itemName, String category, int count) {
        this.itemName = itemName;
        this.category = category;
        this.count = count;
    }

    public String getItemName() {
        return itemName;
    }

    public String getCategory() {
        return category;
    }

    public int getCount() {
        return count;
    }
}
