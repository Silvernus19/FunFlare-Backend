package com.funflare.funflare.controller;

import com.fasterxml.jackson.databind.ObjectMapper;  // Add this import
import com.funflare.funflare.dto.ErrorResponse;
import com.funflare.funflare.dto.EventCreateDTO;
import com.funflare.funflare.dto.EventResponseDTO;
import com.funflare.funflare.model.Event;
import com.funflare.funflare.service.EventService;
import com.funflare.funflare.component.JwtUtil;
import com.funflare.funflare.service.UserService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;  // Still used, but after parsing
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
    private final ObjectMapper objectMapper;  // Add this
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);
    private static final long MAX_POSTER_SIZE = 5 * 1024 * 1024; // 5MB limit

    public EventController(UserService userService, EventService eventService, JwtUtil jwtUtil, ObjectMapper objectMapper) {  // Add objectMapper to constructor
        this.userService = userService;
        this.eventService = eventService;
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    @PostMapping(value = "/create/event", consumes = "multipart/form-data")
    public ResponseEntity<?> createEvent(@RequestPart("dto") String dtoJson,  // Change to String
                                         @RequestPart(value = "poster", required = false) MultipartFile poster,
                                         @RequestHeader("Authorization") String authorizationHeader,
                                         Authentication authentication) {
        try {
            // Parse JSON string to DTO
            EventCreateDTO dto = objectMapper.readValue(dtoJson, EventCreateDTO.class);

            // Manual validation after parsing (since @Valid doesn't work directly on String)
            BindingResult bindingResult = validateDto(dto);  // Implement this helper
            if (bindingResult.hasErrors()) {
                String errors = bindingResult.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .collect(Collectors.joining(", "));
                logger.error("Validation errors: {}", errors);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Validation failed: " + errors));
            }

            // Check if the user is authenticated
            if (authentication == null || !authentication.isAuthenticated()) {
                logger.error("No authenticated user provided");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("User not authenticated"));
            }

            // Handle poster upload
            if (poster != null) {
                if (poster.getSize() > MAX_POSTER_SIZE) {
                    logger.error("Poster file too large: {} bytes", poster.getSize());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ErrorResponse("Poster file exceeds 5MB limit"));
                }
                try {
                    dto.setEventPoster(poster.getBytes());
                } catch (IOException e) {
                    logger.error("Failed to read poster file: {}", e.getMessage());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ErrorResponse("Failed to read poster file"));
                }
            }

            // Extract userId from JWT
            String userId = jwtUtil.extractUserId(authorizationHeader);
            logger.info("Extracted userId from JWT: {}", userId);

            // Create event
            Event createdEvent = eventService.createEvent(dto, Long.parseLong(userId));
            logger.info("Event created successfully: {}", createdEvent.getName());
            EventResponseDTO response = new EventResponseDTO(createdEvent);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException e) {  // For ObjectMapper.readValue
            logger.error("Failed to parse DTO JSON: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid JSON in dto: " + e.getMessage()));
        } catch (NumberFormatException e) {
            logger.error("Invalid userId format in JWT: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid user ID format in token"));
        } catch (IllegalArgumentException e) {
            logger.error("Failed to create event: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Failed to create event: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error creating event: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An unexpected error occurred"));
        }
    }

    // Add to EventController.java
    @GetMapping("/organizer")
    public ResponseEntity<?> getOrganizerEvents(@RequestHeader("Authorization") String authorizationHeader,
                                                Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                logger.error("No authenticated user provided");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("User not authenticated"));
            }

            String userId = jwtUtil.extractUserId(authorizationHeader);
            logger.info("Fetching events for organizer userId: {}", userId);

            List<Event> events = eventService.getOrganizerEvents(Long.parseLong(userId));
            List<EventResponseDTO> response = events.stream()
                    .map(EventResponseDTO::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (NumberFormatException e) {
            logger.error("Invalid userId format in JWT: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid user ID format in token"));
        } catch (Exception e) {
            logger.error("Unexpected error fetching organizer events: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An unexpected error occurred"));
        }
    }

    // Helper method for manual validation (inject Validator if needed, but this uses Bean Validation)
    private BindingResult validateDto(EventCreateDTO dto) {
        // Use Spring's Validator (autowired or manual)
        // For simplicity, assuming you have jakarta.validation.Validator injected or use reflection
        // Quick impl: Use a dummy BindingResult
        jakarta.validation.Validator validator = jakarta.validation.Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<EventCreateDTO>> violations = validator.validate(dto);
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "dto");
        violations.forEach(v -> bindingResult.rejectValue(v.getPropertyPath().toString(), "", v.getMessage()));
        return bindingResult;
    }
}