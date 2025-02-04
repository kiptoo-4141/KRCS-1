package com.kenyaredcross.domain_model;

public class ItemRequest {
    private String itemName;
    private int quantity;
    private String trainerEmail;
    private String status; // Pending, Approved, Rejected

    public ItemRequest() {
        // Default constructor required for Firebase
    }

    public ItemRequest(String itemName, int quantity, String trainerEmail) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.trainerEmail = trainerEmail;
        this.status = "Pending"; // Default status
    }

    public String getItemName() {
        return itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getTrainerEmail() {
        return trainerEmail;
    }

    public String getStatus() {
        return status;
    }
}
