// src/main/java/com/funflare/funflare/dto/EventBuyerDTO.java
package com.funflare.funflare.dto;

import com.funflare.funflare.model.Event;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Base64;
import java.util.List;
import java.util.logging.Logger;

public class EventBuyerDTO {

    private static final Logger logger = Logger.getLogger(EventBuyerDTO.class.getName());

    private Long id;
    private String name;
    private String description;
    private String location;
    private String eventPosterUrl;
    private String eventPosterBase64;
    private LocalDate startDate;
    private LocalTime startTime;
    private LocalDate endDate;
    private LocalTime endTime;
    private List<TicketInfo> tickets;

    public EventBuyerDTO(Event event, List<TicketInfo> tickets) {
        this.id = event.getId();
        this.name = event.getName();
        this.description = event.getDescription();
        this.location = event.getLocation();
        this.startDate = event.getEventStartDate();
        this.startTime = event.getEventStartTime();
        this.endDate = event.getEventEndDate();
        this.endTime = event.getEventEndTime();
        this.tickets = tickets;

        this.eventPosterUrl = event.getEventPosterUrl();
        if (this.eventPosterUrl == null && event.getEventPoster() != null && event.getEventPoster().length > 0) {
            try {
                String base64Data = Base64.getEncoder().encodeToString(event.getEventPoster());
                this.eventPosterUrl = "data:image/jpeg;base64," + base64Data;
                this.eventPosterBase64 = base64Data;
                logger.info("Generated base64 poster for buyer view - event ID: {}");
            } catch (Exception e) {
                logger.warning("Failed to encode poster for event {}: {}");
                this.eventPosterUrl = null;
                this.eventPosterBase64 = null;
            }
        }
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public String getEventPosterUrl() { return eventPosterUrl; }
    public String getEventPosterBase64() { return eventPosterBase64; }
    public LocalDate getStartDate() { return startDate; }
    public LocalTime getStartTime() { return startTime; }
    public LocalDate getEndDate() { return endDate; }
    public LocalTime getEndTime() { return endTime; }
    public List<TicketInfo> getTickets() { return tickets; }

    public static class TicketInfo {
        private String type;
        private BigDecimal price;
        private int available;

        public TicketInfo(String type, BigDecimal price, int available) {
            this.type = type;
            this.price = price;
            this.available = available;
        }

        public String getType() { return type; }
        public BigDecimal getPrice() { return price; }
        public int getAvailable() { return available; }
    }
}