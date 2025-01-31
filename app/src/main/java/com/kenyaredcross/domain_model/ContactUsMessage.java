package com.kenyaredcross.domain_model;

public class ContactUsMessage {
    private String message;
    private String status;
    private String userEmail;
    private String userId;

    // No-argument constructor
    public ContactUsMessage() {
    }

    // Constructor with parameters
    public ContactUsMessage(String message, String status, String userEmail, String userId) {
        this.message = message;
        this.status = status;
        this.userEmail = userEmail;
        this.userId = userId;
    }

    // Getters and setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
