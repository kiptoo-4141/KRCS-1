package com.kenyaredcross.domain_model;

import java.util.HashMap;
import java.util.Map;

public class AttendanceRecord {
    private String id;
    private String courseId;
    private String courseTitle;
    private String trainerName;
    private String trainerEmail;
    private boolean active;
    private int durationMinutes;
    private String activationTime;
    private String expirationTime;
    private Map<String, Student> students = new HashMap<>();
    private boolean signedByCurrentUser; // New field to track signing status
    private int durationDuration;

    public AttendanceRecord() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public String getTrainerName() {
        return trainerName;
    }

    public void setTrainerName(String trainerName) {
        this.trainerName = trainerName;
    }

    public String getTrainerEmail() {
        return trainerEmail;
    }

    public void setTrainerEmail(String trainerEmail) {
        this.trainerEmail = trainerEmail;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationDuration = durationMinutes;
    }

    public String getActivationTime() {
        return activationTime;
    }

    public void setActivationTime(String activationTime) {
        this.activationTime = activationTime;
    }

    public String getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(String expirationTime) {
        this.expirationTime = expirationTime;
    }

    public Map<String, Student> getStudents() {
        return students;
    }

    public void setStudents(Map<String, Student> students) {
        this.students = students;
    }

    /**
     * Checks if the current user has already signed this attendance record.
     *
     * @param currentUserEmail The email of the current user.
     * @return True if the user has signed, false otherwise.
     */
    public boolean isSignedByCurrentUser(String currentUserEmail) {
        if (students == null || students.isEmpty()) {
            return false;
        }

        // Replace dots in email to match Firebase key format
        String emailKey = currentUserEmail.replace(".", "_");

        // Check if the current user's email exists in the students map
        return students.containsKey(emailKey);
    }

    /**
     * Sets whether the current user has signed this attendance record.
     *
     * @param signedByCurrentUser True if the user has signed, false otherwise.
     */
    public void setSignedByCurrentUser(boolean signedByCurrentUser) {
        this.signedByCurrentUser = signedByCurrentUser;
    }

    /**
     * Returns whether the current user has signed this attendance record.
     *
     * @return True if the user has signed, false otherwise.
     */
    public boolean isSignedByCurrentUser() {
        return signedByCurrentUser;
    }
}