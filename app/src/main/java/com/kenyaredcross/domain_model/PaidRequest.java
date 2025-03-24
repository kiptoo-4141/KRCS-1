package com.kenyaredcross.domain_model;

public class PaidRequest {
    private String amount;
    private String category;
    private String dateTime;
    private String inventoryManager;
    private String itemName;
    private String requestCount;
    private String status;
    private String supplier;

    public PaidRequest() {
        // Required empty constructor for Firebase
    }

    // Getters and Setters with type conversion handling
    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    // Handle potential Long values from Firebase
    public void setAmount(Long amount) {
        if (amount != null) {
            this.amount = String.valueOf(amount);
        }
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

    public String getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(String requestCount) {
        this.requestCount = requestCount;
    }

    // Handle potential Long values from Firebase
    public void setRequestCount(Long requestCount) {
        if (requestCount != null) {
            this.requestCount = String.valueOf(requestCount);
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }
}