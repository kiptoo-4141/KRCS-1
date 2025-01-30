package com.kenyaredcross.domain_model;

public class PendingTask {
    private String taskId, taskName, assignedTo, status;

    public PendingTask() {
        // Required empty constructor for Firebase
    }

    public PendingTask(String taskId, String taskName, String assignedTo, String status) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.assignedTo = assignedTo;
        this.status = status;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
