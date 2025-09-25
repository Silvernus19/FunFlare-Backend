package com.funflare.funflare.service;

import com.funflare.funflare.dto.TicketCreateDTO;
import com.funflare.funflare.model.Event;
import com.funflare.funflare.model.Ticket;
import com.funflare.funflare.repository.EventRepository;
import com.funflare.funflare.repository.TicketRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TicketService {

    private static final Logger logger = LoggerFactory.getLogger(TicketService.class);

    private final EventService eventService;
    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;

    public TicketService(EventService eventService, TicketRepository ticketRepository, EventRepository eventRepository) {
        this.eventService = eventService;
        this.ticketRepository = ticketRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional
    public Ticket generateTickets(TicketCreateDTO dto) {
        // Log the incoming DTO type
        logger.debug("Received ticket type from DTO: {}", dto.getType());

        // Validate ticket type early
        if (dto.getType() == null || dto.getType().trim().isEmpty()) {
            logger.error("Ticket type is null or empty");
            throw new IllegalArgumentException("Ticket type cannot be null or empty");
        }

        // Normalize and validate ticket type (map input to enum)
        String normalizedType = dto.getType().trim().toUpperCase().replace("-", "_").replace(" ", "_");
        logger.debug("Normalized ticket type: {}", normalizedType);

        Ticket.Type ticketType;
        try {
            if ("EARLYBIRD".equalsIgnoreCase(dto.getType()) || "EARLY_BIRD".equalsIgnoreCase(dto.getType())) {
                ticketType = Ticket.Type.EARLY_BIRD;
            } else if ("ADVANCE".equalsIgnoreCase(dto.getType())) {
                ticketType = Ticket.Type.ADVANCE;
            } else {
                logger.error("Invalid ticket type provided: {}", dto.getType());
                throw new IllegalArgumentException("Invalid ticket type: " + dto.getType() + ". Must be EARLY_BIRD or ADVANCE.");
            }
        } catch (Exception e) {
            logger.error("Exception while mapping ticket type: {}. Error: {}", dto.getType(), e.getMessage());
            throw new IllegalArgumentException("Invalid ticket type: " + dto.getType() + ". Must be EARLY_BIRD or ADVANCE.");
        }

        // Log the mapped ticket type
        logger.debug("Mapped ticket type to enum: {}", ticketType);

        // Fetch event from repository
        Event event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + dto.getEventId()));

        // Check that tickets cannot be generated for cancelled or deleted events
        if (event.getEventStatus() == Event.EventStatus.CANCELED || event.getEventStatus() == Event.EventStatus.DELETED) {
            throw new IllegalStateException("Cannot generate tickets for cancelled or deleted events.");
        }

        // Check that tickets cannot be regenerated for the same active event
        boolean ticketExists = ticketRepository.existsByEventId(dto.getEventId());
        if (event.getEventStatus() == Event.EventStatus.ACTIVE && ticketExists) {
            throw new IllegalStateException("Cannot generate tickets for the same active event.");
        }

        // Check if the capacity is less than 1
        Integer capacity = eventService.getCapacity(dto.getEventId());
        if (capacity < 1) {
            throw new IllegalArgumentException("Capacity cannot be less than 1");
        }

        // Check that ticket quantity does not exceed the event capacity
        if (dto.getQuantity() > capacity) {
            throw new IllegalArgumentException("Ticket quantity cannot be greater than event capacity");
        }

        // Create a new ticket entity
        Ticket ticket = new Ticket();
        ticket.setEvent(event);
       // ticket.setEventName(event.getName() != null ? event.getName() : "Unknown Event");
        ticket.setType(ticketType); // Set validated ticket type
        ticket.setPrice(dto.getPrice());
        ticket.setQuantity(dto.getQuantity());
        ticket.setQuantitySold(0); // Initialize quantity sold to 0

        // Log the ticket type before saving
        logger.debug("Ticket type set on entity before save: {}", ticket.getType());

        // Set dates and times
        ticket.setSaleStartDate(dto.getSaleStartDate());
        ticket.setSaleEndDate(dto.getSaleEndDate());
        ticket.setSaleStartTime(dto.getSaleStartTime());
        ticket.setSaleEndTime(dto.getSaleEndTime());

        // Save to database
        Ticket savedTicket = ticketRepository.save(ticket);
        logger.debug("Saved ticket with type: {}", savedTicket.getType());

        return savedTicket;
    }
}