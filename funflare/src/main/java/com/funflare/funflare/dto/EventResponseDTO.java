// EventResponseDTO.java
package com.funflare.funflare.dto;

import com.funflare.funflare.model.Event;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Base64;

public class EventResponseDTO {

    private Long id;
    private String name;
    private String description;
    private String location;
    private String eventPosterUrl; // Kept for compatibility; can be used if URL generation is added later
    private String eventPosterBase64; // Base64 encoded poster image for frontend display
    private Integer eventCapacity;
    private String eventCategory;
    private Event.EventStatus eventStatus;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private LocalDate eventStartDate;
    private LocalDate eventEndDate;
    private LocalTime eventStartTime;
    private LocalTime eventEndTime;

    // Constructor mapping from Event entity
    public EventResponseDTO(Event event) {
        this.id = event.getId();
        this.name = event.getName();
        this.description = event.getDescription();
        this.location = event.getLocation();
        this.eventPosterUrl = event.getEventPosterUrl();
        // Encode byte[] poster to base64 for frontend
        if (event.getEventPoster() != null) {
            this.eventPosterBase64 = Base64.getEncoder().encodeToString(event.getEventPoster());
        }
        this.eventCapacity = event.getEventCapacity();
        this.eventCategory = event.getEventCategory();
        this.eventStatus = event.getEventStatus();
        this.createdAt = event.getCreatedAt();
        this.updatedAt = event.getUpdatedAt();
        this.eventStartDate = event.getEventStartDate();
        this.eventEndDate = event.getEventEndDate();
        this.eventStartTime = event.getEventStartTime();
        this.eventEndTime = event.getEventEndTime();
        // Note: eventPoster (byte[]) is excluded to avoid large payloads in response
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public String getEventPosterUrl() { return eventPosterUrl; }
    public String getEventPosterBase64() { return eventPosterBase64; }
    public Integer getEventCapacity() { return eventCapacity; }
    public String getEventCategory() { return eventCategory; }
    public Event.EventStatus getEventStatus() { return eventStatus; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public LocalDate getEventStartDate() { return eventStartDate; }
    public LocalDate getEventEndDate() { return eventEndDate; }
    public LocalTime getEventStartTime() { return eventStartTime; }
    public LocalTime getEventEndTime() { return eventEndTime; }
}