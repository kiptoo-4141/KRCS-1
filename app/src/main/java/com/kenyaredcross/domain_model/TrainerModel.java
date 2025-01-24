package com.kenyaredcross.domain_model;

public class TrainerModel {
    private String username;
    private String email;

    public TrainerModel() {}

    public TrainerModel(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

