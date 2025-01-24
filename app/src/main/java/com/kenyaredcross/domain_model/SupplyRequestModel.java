package com.kenyaredcross.domain_model;

public class SupplyRequestModel {
    private String requestId;
    private String itemName;
    private String category;
    private String inventoryManager;
    private int requestCount;
    private String status;
    private String dateTime; // Added for timestamp if needed
    private double totalAmount; // Assuming you want to use this field

    // Default constructor required for calls to DataSnapshot.getValue(SupplyRequestModel.class)
    public SupplyRequestModel() {}

    // Add getters
    public String getRequestId() {
        return requestId;
    }

    public String getItemName() {
        return itemName;
    }

    public String getCategory() {
        return category;
    }

    public String getInventoryManager() {
        return inventoryManager;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public String getStatus() {
        return status;
    }

    public String getDateTime() {
        return dateTime;
    }

    public double getTotalAmount() {
        return totalAmount;
    }
}
