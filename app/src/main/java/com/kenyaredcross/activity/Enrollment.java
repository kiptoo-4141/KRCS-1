package com.kenyaredcross.activity;

public class Enrollment {
    private String courseId;
    private String courseTitle;
    private String studentName;
    private String studentEmail;
    private String status;

    public Enrollment() {
        // Default constructor required for Firebase
    }

    public Enrollment(String courseId, String courseTitle, String studentName, String studentEmail, String status) {
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.status = status;
    }

    // Getters and Setters
    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}