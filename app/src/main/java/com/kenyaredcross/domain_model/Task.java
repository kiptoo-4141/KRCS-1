package com.kenyaredcross.domain_model;

public class Task {
    private String description;
    private String startDate;
    private String endDate;
    private String groupId;

    private String status;

    public Task(String status, String start, String end, String selectedGroupId, String s) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Task() {
        // Default constructor required for Firebase
    }

    public Task(String description, String startDate, String endDate, String groupId) {
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.groupId = groupId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
