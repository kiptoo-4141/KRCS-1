package com.kenyaredcross.domain_model;

import java.util.HashMap;
import java.util.Map;

public class FeedbacksModel {
    private String feedbackId;
    private String senderId;
    private String receiverId;
    private String message;
    private long timestamp;
    private Map<String, ReplyModel> replies;

    public FeedbacksModel() {}

    public FeedbacksModel(String feedbackId, String senderId, String receiverId, String message, long timestamp) {
        this.feedbackId = feedbackId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.timestamp = timestamp;
        this.replies = new HashMap<>();
    }

    public String getFeedbackId() { return feedbackId; }
    public String getSenderId() { return senderId; }
    public String getReceiverId() { return receiverId; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }
    public Map<String, ReplyModel> getReplies() { return replies; }

    public void setReplies(Map<String, ReplyModel> replies) { this.replies = replies; }
}
