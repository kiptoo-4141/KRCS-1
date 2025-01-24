package com.kenyaredcross.domain_model;

public class CoursesModel
{
    String id,title, description, duration, image_link, certification_status;
    Integer fees;

    CoursesModel(){

    }

    public CoursesModel(Integer fees) {
        this.fees = fees;
    }

    public Integer getFees() {
        return fees;
    }

    public void setFees(Integer fees) {
        this.fees = fees;
    }

    public CoursesModel(String id, String title, String description, String duration, String image_link, String certification_status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.image_link = image_link;
        this.certification_status = certification_status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getImage_link() {
        return image_link;
    }

    public void setImage_link(String image_link) {
        this.image_link = image_link;
    }

    public String getCertification_status() {
        return certification_status;
    }

    public void setCertification_status(String certification_status) {
        this.certification_status = certification_status;
    }
}
