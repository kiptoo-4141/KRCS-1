package com.kenyaredcross.domain_model;

public class FeedbacksModel {
    private String feedbackId;
    private String senderEmail;
    private String receiverEmail;
    private String message;
    private long timestamp;

    public FeedbacksModel() { } // Required for Firebase

    public FeedbacksModel(String feedbackId, String senderEmail, String receiverEmail, String message, long timestamp) {
        this.feedbackId = feedbackId;
        this.senderEmail = senderEmail;
        this.receiverEmail = receiverEmail;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getFeedbackId() { return feedbackId; }
    public String getSenderEmail() { return senderEmail; }
    public String getReceiverEmail() { return receiverEmail; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }
}
