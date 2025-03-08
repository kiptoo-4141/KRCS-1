package com.kenyaredcross.domain_model;

public class SuppliedGoodsModel {

    public SuppliedGoodsModel() {
        // Default constructor required for Firebase
    }

    private String category, dateTime, inventoryManager, itemName, requestId, status, supplier;
    Integer totalAmount;

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public SuppliedGoodsModel(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public SuppliedGoodsModel(String supplier) {
        this.supplier = supplier;
    }

    private Integer amount, requestCount;

    public SuppliedGoodsModel(String category, Integer requestCount, Integer amount, String status, String requestId, String itemName, String inventoryManager, String dateTime) {
        this.category = category;
        this.requestCount = requestCount;
        this.amount = amount;
        this.status = status;
        this.requestId = requestId;
        this.itemName = itemName;
        this.inventoryManager = inventoryManager;
        this.dateTime = dateTime;
    }

    // Getters and Setters
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

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
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
}