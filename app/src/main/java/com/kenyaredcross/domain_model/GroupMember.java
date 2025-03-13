package com.kenyaredcross.domain_model;

public class GroupMember {
    private String email; // Email of the group member
    private String username; // Username of the group member

    // Default constructor required for Firebase
    public GroupMember() {}

    // Parameterized constructor
    public GroupMember(String email, String username) {
        this.email = email;
        this.username = username;
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

    @Override
    public String toString() {
        return "GroupMember{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}