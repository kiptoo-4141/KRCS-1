package com.kenyaredcross.domain_model;

public class VolunteerCheckModel {

    VolunteerCheckModel(){

    }

    String email, password, role, status, username;

    public VolunteerCheckModel(String email, String username, String status, String role, String password) {
        this.email = email;
        this.username = username;
        this.status = status;
        this.role = role;
        this.password = password;
    }

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
