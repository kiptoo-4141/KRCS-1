package com.kenyaredcross.domain_model;

import java.util.List;

public class VolunteerTasks {
    private String taskId;
    private String description;
    private String startDate;
    private String endDate;
    private String groupId;
    private String status;
    private List<GroupMember> groupMembers; // List of group members

    // Default constructor required for Firebase
    public VolunteerTasks() {}

    public VolunteerTasks(String taskId, String description, String startDate, String endDate, String groupId, String status, List<GroupMember> groupMembers) {
        this.taskId = taskId;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.groupId = groupId;
        this.status = status;
        this.groupMembers = groupMembers;
    }

    // Getters and Setters
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<GroupMember> getGroupMembers() { return groupMembers; }
    public void setGroupMembers(List<GroupMember> groupMembers) { this.groupMembers = groupMembers; }
}