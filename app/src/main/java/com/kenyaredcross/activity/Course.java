package com.kenyaredcross.activity;

public class Course {
    private String courseId;
    private String courseTitle;
    private String courseDescription;
    private String courseDuration;
    private String trainerName;
    private String trainerEmail;

    public Course() {
        // Default constructor required for Firebase
    }

    public Course(String courseId, String courseTitle, String courseDescription, String courseDuration, String trainerName, String trainerEmail) {
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.courseDescription = courseDescription;
        this.courseDuration = courseDuration;
        this.trainerName = trainerName;
        this.trainerEmail = trainerEmail;
    }

    // Getters and Setters
    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }

    public String getCourseDescription() { return courseDescription; }
    public void setCourseDescription(String courseDescription) { this.courseDescription = courseDescription; }

    public String getCourseDuration() { return courseDuration; }
    public void setCourseDuration(String courseDuration) { this.courseDuration = courseDuration; }

    public String getTrainerName() { return trainerName; }
    public void setTrainerName(String trainerName) { this.trainerName = trainerName; }

    public String getTrainerEmail() { return trainerEmail; }
    public void setTrainerEmail(String trainerEmail) { this.trainerEmail = trainerEmail; }
}