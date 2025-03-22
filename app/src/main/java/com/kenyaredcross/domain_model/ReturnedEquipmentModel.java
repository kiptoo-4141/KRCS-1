package com.kenyaredcross.domain_model;

import java.util.Date;

public class ReturnedEquipmentModel {
    private String username;
    private String email;
    private String itemName;
    private String category;
    private int count;
    private Date borrowedDate;
    private Date returnedDate;
    private String status;

    public ReturnedEquipmentModel() {
        // Default constructor required for Firebase
    }

    public ReturnedEquipmentModel(String username, String email, String itemName, String category, int count, Date borrowedDate, Date returnedDate, String status) {
        this.username = username;
        this.email = email;
        this.itemName = itemName;
        this.category = category;
        this.count = count;
        this.borrowedDate = borrowedDate;
        this.returnedDate = returnedDate;
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

    public Date getBorrowedDate() {
        return borrowedDate;
    }

    public void setBorrowedDate(Date borrowedDate) {
        this.borrowedDate = borrowedDate;
    }

    public Date getReturnedDate() {
        return returnedDate;
    }

    public void setReturnedDate(Date returnedDate) {
        this.returnedDate = returnedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}