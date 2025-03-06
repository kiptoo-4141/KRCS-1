package com.kenyaredcross.domain_model;

import java.util.Map;

public class CourseResource {
    private String courseId;
    private Map<String, String> courseActivities;
    private Map<String, String> courseOutlines;
    private Map<String, String> courseStructures;
    private Map<String, String> resourceLinks;

    public CourseResource() {
    }

    public CourseResource(String courseId, Map<String, String> courseActivities, Map<String, String> courseOutlines,
                          Map<String, String> courseStructures, Map<String, String> resourceLinks) {
        this.courseId = courseId;
        this.courseActivities = courseActivities;
        this.courseOutlines = courseOutlines;
        this.courseStructures = courseStructures;
        this.resourceLinks = resourceLinks;
    }

    public String getCourseId() {
        return courseId;
    }

    public Map<String, String> getCourseActivities() {
        return courseActivities;
    }

    public Map<String, String> getCourseOutlines() {
        return courseOutlines;
    }

    public Map<String, String> getCourseStructures() {
        return courseStructures;
    }

    public Map<String, String> getResourceLinks() {
        return resourceLinks;
    }
}
