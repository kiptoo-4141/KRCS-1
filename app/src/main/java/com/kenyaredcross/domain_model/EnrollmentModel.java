package com.kenyaredcross.domain_model;

public class EnrollmentModel {
    private String courseId;
    private String title;
    private String username;
    private String status;

    String email;

    public EnrollmentModel(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public EnrollmentModel() {}

    public String getCourseId() { return courseId; }
    public String getTitle() { return title; }
    public String getUsername() { return username; }
    public String getStatus() { return status; }
}
