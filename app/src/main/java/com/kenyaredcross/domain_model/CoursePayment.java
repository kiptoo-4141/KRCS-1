package com.kenyaredcross.domain_model;

public class CoursePayment {
    private String courseId, userEmail, paymentMethod, paymentDetails, date, time, status;
    private String certificationStatus;
    private String description;
    private String duration;
    private String image;
    private String title;
    private String username;

    public CoursePayment(String image, String username, String title, String duration, String description, String certificationStatus) {
        this.image = image;
        this.username = username;
        this.title = title;
        this.duration = duration;
        this.description = description;
        this.certificationStatus = certificationStatus;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCertificationStatus() {
        return certificationStatus;
    }

    public void setCertificationStatus(String certificationStatus) {
        this.certificationStatus = certificationStatus;
    }

    private int amount;

    public CoursePayment() {
        // Required for Firebase
    }

    public CoursePayment(String courseId, String userEmail, String paymentMethod, String paymentDetails, String date, String time, String status, int amount) {
        this.courseId = courseId;
        this.userEmail = userEmail;
        this.paymentMethod = paymentMethod;
        this.paymentDetails = paymentDetails;
        this.date = date;
        this.time = time;
        this.status = status;
        this.amount = amount;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getPaymentDetails() {
        return paymentDetails;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }

    public int getAmount() {
        return amount;
    }
}
