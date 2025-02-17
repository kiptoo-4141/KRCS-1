package com.kenyaredcross.domain_model;

public class ReplyModel {
    private String replyId;
    private String senderId;
    private String message;
    private long timestamp;

    public ReplyModel() {}

    public ReplyModel(String replyId, String senderId, String message, long timestamp) {
        this.replyId = replyId;
        this.senderId = senderId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getReplyId() { return replyId; }
    public String getSenderId() { return senderId; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }
}
