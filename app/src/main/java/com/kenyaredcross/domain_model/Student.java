package com.kenyaredcross.domain_model;

public class Student {
    private String name;
    private String email;
    private String signTime;

    public Student() {
        // Required empty constructor for Firebase
    }

    public Student(String name, String email, String signTime) {
        this.name = name;
        this.email = email;
        this.signTime = signTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSignTime() {
        return signTime;
    }

    public void setSignTime(String signTime) {
        this.signTime = signTime;
    }
}