package com.funflare.funflare.service;

import com.funflare.funflare.dto.EventCreateDTO;
import com.funflare.funflare.dto.EventsTicketsDTO;
import com.funflare.funflare.model.Event;
import com.funflare.funflare.model.Ticket;
import com.funflare.funflare.model.User;
import com.funflare.funflare.repository.EventRepository;
import com.funflare.funflare.repository.TicketRepository;
import com.funflare.funflare.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;  // Fixed: Use Spring's @Transactional import

import java.time.LocalTime;
import java.util.List;

@Service
public class EventService {

    private EventRepository eventRepository;
    private UserRepository userRepository;
    private TicketRepository ticketRepository;  // New: Inject for fetching tickets
    private static final Logger logger = LoggerFactory.getLogger(EventService.class);

    // Updated constructor to include TicketRepository
    public EventService(EventRepository eventRepository, UserRepository userRepository, TicketRepository ticketRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
    }

    @Transactional
    public Event createEvent(EventCreateDTO dto, Long userId) {
        boolean exists = eventRepository.existsByEventStartTimeAndLocationAndEventStartDate(
                dto.getEventStartTime(), dto.getLocation(), dto.getEventStartDate());
        if (exists) {
            logger.error("Event already exists at location {} on {} at {}",
                    dto.getLocation(), dto.getEventStartDate(), dto.getEventStartTime());
            throw new RuntimeException("Event already exists");
        }

        // validate event capacity
        if (dto.getEventCapacity() == null || dto.getEventCapacity() < 0) {
            logger.error("Event capacity is empty");
            throw new RuntimeException("Event capacity is empty");
        }

        // fetch user
        User organizer = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with id {} not found", userId);
                    return new RuntimeException("User not found");
                });
        if (organizer.getRole() != User.Role.ORGANIZER && organizer.getRole() != User.Role.ADMIN) {
            logger.error("Only organizers and admins can add events", userId);
            throw new RuntimeException("Only organizers and admins can add events");
        }

        // Map DTO to entity
        Event event = new Event();
        event.setName(dto.getName());
        event.setDescription(dto.getDescription());
        event.setLocation(dto.getLocation());
        event.setEventPosterUrl(dto.getEventPosterUrl()); // Optional: Keep if needed for URLs
        event.setEventPoster(dto.getEventPoster()); // Store binary poster as bytea
        event.setEventCapacity(dto.getEventCapacity());
        event.setEventCategory(dto.getEventCategory());
        event.setEventStatus(Event.EventStatus.ACTIVE); // Default per schema
        event.setEventStartDate(dto.getEventStartDate());
        event.setEventEndDate(dto.getEventEndDate());
        event.setEventStartTime(dto.getEventStartTime());
        event.setEventEndTime(dto.getEventEndTime());
        event.setOrganizer(organizer);

        // Save and return
        logger.info("Creating event: {}", event.getName());
        return eventRepository.save(event);
    }

    public Integer getCapacity(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        return event.getEventCapacity();
    }

    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    logger.error("Event with id {} not found", eventId);
                    return new RuntimeException("Event not found");
                });
    }

    // Add to EventService.java
    public List<Event> getOrganizerEvents(Long userId) {
        return eventRepository.findByOrganizerId(userId);
    }

    /**
     * Retrieves an event with its associated tickets for the given eventId.
     * Verifies ownership by the userId (organizer access only).
     * Maps to EventsTicketsDTO for response.
     *
     * @param eventId The ID of the event to fetch.
     * @param userId The ID of the user (organizer) requesting access.
     * @return EventsTicketsDTO containing event details and list of tickets.
     * @throws RuntimeException if event not found or access denied.
     */
    @Transactional(readOnly = true)  // Now works with correct import
    public EventsTicketsDTO getEventWithTickets(Long eventId, Long userId) {
        // Fetch and validate event (reuses existing method for consistency)
        Event event = getEventById(eventId);

        // Ownership check: Ensure the requester is the organizer
        if (!event.getOrganizer().getId().equals(userId)) {
            logger.error("Access denied: User {} is not the organizer of event {}", userId, eventId);
            throw new RuntimeException("Access denied: You are not authorized to view this event's details.");
        }

        // Fetch associated tickets (assumes TicketRepository has findByEventId)
        List<Ticket> tickets = ticketRepository.findByEventId(eventId);
        logger.info("Fetched {} tickets for event {}", tickets.size(), eventId);

        // Map to DTO
        return new EventsTicketsDTO(event, tickets);
    }
}