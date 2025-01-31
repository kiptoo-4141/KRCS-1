package com.kenyaredcross.domain_model;

public class ContactUsMessage {

    private String message;
    private String status;
    private String userEmail;
    private String userId;
    private String reply;  // Add this field to store the service manager's reply

    public ContactUsMessage() {
        // Default constructor required for Firebase
    }

    // Getter and setter for message
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Getter and setter for status
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Getter and setter for userEmail
    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    // Getter and setter for userId
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Getter and setter for reply (newly added)
    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}
