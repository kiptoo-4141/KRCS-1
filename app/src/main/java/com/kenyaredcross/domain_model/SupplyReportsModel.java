package com.kenyaredcross.domain_model;

public class SupplyReportsModel {
    private String id;
    private String itemName;
    private String category;
    private int requestCount;
    private double amount;
    private String dateTime;
    private String inventoryManager;
    private String status;

    // Default constructor required for calls to DataSnapshot.getValue(SupplyReportsModel.class)
    public SupplyReportsModel() {
    }

    // Constructor to initialize the model
    public SupplyReportsModel(String id, String itemName, String category, int requestCount, double amount, String dateTime, String inventoryManager, String status) {
        this.id = id;
        this.itemName = itemName;
        this.category = category;
        this.requestCount = requestCount;
        this.amount = amount;
        this.dateTime = dateTime;
        this.inventoryManager = inventoryManager;
        this.status = status;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(int requestCount) {
        this.requestCount = requestCount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getInventoryManager() {
        return inventoryManager;
    }

    public void setInventoryManager(String inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
