package com.kenyaredcross.domain_model;

public class EventModel {
    private String event_description;
    private String event_fees;
    private String event_location;
    private String event_title;
    private String image_link;


    // Default constructor required for calls to DataSnapshot.getValue(EventModel.class)
    public EventModel() {
    }

    public EventModel(String event_description, String event_title, String image_link, String event_location, String event_fees) {
        this.event_description = event_description;
        this.event_title = event_title;
        this.image_link = image_link;
        this.event_location = event_location;
        this.event_fees = event_fees;
    }


    // Getters and setters
    public String getEvent_description() {
        return event_description;
    }

    public void setEvent_description(String event_description) {
        this.event_description = event_description;
    }

    public String getEvent_fees() {
        return event_fees;
    }

    public void setEvent_fees(String event_fees) {
        this.event_fees = event_fees;
    }

    public String getEvent_location() {
        return event_location;
    }

    public void setEvent_location(String event_location) {
        this.event_location = event_location;
    }

    public String getImage_link() {
        return image_link;
    }

    public void setImage_link(String image_link) {
        this.image_link = image_link;
    }

    public String getEvent_title() {
        return event_title;
    }

    public void setEvent_title(String event_title) {
        this.event_title = event_title;
    }
}
