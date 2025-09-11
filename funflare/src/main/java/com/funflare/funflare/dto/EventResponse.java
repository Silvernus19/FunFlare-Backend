//package com.funflare.funflare.dto;
//
//import com.fasterxml.jackson.annotation.JsonFormat;
//import com.funflare.funflare.model.Event;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//
//public class EventResponse{
//    private Long id;
//
//    private String name;
//
//    private String description;
//
//    private String location;
//
//    private String eventPosterUrl;
//
//    private Integer eventCapacity;
//
//    private String eventCategory;
//
//    private String eventStatus;
//
//    @JsonFormat(pattern = "yyyy-MM-dd")
//    private LocalDate eventStartDate;
//
//    @JsonFormat(pattern = "yyyy-MM-dd")
//    private LocalDate eventEndDate;
//
//    @JsonFormat(pattern = "HH:mm:ss")
//    private LocalTime eventStartTime;
//
//    @JsonFormat(pattern = "HH:mm:ss")
//    private LocalTime eventEndTime;
//
//    private String eventPoster;
//
//    private Long organizerId;
//
//    // Optional: Include organizer details if needed
//    private String organizerUsername;
//    private String organizerOrganizationName;
//
//    // Constructor for mapping from Event entity
//    public EventResponseDTO(Event event) {
//        this.id = event.getId();
//        this.name = event.getName();
//        this.description = event.getDescription();
//        this.location = event.getLocation();
//        this.eventPosterUrl = event.getEventPosterUrl();
//        this.eventCapacity = event.getEventCapacity();
//        this.eventCategory = event.getEventCategory();
//        this.eventStatus = event.getEventStatus().name();
//        this.eventStartDate = event.getEventStartDate();
//        this.eventEndDate = event.getEventEndDate();
//        this.eventStartTime = event.getEventStartTime();
//        this.eventEndTime = event.getEventEndTime();
//       // this.eventPoster = event.getEventPoster();
//        this.organizerId = event.getOrganizer().getId();
//        this.organizerUsername = event.getOrganizer().getUsername();
//        this.organizerOrganizationName = event.getOrganizer().getOrganizationName();
//    }
//
//    // Getters and setters
//    public Long getId() { return id; }
//    public void setId(Long id) { this.id = id; }
//    public String getName() { return name; }
//    public void setName(String name) { this.name = name; }
//    public String getDescription() { return description; }
//    public void setDescription(String description) { this.description = description; }
//    public String getLocation() { return location; }
//    public void setLocation(String location) { this.location = location; }
//    public String getEventPosterUrl() { return eventPosterUrl; }
//    public void setEventPosterUrl(String eventPosterUrl) { this.eventPosterUrl = eventPosterUrl; }
//    public Integer getEventCapacity() { return eventCapacity; }
//    public void setEventCapacity(Integer eventCapacity) { this.eventCapacity = eventCapacity; }
//    public String getEventCategory() { return eventCategory; }
//    public void setEventCategory(String eventCategory) { this.eventCategory = eventCategory; }
//    public String getEventStatus() { return eventStatus; }
//    public void setEventStatus(String eventStatus) { this.eventStatus = eventStatus; }
//    public LocalDate getEventStartDate() { return eventStartDate; }
//    public void setEventStartDate(LocalDate eventStartDate) { this.eventStartDate = eventStartDate; }
//    public LocalDate getEventEndDate() { return eventEndDate; }
//    public void setEventEndDate(LocalDate eventEndDate) { this.eventEndDate = eventEndDate; }
//    public LocalTime getEventStartTime() { return eventStartTime; }
//    public void setEventStartTime(LocalTime eventStartTime) { this.eventStartTime = eventStartTime; }
//    public LocalTime getEventEndTime() { return eventEndTime; }
//    public void setEventEndTime(LocalTime eventEndTime) { this.eventEndTime = eventEndTime; }
////    public String getEventPoster() { return eventPoster; }
////    public void setEventPoster(String eventPoster) { this.eventPoster = eventPoster; }
//    public Long getOrganizerId() { return organizerId; }
//    public void setOrganizerId(Long organizerId) { this.organizerId = organizerId; }
//    public String getOrganizerUsername() { return organizerUsername; }
//    public void setOrganizerUsername(String organizerUsername) { this.organizerUsername = organizerUsername; }
//    public String getOrganizerOrganizationName() { return organizerOrganizationName; }
//    public void setOrganizerOrganizationName(String organizerOrganizationName) { this.organizerOrganizationName = organizerOrganizationName; }
//}