package com.funflare.funflare.controller;


import com.funflare.funflare.dto.OrganizerCreateDto;
import com.funflare.funflare.model.User;
import com.funflare.funflare.dto.AttendeeCreateDTO;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.funflare.funflare.service.UserService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/users")
public class UserController {
    private UserService userService;

    //default constructor
//    public UserController() {}

//    @Autowired
    public UserController(UserService userService) {


        this.userService = userService;
    }

    @PostMapping("/register/attendee")
    public ResponseEntity<User> registerAttendee(@Valid @RequestBody AttendeeCreateDTO dto) {
        User response = userService.registerAttendee(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

//    public String testEndpoint() {
//        return "endpoint hit!";
//    }

    @PostMapping("/register/organizer")
    public ResponseEntity<User> registerOrganizer(@Valid @RequestBody OrganizerCreateDto dto) {
        User response = userService.registerOrganizer(dto);
        return new ResponseEntity<User>(response, HttpStatus.CREATED);
    }
//    public String testEndpoint2() {
//        return "Hello, World!";
//    }


    @ControllerAdvice
    public static class GlobalExceptionHandler {

        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<Map<String, String>> handleValidationExceptions(ConstraintViolationException ex) {
            Map<String, String> errors = new HashMap<>();
            ex.getConstraintViolations().forEach(violation ->
                    errors.put(violation.getPropertyPath().toString(), violation.getMessage())
            );
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return new ResponseEntity<>(error, HttpStatus.CONFLICT);
        }
    }
}

