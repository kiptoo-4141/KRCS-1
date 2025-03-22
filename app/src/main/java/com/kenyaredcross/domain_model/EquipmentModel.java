package com.kenyaredcross.domain_model;

public class EquipmentModel {
    private String itemName;
    private String category;
    private int count;

    public EquipmentModel() {
        // Default constructor required for Firebase
    }

    public EquipmentModel(String itemName, String category, int count) {
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