package com.kenyaredcross.domain_model;

import java.util.Date;

public class BorrowedEquipmentModel {
    private String username;
    private String email;
    private String itemName;
    private String category;
    private int count;
    private Date date;
    private String status;

    public BorrowedEquipmentModel() {
        // Default constructor required for Firebase
    }

    public BorrowedEquipmentModel(String username, String email, String itemName, String category, int count, Date date, String status) {
        this.username = username;
        this.email = email;
        this.itemName = itemName;
        this.category = category;
        this.count = count;
        this.date = date;
        this.status = status;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}