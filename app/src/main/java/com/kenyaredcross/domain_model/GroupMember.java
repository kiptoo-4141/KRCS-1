package com.kenyaredcross.domain_model;

public class GroupMember {
    private String email;
    private String username;

    public GroupMember() {}

    public GroupMember(String email, String username) {
        this.email = email;
        this.username = username;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
