package com.funflare.funflare.controller;


import com.funflare.funflare.dto.LoginRequestDTO;
import com.funflare.funflare.dto.LoginResponseDTO;
import com.funflare.funflare.model.User;
import com.funflare.funflare.repository.UserRepository;
import com.funflare.funflare.service.LoginService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
        @RequestMapping("api/auth")
public class LoginController {

    private LoginService loginService;

    public LoginController(LoginService loginService) {
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
}
