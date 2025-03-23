package com.kenyaredcross.domain_model;

public class CoursePayment {
    private String courseId, userEmail, paymentMethod, paymentDetails, date, time, status, transactionCode, username;
    private int amount;

    public CoursePayment() {
        // Required for Firebase
    }

    public CoursePayment(String courseId, String userEmail, String paymentMethod, String paymentDetails, String date, String time, String status, int amount, String transactionCode, String username) {
        this.courseId = courseId;
        this.userEmail = userEmail;
        this.paymentMethod = paymentMethod;
        this.paymentDetails = paymentDetails;
        this.date = date;
        this.time = time;
        this.status = status;
        this.amount = amount;
        this.transactionCode = transactionCode;
        this.username = username;
    }

    // Getters and setters
    public String getCourseId() {
        return courseId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getPaymentDetails() {
        return paymentDetails;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }

    public int getAmount() {
        return amount;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}