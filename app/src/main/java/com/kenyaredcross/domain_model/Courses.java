package com.kenyaredcross.domain_model;

import java.util.List;

public class Courses {
    private String id, title, description, duration, imageLink;
    private List<String> resources;

    public Courses() { }

    public Courses(String id, String title, String description, String duration, String imageLink) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.imageLink = imageLink;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDuration() { return duration; }
    public String getImageLink() { return imageLink; }
    public List<String> getResources() { return resources; }

    public void setResources(List<String> resources) { this.resources = resources; }
}
