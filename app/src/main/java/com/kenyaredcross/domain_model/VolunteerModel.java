package com.kenyaredcross.domain_model;

public class VolunteerModel {
    private String email;
    private String username;
    private String role;
    private String status;

    public VolunteerModel() {
        // Default constructor required for Firebase
    }

    public VolunteerModel(String email, String username, String role, String status) {
        this.email = email;
        this.username = username;
        this.role = role;
        this.status = status;
    }

    // Getters and Setters
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}