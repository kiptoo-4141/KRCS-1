package com.kenyaredcross.domain_model;

import java.util.List;

public class CourseResource2 {
    private List<String> courseActivities;
    private List<String> courseOutlines;
    private List<String> courseStructures;
    private List<String> resourceLinks;

    public CourseResource2() {
    }

    public List<String> getCourseActivities() {
        return courseActivities;
    }

    public void setCourseActivities(List<String> courseActivities) {
        this.courseActivities = courseActivities;
    }

    public List<String> getCourseOutlines() {
        return courseOutlines;
    }

    public void setCourseOutlines(List<String> courseOutlines) {
        this.courseOutlines = courseOutlines;
    }

    public List<String> getCourseStructures() {
        return courseStructures;
    }

    public void setCourseStructures(List<String> courseStructures) {
        this.courseStructures = courseStructures;
    }

    public List<String> getResourceLinks() {
        return resourceLinks;
    }

    public void setResourceLinks(List<String> resourceLinks) {
        this.resourceLinks = resourceLinks;
    }
}