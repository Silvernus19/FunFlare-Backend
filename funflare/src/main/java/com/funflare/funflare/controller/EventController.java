package com.funflare.funflare.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.funflare.funflare.dto.*;
import com.funflare.funflare.model.Event;
import com.funflare.funflare.service.EventService;
import com.funflare.funflare.component.JwtUtil;
import com.funflare.funflare.service.UserService;
import jakarta.validation.ConstraintViolation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/events")
public class EventController {

    private final EventService eventService;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);
    private static final long MAX_POSTER_SIZE = 5 * 1024 * 1024; // 5MB

    public EventController(UserService userService, EventService eventService, JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.userService = userService;
        this.eventService = eventService;
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    // CREATE EVENT
    @PostMapping(value = "/create/event", consumes = "multipart/form-data")
    public ResponseEntity<?> createEvent(
            @RequestPart("dto") String dtoJson,
            @RequestPart(value = "poster", required = false) MultipartFile poster,
            @RequestHeader("Authorization") String authorizationHeader,
            Authentication authentication) {

        try {
            EventCreateDTO dto = objectMapper.readValue(dtoJson, EventCreateDTO.class);
            BindingResult bindingResult = validateDto(dto);  // Now resolved

            if (bindingResult.hasErrors()) {
                String errors = bindingResult.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .collect(Collectors.joining(", "));
                logger.error("Validation errors: {}", errors);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Validation failed: " + errors));
            }

            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("User not authenticated"));
            }

            if (poster != null && poster.getSize() > MAX_POSTER_SIZE) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Poster file exceeds 5MB limit"));
            }

            if (poster != null) {
                dto.setEventPoster(poster.getBytes());
            }

            String userId = jwtUtil.extractUserId(authorizationHeader);
            Event createdEvent = eventService.createEvent(dto, Long.parseLong(userId));
            return ResponseEntity.status(HttpStatus.CREATED).body(new EventResponseDTO(createdEvent));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid JSON in dto: " + e.getMessage()));
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid user ID format in token"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Failed to create event: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An unexpected error occurred"));
        }
    }

    // ORGANIZER EVENTS
    @GetMapping("/organizer")
    public ResponseEntity<?> getOrganizerEvents(
            @RequestHeader("Authorization") String authorizationHeader,
            Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("User not authenticated"));
            }

            String userId = jwtUtil.extractUserId(authorizationHeader);
            List<Event> events = eventService.getOrganizerEvents(Long.parseLong(userId));
            List<EventResponseDTO> response = events.stream()
                    .map(EventResponseDTO::new)
                    .toList();

            return ResponseEntity.ok(response);

        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid user ID format in token"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An unexpected error occurred"));
        }
    }

    // EVENT + TICKETS (ORGANIZER)
    @GetMapping("/{eventId}/details")
    public ResponseEntity<?> getEventWithTickets(
            @PathVariable Long eventId,
            @RequestHeader("Authorization") String authorizationHeader,
            Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("User not authenticated"));
            }

            String userIdStr = jwtUtil.extractUserId(authorizationHeader);
            Long userId = Long.parseLong(userIdStr);

            EventsTicketsDTO response = eventService.getEventWithTickets(eventId, userId);
            return ResponseEntity.ok(response);

        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid user ID format in token"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Access denied")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
            } else if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An unexpected error occurred"));
        }
    }

    // BUYER: PUBLIC EVENTS
    @GetMapping("/public")
    public ResponseEntity<?> getPublicEvents() {
        try {
            logger.info("Fetching public events");
            List<EventBuyerDTO> events = eventService.getPublicEvents();
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            logger.error("Error fetching public events: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to load events"));
        }
    }

    // FIXED: validateDto() METHOD
    private BindingResult validateDto(EventCreateDTO dto) {
        jakarta.validation.Validator validator = jakarta.validation.Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<EventCreateDTO>> violations = validator.validate(dto);
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "dto");
        violations.forEach(v -> bindingResult.rejectValue(v.getPropertyPath().toString(), "", v.getMessage()));
        return bindingResult;
    }
}