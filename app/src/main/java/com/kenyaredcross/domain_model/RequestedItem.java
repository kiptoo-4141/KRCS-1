package com.kenyaredcross.domain_model;


public class RequestedItem {
    private String requestId;
    private String itemName;
    private int quantity;
    private String status;
    private String trainerEmail;

    public RequestedItem() {
        // Default constructor required for Firebase
    }

    public RequestedItem(String itemName, int quantity, String status, String trainerEmail) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.status = status != null ? status : "Pending";
        this.trainerEmail = trainerEmail;
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

    public int getQuantity() {
        return quantity;
    }

    public String getStatus() {
        return status;
    }

    public String getTrainerEmail() {
        return trainerEmail;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
