package com.funflare.funflare.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.sql.Timestamp;
import java.util.Date;

public class EventCreateDTO {

    @NotBlank(message = "Event name is required")
    @Size( max = 100, message = "Event name must not exceed 100 characters")
    public String event_name;

    @NotBlank(message = "description for the event is required")
    @Size( max = 255, message = "description must exceed 100 characters")
    public String description;

    @NotBlank(message = "Event_date is required")
    public Date Event_date;

    @NotBlank(message = "Event location is required")
    @Size(max = 255, message = "Event loacation must not exceed 100 characters")
    public String location;

    @NotBlank(message = "Event posters are required")
    @Size( max = 100, message = "URL not exceed 12 characters")
    public String event_poster_url;

    private final String event_status = "upcoming";

    @NotBlank(message = "event capacity is required for ticket generation")
    public Integer event_capacity;

    @NotBlank(message = "event category is required")
    @Size(max = 50 , message = "event capacity must not exceed 50 characters")
    public String event_category;

    @NotBlank(message = "Event end date is required")
    public Timestamp Event_end_date;

    @NotBlank(message = "Event start time is required")
    public Date Event_start_time;

    @NotBlank(message = "Event end time is required")
    public Date Event_end_time;





    @NotBlank(message = "Password is required")
    @Size( min = 8, message = "password must exceed 8 characters")
    public String password;

//    Getters and setters
//    Some getters might be missing


    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getEvent_date() {
        return Event_date;
    }

    public void setEvent_date(Date event_date) {
        Event_date = event_date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEvent_poster_url() {
        return event_poster_url;
    }

    public void setEvent_poster_url(String event_poster_url) {
        this.event_poster_url = event_poster_url;
    }

    public String getEvent_status() {
        return event_status;
    }

    public String getEvent_category() {
        return event_category;
    }

    public void setEvent_category(String event_category) {
        this.event_category = event_category;
    }

    public Integer getEvent_capacity() {
        return event_capacity;
    }

    public void setEvent_capacity(Integer event_capacity) {
        this.event_capacity = event_capacity;
    }

    public Date getEvent_end_date() {
        return Event_end_date;
    }

    public void setEvent_end_date(Timestamp event_end_date) {
        Event_end_date = event_end_date;
    }

    public Date getEvent_start_time() {
        return Event_start_time;
    }

    public void setEvent_start_time(Date event_start_time) {
        Event_start_time = event_start_time;
    }

    public Date getEvent_end_time() {
        return Event_end_time;
    }

    public void setEvent_end_time(Date event_end_time) {
        Event_end_time = event_end_time;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
