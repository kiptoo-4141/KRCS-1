package com.kenyaredcross.domain_model;

public class RequestModel {
    private String certificationStatus;
    private String description;
    private String duration;
    private String email;
    private String image;
    private String status;
    private String title;
    private String username;
    private String userId; // New field
    private String courseId; // New field

    // Default constructor (required for Firebase)
    public RequestModel() {}

    // Getters and Setters for all fields
    public String getCertificationStatus() {
        return certificationStatus;
    }

    public void setCertificationStatus(String certificationStatus) {
        this.certificationStatus = certificationStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // New getter and setter for userId
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // New getter and setter for courseId
    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
}