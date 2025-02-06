package com.kenyaredcross.domain_model;



public class Feedback {
    private String feedback_id, activity, dateSent, email, feedback, recipientEmail, role, status, timeSent, username;

    public Feedback() {}

    public Feedback(String feedback_id, String activity, String dateSent, String email, String feedback,
                    String recipientEmail, String role, String status, String timeSent, String username) {
        this.feedback_id = feedback_id;
        this.activity = activity;
        this.dateSent = dateSent;
        this.email = email;
        this.feedback = feedback;
        this.recipientEmail = recipientEmail;
        this.role = role;
        this.status = status;
        this.timeSent = timeSent;
        this.username = username;
    }

    public String getFeedback_id() { return feedback_id; }
    public String getActivity() { return activity; }
    public String getDateSent() { return dateSent; }
    public String getEmail() { return email; }
    public String getFeedback() { return feedback; }
    public String getRecipientEmail() { return recipientEmail; }
    public String getRole() { return role; }
    public String getStatus() { return status; }
    public String getTimeSent() { return timeSent; }
    public String getUsername() { return username; }
}
