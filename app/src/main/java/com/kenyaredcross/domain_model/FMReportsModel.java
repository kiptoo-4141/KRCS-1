package com.kenyaredcross.domain_model;

public class FMReportsModel {
    String amount, category, dateTime, inventoryManager, itemName,  requestId, status,timestamp;
    String donationId,donationTime,fullName,paymentMethod, phoneNumber;
    Integer requestCount;
    FMReportsModel(){

    }

    public FMReportsModel(String amount, String category, String dateTime, String inventoryManager, String itemName, String requestId, String status, String timestamp, String donationId, String donationTime, String fullName, String paymentMethod, String phoneNumber, Integer requestCount) {
        this.amount = amount;
        this.category = category;
        this.dateTime = dateTime;
        this.inventoryManager = inventoryManager;
        this.itemName = itemName;
        this.requestId = requestId;
        this.status = status;
        this.timestamp = timestamp;
        this.donationId = donationId;
        this.donationTime = donationTime;
        this.fullName = fullName;
        this.paymentMethod = paymentMethod;
        this.phoneNumber = phoneNumber;
        this.requestCount = requestCount;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
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

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDonationId() {
        return donationId;
    }

    public void setDonationId(String donationId) {
        this.donationId = donationId;
    }

    public String getDonationTime() {
        return donationTime;
    }

    public void setDonationTime(String donationTime) {
        this.donationTime = donationTime;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(Integer requestCount) {
        this.requestCount = requestCount;
    }
}
