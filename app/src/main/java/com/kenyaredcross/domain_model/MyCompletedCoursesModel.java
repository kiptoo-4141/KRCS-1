package com.kenyaredcross.domain_model;

public class MyCompletedCoursesModel {
    private String courseId, title, description, duration, image, certificationStatus, status;

    public MyCompletedCoursesModel() {
        // Empty constructor needed for Firebase
    }

    public MyCompletedCoursesModel(String courseId, String title, String description, String duration, String image, String certificationStatus, String status) {
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.image = image;
        this.certificationStatus = certificationStatus;
        this.status = status;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDuration() {
        return duration;
    }

    public String getImage() {
        return image;
    }

    public String getCertificationStatus() {
        return certificationStatus;
    }

    public String getStatus() {
        return status;
    }
}
