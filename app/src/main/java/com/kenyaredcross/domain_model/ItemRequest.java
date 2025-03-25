package com.kenyaredcross.domain_model;

public class ItemRequest {
    private String itemName;
    private int quantity;
    private String status;
    private String trainerEmail;

    public ItemRequest() {
        // Required empty constructor for Firebase
    }

    public ItemRequest(String itemName, int quantity, String status, String trainerEmail) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.status = status;
        this.trainerEmail = trainerEmail;
    }

    // Getters and setters
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTrainerEmail() {
        return trainerEmail;
    }

    public void setTrainerEmail(String trainerEmail) {
        this.trainerEmail = trainerEmail;
    }
}