package com.kenyaredcross.domain_model;

public class EnrolledCourseModel {
    private String id;
    private String title;
    private String description;
    private String duration;
    private String image_link;
    private String certificationStatus; // Added to match Firebase field
    private String courseStatus;
    private String image;
    private String status;
    private String email; // Added
    private String username; // Added

    // Default constructor (required for Firebase)
    public EnrolledCourseModel() {
    }

    // Constructor with parameters
    public EnrolledCourseModel(String id, String title, String description, String duration, String image_link, String certificationStatus, String courseStatus, String email, String username) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.image_link = image_link;
        this.certificationStatus = certificationStatus;
        this.courseStatus = courseStatus;
        this.email = email;
        this.username = username;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getImage_link() {
        return image_link;
    }

    public void setImage_link(String image_link) {
        this.image_link = image_link;
    }

    public String getCertificationStatus() {
        return certificationStatus;
    }

    public void setCertificationStatus(String certificationStatus) {
        this.certificationStatus = certificationStatus;
    }

    public String getCourseStatus() {
        return courseStatus;
    }

    public void setCourseStatus(String courseStatus) {
        this.courseStatus = courseStatus;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}