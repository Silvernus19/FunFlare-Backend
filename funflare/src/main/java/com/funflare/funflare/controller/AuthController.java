package com.funflare.funflare.controller;

import com.funflare.funflare.dto.LoginRequestDTO;
import com.funflare.funflare.dto.LoginResponseDTO;
import com.funflare.funflare.service.LoginService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
@RestController
@RequestMapping("api/users")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final LoginService loginService;

    public AuthController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO request) {
        try {
            LoginResponseDTO response = loginService.login(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            log.warn("Bad credentials for email: {}", request.getEmail());
            ErrorResponse error = new ErrorResponse("Invalid email or password, or account not verified.");
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("Login error", e);
            ErrorResponse error = new ErrorResponse("Login failed. Please try again.");
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Validate Authorization header
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return new ResponseEntity<>("Invalid or missing Authorization header", HttpStatus.BAD_REQUEST);
            }
            // No server-side action needed; client should discard token
            return new ResponseEntity<>("Logout successful", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Logout error", e);
            return new ResponseEntity<>("Server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Inner class for error responses (now used)
    public static class ErrorResponse {
        private final String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}