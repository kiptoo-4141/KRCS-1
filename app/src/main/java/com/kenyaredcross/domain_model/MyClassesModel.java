package com.kenyaredcross.domain_model;

public class MyClassesModel {
    private String courseId, title, description, duration, certificationStatus, image, email, username, status;

    public MyClassesModel() { }

    public MyClassesModel(String courseId, String title, String description, String duration, String certificationStatus, String image, String email, String username, String status) {
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.certificationStatus = certificationStatus;
        this.image = image;
        this.email = email;
        this.username = username;
        this.status = status;
    }

    public String getCourseId() { return courseId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDuration() { return duration; }
    public String getCertificationStatus() { return certificationStatus; }
    public String getImage() { return image; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public String getStatus() { return status; }
}