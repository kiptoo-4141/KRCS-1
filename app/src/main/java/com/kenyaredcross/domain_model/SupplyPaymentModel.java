package com.kenyaredcross.domain_model;

public class SupplyPaymentModel {
    private String id, category, dateTime, inventoryManager, itemName, status, supplier;
    private Integer requestCount, amount;

    // Default constructor required for calls to DataSnapshot.getValue(SupplyPaymentModel.class)
    public SupplyPaymentModel() {}

    // Constructor with all fields
    public SupplyPaymentModel(String id, String category, Integer requestCount, Integer amount, String status, String itemName, String inventoryManager, String dateTime, String supplier) {
        this.id = id;
        this.category = category;
        this.requestCount = requestCount;
        this.amount = amount;
        this.status = status;
        this.itemName = itemName;
        this.inventoryManager = inventoryManager;
        this.dateTime = dateTime;
        this.supplier = supplier;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
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