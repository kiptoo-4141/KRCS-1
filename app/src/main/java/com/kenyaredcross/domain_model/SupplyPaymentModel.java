package com.kenyaredcross.domain_model;

public class SupplyPaymentModel {
    private String id;
    private double amount;
    private String category;
    private String dateTime;
    private String inventoryManager;
    private String itemName;
    private int requestCount;
    private String status;

    // Constructor
    public SupplyPaymentModel() {
    }

    public SupplyPaymentModel(String id, double amount, String category, String dateTime, String inventoryManager, String itemName, int requestCount, String status) {
        this.id = id;
        this.amount = amount;
        this.category = category;
        this.dateTime = dateTime;
        this.inventoryManager = inventoryManager;
        this.itemName = itemName;
        this.requestCount = requestCount;
        this.status = status;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(int requestCount) {
        this.requestCount = requestCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // toString method for easy display
    @Override
    public String toString() {
        return "SupplyPaymentModel{" +
                "id='" + id + '\'' +
                ", amount=" + amount +
                ", category='" + category + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", inventoryManager='" + inventoryManager + '\'' +
                ", itemName='" + itemName + '\'' +
                ", requestCount=" + requestCount +
                ", status='" + status + '\'' +
                '}';
    }
}
