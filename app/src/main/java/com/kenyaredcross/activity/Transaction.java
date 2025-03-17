package com.kenyaredcross.activity;

public class Transaction {
    private String id;
    private String amount;
    private String date;
    private String time;
    private String paymentMethod;

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    private String fullname;

    public Transaction(String fullName) {
        this.fullName = fullName;
    }

    private String status;
    private String userEmail;
    private String paymentDetails;
    private String courseId;
    private String fullName;
    private String phoneNumber;
    private String category;
    private String itemName;
    private String requestCount;
    private String supplier;
    private String inventoryManager;

    // Constructors (same as before)
    public Transaction(String id, String amount, String date, String time, String paymentMethod, String status, String userEmail, String paymentDetails, String courseId) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.time = time;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.userEmail = userEmail;
        this.paymentDetails = paymentDetails;
        this.courseId = courseId;
    }

    public Transaction(String id, String amount, String paymentMethod, String email, String fullName, String phoneNumber) {
        this.id = id;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.userEmail = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }

//    public Transaction(String id, String amount, String category, String dateTime, String inventoryManager, String itemName, String requestCount, String status, String supplier) {
//        this.id = id;
//        this.amount = amount;
//        this.category = category;
//        this.date = dateTime;
//        this.inventoryManager = inventoryManager;
//        this.itemName = itemName;
//        this.requestCount = requestCount;
//        this.status = status;
//        this.supplier = supplier;
//    }



    // Getters (same as before)
    public String getId() { return id; }
    public String getAmount() { return amount; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getStatus() { return status; }
    public String getUserEmail() { return userEmail; }
    public String getPaymentDetails() { return paymentDetails; }
    public String getCourseId() { return courseId; }
    public String getFullName() { return fullName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getCategory() { return category; }
    public String getItemName() { return itemName; }
    public String getRequestCount() { return requestCount; }
    public String getSupplier() { return supplier; }
    public String getInventoryManager() { return inventoryManager; }
}