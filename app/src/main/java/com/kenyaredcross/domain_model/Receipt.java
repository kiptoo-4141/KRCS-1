package com.kenyaredcross.domain_model;

public class Receipt {
    private String courseId;
    private String date;
    private String paymentDetails;
    private String paymentMethod;
    private String status;
    private String time;
    private String userEmail;
    private int amount;

    public Receipt() {
        // Default constructor for Firebase
    }

    public Receipt(String courseId, String date, String paymentDetails, String paymentMethod, String status, String time, String userEmail, int amount) {
        this.courseId = courseId;
        this.date = date;
        this.paymentDetails = paymentDetails;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.time = time;
        this.userEmail = userEmail;
        this.amount = amount;
    }

    public String getCourseId() { return courseId; }
    public String getDate() { return date; }
    public String getPaymentDetails() { return paymentDetails; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getStatus() { return status; }
    public String getTime() { return time; }
    public String getUserEmail() { return userEmail; }
    public int getAmount() { return amount; }
}
