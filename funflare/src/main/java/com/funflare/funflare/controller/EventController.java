package com.funflare.funflare.controller;

import com.funflare.funflare.dto.EventCreateDTO;
import com.funflare.funflare.dto.EventResponseDTO;
import com.funflare.funflare.model.Event;
import com.funflare.funflare.service.EventService;
import com.funflare.funflare.component.JwtUtil;
import com.funflare.funflare.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/events")
public class EventController {
    private final EventService eventService;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    public EventController(UserService userService, EventService eventService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.eventService = eventService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/create/event")
    public ResponseEntity<EventResponseDTO> createEvent(@RequestBody @Valid EventCreateDTO dto,
                                                        @RequestHeader("Authorization") String authorizationHeader,
                                                        Authentication authentication) {
        try {
            // Check if user is authenticated
            if (authentication == null || !authentication.isAuthenticated()) {
                logger.error("No authenticated user provided");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new EventResponseDTO(null));
            }

            // Extract userId from JWT
            String userId = jwtUtil.extractUserId(authorizationHeader);
            logger.info("Extracted userId from JWT: {}", userId);

            // Create event
            Event createdEvent = eventService.createEvent(dto, Long.parseLong(userId));
            logger.info("Event created successfully: {}", createdEvent.getName());
            EventResponseDTO response = new EventResponseDTO(createdEvent);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (NumberFormatException e) {
            logger.error("Invalid userId format in JWT: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new EventResponseDTO(null));
        } catch (IllegalArgumentException e) {
            logger.error("Failed to create event: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new EventResponseDTO(null));
        } catch (Exception e) {
            logger.error("Unexpected error creating event: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new EventResponseDTO(null));
        }
    }
}