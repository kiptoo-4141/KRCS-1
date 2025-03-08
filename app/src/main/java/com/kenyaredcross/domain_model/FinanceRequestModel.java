package com.kenyaredcross.domain_model;

public class FinanceRequestModel {
    private String category, dateTime, inventoryManager, itemName, status, requestId, supplier;
    private Integer requestCount, totalAmount;

    // Default constructor required for calls to DataSnapshot.getValue(FinanceRequestModel.class)
    public FinanceRequestModel() {}

    // Constructor with requestId
    public FinanceRequestModel(String requestId, String category, Integer requestCount, Integer totalAmount, String status, String itemName, String inventoryManager, String dateTime, String supplier) {
        this.requestId = requestId;
        this.category = category;
        this.requestCount = requestCount;
        this.totalAmount = totalAmount;
        this.status = status;
        this.itemName = itemName;
        this.inventoryManager = inventoryManager;
        this.dateTime = dateTime;
        this.supplier = supplier;
    }

    // Getters and setters
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(Integer requestCount) {
        this.requestCount = requestCount;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getInventoryManager() {
        return inventoryManager;
    }

    public void setInventoryManager(String inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }
}