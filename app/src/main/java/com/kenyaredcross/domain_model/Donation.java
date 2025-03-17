package com.kenyaredcross.domain_model;

public class Donation {
    private String donationId;
    private String amount;
    private String donationTime;
    private String email;
    private String fullName;
    private String paymentMethod;
    private String phoneNumber;
    private String status;

    // Constructor
    public Donation(String donationId, String amount, String donationTime, String email, String fullName, String paymentMethod, String phoneNumber, String status) {
        this.donationId = donationId;
        this.amount = amount;
        this.donationTime = donationTime;
        this.email = email;
        this.fullName = fullName;
        this.paymentMethod = paymentMethod;
        this.phoneNumber = phoneNumber;
        this.status = status;
    }

    // Getters and Setters
    public String getDonationId() {
        return donationId;
    }

    public void setDonationId(String donationId) {
        this.donationId = donationId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDonationTime() {
        return donationTime;
    }

    public void setDonationTime(String donationTime) {
        this.donationTime = donationTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}