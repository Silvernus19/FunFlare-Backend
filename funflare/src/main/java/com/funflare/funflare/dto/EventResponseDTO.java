// EventResponseDTO.java
package com.funflare.funflare.dto;

import com.funflare.funflare.model.Event;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.logging.Logger;  // For simple logging (or use SLF4J if preferred)

public class EventResponseDTO {

    private static final Logger logger = Logger.getLogger(EventResponseDTO.class.getName());

    private Long id;
    private String name;
    private String description;
    private String location;
    private String eventPosterUrl; // Full data URI for frontend <img src>
    private String eventPosterBase64; // Optional raw base64
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
        this.eventCapacity = event.getEventCapacity();
        this.eventCategory = event.getEventCategory();
        this.eventStatus = event.getEventStatus();
        this.createdAt = event.getCreatedAt();
        this.updatedAt = event.getUpdatedAt();
        this.eventStartDate = event.getEventStartDate();
        this.eventEndDate = event.getEventEndDate();
        this.eventStartTime = event.getEventStartTime();
        this.eventEndTime = event.getEventEndTime();

        // Handle poster: Prefer existing URL, else convert byte[] to full base64 data URI
        this.eventPosterUrl = event.getEventPosterUrl();
        if (this.eventPosterUrl == null && event.getEventPoster() != null && event.getEventPoster().length > 0) {
            try {
                String base64Data = Base64.getEncoder().encodeToString(event.getEventPoster());
                this.eventPosterUrl = "data:image/jpeg;base64," + base64Data;  // Prefix for <img src>
                this.eventPosterBase64 = base64Data;  // Optional raw version
                logger.info("Generated base64 poster URL for event ID: " + event.getId() + " (length: " + base64Data.length() + ")");
            } catch (Exception e) {
                logger.warning("Failed to encode poster for event " + event.getId() + ": " + e.getMessage());
                this.eventPosterUrl = null;
                this.eventPosterBase64 = null;
            }
        } else if (event.getEventPoster() == null) {
            logger.info("No poster found for event ID: " + event.getId());
        }
    }

    // Getters (unchanged)
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

    // Setters (add if needed for other uses; optional here)
    // e.g., public void setEventPosterUrl(String eventPosterUrl) { this.eventPosterUrl = eventPosterUrl; }
}