package com.kenyaredcross.domain_model;

public class EventModel2 {
    private String event_ID;
    private String event_title;
    private String event_description;
    private String event_location;
    private String event_fees;
    private String event_date;

    // No-argument constructor (required by Firebase)
    public EventModel2() {
        // Default constructor
    }

    // Parameterized constructor (optional, for convenience)
    public EventModel2(String event_ID, String event_title, String event_description, String event_location, String event_fees, String event_date) {
        this.event_ID = event_ID;
        this.event_title = event_title;
        this.event_description = event_description;
        this.event_location = event_location;
        this.event_fees = event_fees;
        this.event_date = event_date;
    }

    // Getters and Setters
    public String getEvent_ID() { return event_ID; }
    public void setEvent_ID(String event_ID) { this.event_ID = event_ID; }

    public String getEvent_title() { return event_title; }
    public void setEvent_title(String event_title) { this.event_title = event_title; }

    public String getEvent_description() { return event_description; }
    public void setEvent_description(String event_description) { this.event_description = event_description; }

    public String getEvent_location() { return event_location; }
    public void setEvent_location(String event_location) { this.event_location = event_location; }

    public String getEvent_fees() { return event_fees; }
    public void setEvent_fees(String event_fees) { this.event_fees = event_fees; }

    public String getEvent_date() { return event_date; }
    public void setEvent_date(String event_date) { this.event_date = event_date; }
}