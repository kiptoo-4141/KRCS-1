package com.kenyaredcross.domain_model;

public class Feedback {
    private String feedback_id, activity, dateSent, email, feedback, recipientEmail, role, status, timeSent,message;

    public Feedback() {}

    public Feedback(String feedback_id, String activity, String dateSent, String email, String feedback,
                    String recipientEmail, String role, String status, String timeSent) {
        this.feedback_id = feedback_id;
        this.activity = activity;
        this.dateSent = dateSent;
        this.email = email;
        this.feedback = feedback;
        this.recipientEmail = recipientEmail;
        this.role = role;
        this.status = status;
        this.timeSent = timeSent;
    }

    public Feedback(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Getters and Setters
    public String getFeedback_id() { return feedback_id; }
    public void setFeedback_id(String feedback_id) { this.feedback_id = feedback_id; }

    public String getActivity() { return activity; }
    public void setActivity(String activity) { this.activity = activity; }

    public String getDateSent() { return dateSent; }
    public void setDateSent(String dateSent) { this.dateSent = dateSent; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTimeSent() { return timeSent; }
    public void setTimeSent(String timeSent) { this.timeSent = timeSent; }
}
