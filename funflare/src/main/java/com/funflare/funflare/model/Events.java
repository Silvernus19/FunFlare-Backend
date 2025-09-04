package com.funflare.funflare.model;


import jakarta.persistence.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Entity
@Table(name = "events")
public class Events {

    public enum event_status
    {
        upcoming,
        ongoing,
        Ended
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "name", length = 50)
    private String name;
    @Column(name = "description", length = 255)
    private String description;
    @Column(name = "event_date", nullable = false)
    private Date eventDate;
    @Column(name = "location")
    private String location;
    @Column(name = "event_poster_url", length = 255)
    private String eventPosterUrl;
    @Column(name = "event_capacity", nullable = false)
    private int eventCapacity;
    @Column(name = "event_category")
    private String eventCategory;

//    Enumerated status variable, more status in the future

    @Enumerated(EnumType.STRING)
    @Column(name = "event_status")
    private event_status eventStatus;
    @Column(name = "created_at")
    private Date createdAt;
    @Column(name= "updated_at")
    private Date updatedAt;
    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;
    @Column(name = "event_start_date", nullable = false)
    private LocalDate eventStartDate;

    @Column(name = "event_end_date", nullable = false)
    @Temporal(TemporalType.DATE)  // 👈 tells Hibernate to use SQL DATE
    private Date eventEndDate;
    @Column(name = "event_start_time", nullable = false)
    private LocalTime eventStartTime;
    @Column(name = "event_end_time", nullable = false)
    private LocalTime eventEndTime;

//    Getters and setters


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEventPosterUrl() {
        return eventPosterUrl;
    }

    public void setEventPosterUrl(String eventPosterUrl) {
        this.eventPosterUrl = eventPosterUrl;
    }

    public int getEventCapacity() {
        return eventCapacity;
    }

    public void setEventCapacity(int eventCapacity) {
        this.eventCapacity = eventCapacity;
    }

    public String getEventCategory() {
        return eventCategory;
    }

    public void setEventCategory(String eventCategory) {
        this.eventCategory = eventCategory;
    }

    public event_status getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(event_status eventStatus) {
        this.eventStatus = eventStatus;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public LocalDate getEventStartDate() {
        return eventStartDate;
    }

    public void setEventStartDate(LocalDate eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public Date getEventEndDate() {
        return eventEndDate;
    }

    public void setEventEndDate(Date eventEndDate) {
        this.eventEndDate = eventEndDate;
    }

    public LocalTime getEventStartTime() {
        return eventStartTime;
    }

    public void setEventStartTime(LocalTime eventStartTime) {
        this.eventStartTime = eventStartTime;
    }

    public LocalTime getEventEndTime() {
        return eventEndTime;
    }

    public void setEventEndTime(LocalTime eventEndTime) {
        this.eventEndTime = eventEndTime;
    }
}
