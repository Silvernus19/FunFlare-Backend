package com.funflare.funflare.dto;

import com.funflare.funflare.model.Event;
import com.funflare.funflare.model.Ticket;
import com.funflare.funflare.dto.TicketResponseDTO;  // Assuming this exists; adjust path if needed

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO for retrieving an Event along with its associated Tickets.
 * This combines event details with a list of ticket details for a specific event.
 * Reuses existing EventResponseDTO and TicketResponseDTO for consistency.
 *
 * Note:
 * - Organizer is exposed minimally (e.g., only ID or name if needed; adjust based on security).
 * - Poster: Uses eventPosterUrl if available; otherwise, can add base64 encoding logic in mapper if needed.
 * - Ticket types are kept as enum strings (e.g., "EARLY_BIRD")—map to frontend-friendly strings in service if required.
 */
public class EventsTicketsDTO {

    private Long id;
    private Long organizerId;  // Minimal exposure; or use String organizerName if preferred
    private String name;
    private String description;
    private String location;
    private String eventPosterUrl;  // Or byte[] if embedding base64
    private Integer eventCapacity;
    private String eventCategory;
    private Event.EventStatus eventStatus;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private LocalDate eventStartDate;
    private LocalDate eventEndDate;
    private LocalTime eventStartTime;
    private LocalTime eventEndTime;

    private List<TicketResponseDTO> tickets = new ArrayList<>();  // List of associated tickets

    // Default constructor
    public EventsTicketsDTO() {}

    // Constructor from Event entity (for mapping in service)
    public EventsTicketsDTO(Event event, List<Ticket> tickets) {
        this.id = event.getId();
        this.organizerId = event.getOrganizer().getId();  // Assuming User has getId(); adjust if needed
        this.name = event.getName();
        this.description = event.getDescription();
        this.location = event.getLocation();
        this.eventPosterUrl = event.getEventPosterUrl();
        this.eventCapacity = event.getEventCapacity();
        this.eventCategory = event.getEventCategory();
        this.eventStatus = event.getEventStatus();
        this.createdAt = event.getCreatedAt();
        this.updatedAt = event.getUpdatedAt();
        this.eventStartDate = event.getEventStartDate();
        this.eventEndDate = event.getEventEndDate();
        this.eventStartTime = event.getEventStartTime();
        this.eventEndTime = event.getEventEndTime();

        // Map tickets to DTOs
        if (tickets != null) {
            this.tickets = tickets.stream()
                    .map(TicketResponseDTO::new)  // Assuming TicketResponseDTO has a constructor from Ticket
                    .collect(Collectors.toList());
        }
    }

    // Alternative: If you have EventResponseDTO, you could compose with it
    // public EventsTicketsDTO(EventResponseDTO eventDto, List<TicketResponseDTO> tickets) { ... }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(Long organizerId) {
        this.organizerId = organizerId;
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

    public Integer getEventCapacity() {
        return eventCapacity;
    }

    public void setEventCapacity(Integer eventCapacity) {
        this.eventCapacity = eventCapacity;
    }

    public String getEventCategory() {
        return eventCategory;
    }

    public void setEventCategory(String eventCategory) {
        this.eventCategory = eventCategory;
    }

    public Event.EventStatus getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(Event.EventStatus eventStatus) {
        this.eventStatus = eventStatus;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDate getEventStartDate() {
        return eventStartDate;
    }

    public void setEventStartDate(LocalDate eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public LocalDate getEventEndDate() {
        return eventEndDate;
    }

    public void setEventEndDate(LocalDate eventEndDate) {
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

    public List<TicketResponseDTO> getTickets() {
        return tickets;
    }

    public void setTickets(List<TicketResponseDTO> tickets) {
        this.tickets = tickets;
    }
}