package com.kenyaredcross.domain_model;

public class OrganisationInventoryModel {
    private String itemName;
    private String category;
    private int requestCount;
    private String lastRestocked; // or any other appropriate name

    // Default constructor required for calls to DataSnapshot.getValue(OrganisationInventoryModel.class)
    public OrganisationInventoryModel() {
    }

    public OrganisationInventoryModel(String itemName, String category, int requestCount, String lastRestocked) {
        this.itemName = itemName;
        this.category = category;
        this.requestCount = requestCount;
        this.lastRestocked = lastRestocked;
    }

    public String getItemName() {
        return itemName;
    }

    public String getCategory() {
        return category;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public String getLastRestocked() {
        return lastRestocked;
    }
}
