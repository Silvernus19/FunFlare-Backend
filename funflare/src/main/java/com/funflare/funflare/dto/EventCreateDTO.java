package com.funflare.funflare.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class EventCreateDTO {

    @NotBlank(message = "Event name is required")
    @Size(max = 255, message = "Event name must not exceed 255 characters")
    private String name;

    @Size(max = 65535, message = "Description must not exceed 65535 characters")
    private String description;

    @NotBlank(message = "Location is required")
    @Size(max = 255, message = "Location must not exceed 255 characters")
    private String location;

    @Size(max = 255, message = "Event poster URL must not exceed 255 characters")
    private String eventPosterUrl;

    @Min(value = 0, message = "Event capacity must be non-negative")
    private Integer eventCapacity;

    @Size(max = 100, message = "Event category must not exceed 100 characters")
    private String eventCategory;

    @NotNull(message = "Event start date is required")
    private LocalDate eventStartDate;

    @NotNull(message = "Event end date is required")
    private LocalDate eventEndDate;

    @NotNull(message = "Event start time is required")
    private LocalTime eventStartTime;

    @NotNull(message = "Event end time is required")
    private LocalTime eventEndTime;

    // Binary data for event poster (stored as bytea in Postgres)
    // Optional: Add @Size(max = 5242880, message = "Poster size must not exceed 5MB") for validation
    private byte[] eventPoster;

    // Getters and setters

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getEventPosterUrl() { return eventPosterUrl; }
    public void setEventPosterUrl(String eventPosterUrl) { this.eventPosterUrl = eventPosterUrl; }

    public Integer getEventCapacity() { return eventCapacity; }
    public void setEventCapacity(Integer eventCapacity) { this.eventCapacity = eventCapacity; }

    public String getEventCategory() { return eventCategory; }
    public void setEventCategory(String eventCategory) { this.eventCategory = eventCategory; }

    public LocalDate getEventStartDate() { return eventStartDate; }
    public void setEventStartDate(LocalDate eventStartDate) { this.eventStartDate = eventStartDate; }

    public LocalDate getEventEndDate() { return eventEndDate; }
    public void setEventEndDate(LocalDate eventEndDate) { this.eventEndDate = eventEndDate; }

    public LocalTime getEventStartTime() { return eventStartTime; }
    public void setEventStartTime(LocalTime eventStartTime) { this.eventStartTime = eventStartTime; }

    public LocalTime getEventEndTime() { return eventEndTime; }
    public void setEventEndTime(LocalTime eventEndTime) { this.eventEndTime = eventEndTime; }

    public byte[] getEventPoster() { return eventPoster; }
    public void setEventPoster(byte[] eventPoster) { this.eventPoster = eventPoster; }
}