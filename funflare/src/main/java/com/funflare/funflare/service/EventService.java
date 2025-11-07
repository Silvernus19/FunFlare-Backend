// src/main/java/com/funflare/funflare/service/EventService.java
package com.funflare.funflare.service;

import com.funflare.funflare.dto.EventBuyerDTO;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private static final Logger logger = LoggerFactory.getLogger(EventService.class);

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

        if (dto.getEventCapacity() == null || dto.getEventCapacity() < 0) {
            logger.error("Event capacity is invalid");
            throw new RuntimeException("Event capacity is required and must be positive");
        }

        User organizer = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with id {} not found", userId);
                    return new RuntimeException("User not found");
                });

        if (organizer.getRole() != User.Role.ORGANIZER && organizer.getRole() != User.Role.ADMIN) {
            logger.error("User {} is not authorized to create events", userId);
            throw new RuntimeException("Only organizers and admins can create events");
        }

        Event event = new Event();
        event.setName(dto.getName());
        event.setDescription(dto.getDescription());
        event.setLocation(dto.getLocation());
        event.setEventPosterUrl(dto.getEventPosterUrl());
        event.setEventPoster(dto.getEventPoster());
        event.setEventCapacity(dto.getEventCapacity());
        event.setEventCategory(dto.getEventCategory());
        event.setEventStatus(Event.EventStatus.ACTIVE);
        event.setEventStartDate(dto.getEventStartDate());
        event.setEventEndDate(dto.getEventEndDate());
        event.setEventStartTime(dto.getEventStartTime());
        event.setEventEndTime(dto.getEventEndTime());
        event.setOrganizer(organizer);

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

    public List<Event> getOrganizerEvents(Long userId) {
        return eventRepository.findByOrganizerId(userId);
    }

    @Transactional(readOnly = true)
    public EventsTicketsDTO getEventWithTickets(Long eventId, Long userId) {
        Event event = getEventById(eventId);

        if (!event.getOrganizer().getId().equals(userId)) {
            logger.error("Access denied: User {} is not the organizer of event {}", userId, eventId);
            throw new RuntimeException("Access denied: You are not authorized to view this event's details.");
        }

        List<Ticket> tickets = ticketRepository.findByEventId(eventId);
        logger.info("Fetched {} tickets for event {}", tickets.size(), eventId);

        return new EventsTicketsDTO(event, tickets);
    }

    // public event retrieval for buyers
    @Transactional(readOnly = true)
    public List<EventBuyerDTO> getPublicEvents() {
        // REMOVED: status + date filtering
        List<Event> events = eventRepository.findAll();

        logger.info("Fetched {} total events (all statuses, all dates)", events.size());

        return events.stream()
                .map(event -> {
                    List<Ticket> tickets = ticketRepository.findByEventId(event.getId());

                    List<EventBuyerDTO.TicketInfo> ticketInfos = tickets.stream()
                            .filter(t -> t.getQuantity() > 0)
                            .map(t -> new EventBuyerDTO.TicketInfo(
                                    t.getType().name(),
                                    t.getPrice(),
                                    t.getQuantity()
                            ))
                            .toList();

                    return new EventBuyerDTO(event, ticketInfos);
                })
                .toList();
    }
}