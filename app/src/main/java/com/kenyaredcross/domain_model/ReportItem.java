package com.kenyaredcross.domain_model;

public class ReportItem {
    private String title, description, duration, status, trainerName, paymentMethod, date, time;
    private double amount;

    // Constructor for Enrollments and CompletedCourses
    public ReportItem(String title, String description, String duration, String status) {
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.status = status;
    }

    // Constructor for AssignedCourses
    public ReportItem(String title, String description, String duration, String trainerName, boolean isAssignedCourse) {
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.trainerName = trainerName;
    }

    // Constructor for CoursePayments
    public ReportItem(String title, String paymentMethod, String status, String date, String time, double amount) {
        this.title = title;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.date = date;
        this.time = time;
        this.amount = amount;
    }

    // Getters
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDuration() { return duration; }
    public String getStatus() { return status; }
    public String getTrainerName() { return trainerName; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public double getAmount() { return amount; }
}
