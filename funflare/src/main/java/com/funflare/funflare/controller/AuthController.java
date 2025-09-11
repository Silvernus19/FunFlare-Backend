package com.funflare.funflare.controller;


import com.funflare.funflare.dto.LoginRequestDTO;
import com.funflare.funflare.dto.LoginResponseDTO;
import com.funflare.funflare.service.LoginService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;


//@CrossOrigin(origins = "https://inquisitive-tarsier-7fb9f9.netlify.app")

@RestController
@RequestMapping("api/auth")
public class AuthController {

    private LoginService loginService;

    public  AuthController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        try {
            LoginResponseDTO response = loginService.login(request);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        } catch (Exception e) {
            e.printStackTrace(); // Or use a logger: log.error("Login error", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

//    Logout controller
@PostMapping("/logout")



public ResponseEntity<String> logout(@RequestHeader("Authorization") String authorizationHeader) {

    try{

        // Validate Authorization header
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return new ResponseEntity<>("Invalid or missing Authorization header", HttpStatus.BAD_REQUEST);
        }
        // No server-side action needed; client should discard token
        return new ResponseEntity<>("Logout successful", HttpStatus.OK);
    } catch (Exception e) {
        return new ResponseEntity<>("Server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

    class ErrorResponse {
        private final String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

    }

}
