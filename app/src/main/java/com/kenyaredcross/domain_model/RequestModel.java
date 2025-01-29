package com.kenyaredcross.domain_model;

public class RequestModel {
    private String certificationStatus, description, duration, email, image, status, title, username;

    public RequestModel() {
        // Default constructor required for Firebase
    }

    public RequestModel(String certificationStatus, String description, String duration, String email, String image, String status, String title, String username) {
        this.certificationStatus = certificationStatus;
        this.description = description;
        this.duration = duration;
        this.email = email;
        this.image = image;
        this.status = status;
        this.title = title;
        this.username = username;
    }

    public String getCertificationStatus() {
        return certificationStatus;
    }

    public String getDescription() {
        return description;
    }

    public String getDuration() {
        return duration;
    }

    public String getEmail() {
        return email;
    }

    public String getImage() {
        return image;
    }

    public String getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getUsername() {
        return username;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
