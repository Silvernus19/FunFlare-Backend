package com.funflare.funflare.service;


import com.funflare.funflare.dto.EventCreateDTO;
import com.funflare.funflare.model.Event;
import com.funflare.funflare.model.User;
import com.funflare.funflare.repository.EventRepository;
import com.funflare.funflare.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class EventService {

    private EventRepository eventRepository;
    private UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(EventService.class);


    public EventService(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
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
//        validate event capacity
        if (dto.getEventCapacity() == null || dto.getEventCapacity() < 0) {
            logger.error("Event capacity is empty");
            throw new RuntimeException("Event capacity is empty");
        }

//        fetch user
        User organizer = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with id {} not found", userId);
                    return new RuntimeException("User not found");
                });
        if (organizer.getRole() != User.Role.ORGANIZER && organizer.getRole() != User.Role.ADMIN) {
            logger.error("Only organizers and admins can add events", userId);
            throw new RuntimeException("Only organizers and admins can add events");
        }


        //map entity to dto


        // Map DTO to entity
        Event event = new Event();
        event.setName(dto.getName());
        event.setDescription(dto.getDescription());
        event.setLocation(dto.getLocation());
        event.setEventPosterUrl(dto.getEventPosterUrl());
        event.setEventCapacity(dto.getEventCapacity());
        event.setEventCategory(dto.getEventCategory());
        event.setEventStatus(Event.EventStatus.ACTIVE); // Default per schema
        event.setEventStartDate(dto.getEventStartDate());
        event.setEventEndDate(dto.getEventEndDate());
        event.setEventStartTime(dto.getEventStartTime());
        event.setEventEndTime(dto.getEventEndTime());
        //event.setMetadata(dto.getMetadata());
       // event.setEventPoster(dto.getEventPoster());
        event.setOrganizer(organizer);

        // Save and return
        logger.info("Creating event: {}", event.getName());
        return eventRepository.save(event);


    }

}
