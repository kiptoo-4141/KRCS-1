package com.kenyaredcross.domain_model;

public class UserModel {

    private String email;
    private String password;
    private String role;
    private long signupTime;
    private String status;
    private String username;

    public UserModel() {
        // Default constructor for Firebase
    }

    public UserModel(String email, String password, String role, long signupTime, String status, String username) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.signupTime = signupTime;
        this.status = status;
        this.username = username;
    }

    // Getters and setters for each field
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public long getSignupTime() {
        return signupTime;
    }

    public void setSignupTime(long signupTime) {
        this.signupTime = signupTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
