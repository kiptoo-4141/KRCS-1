package com.kenyaredcross.domain_model;

public class SupplierModel {
    private String email;
    private String username;
    private String role;
    private String status;

    public SupplierModel() {
        // Required empty constructor for Firebase
    }

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

    @Override
    public String toString() {
        return username + " (" + email + ")";
    }
}