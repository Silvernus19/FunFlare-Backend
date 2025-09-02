package com.funflare.funflare.service;


import com.funflare.funflare.dto.LoginRequestDTO;
import com.funflare.funflare.dto.LoginResponseDTO;
import com.funflare.funflare.model.User;
import com.funflare.funflare.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;

@Service
public class LoginService {

    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String jwtSecret;
    private final Long jwtExpirationms;

    @Autowired
    public LoginService(UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        @Value("${jwt.secret}") String jwtSecret,
                        @Value("${jwt.expiration-ms}") Long jwtExpirationms) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtSecret = jwtSecret;
        this.jwtExpirationms = jwtExpirationms;


    }

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO request) {
//        find user by email or Username
        User user = userRepository.findByEmail(request.getEmail())
                .or(() -> userRepository.findByUsername(request.getEmail()))
                .orElseThrow(() -> new BadCredentialsException("invalid credentials"));

//        verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("invalid credentials");
        }

//        generate jwt token
        String token = Jwts.builder()
        .setSubject(user.getEmail())
                .claim("userId", user.getId())
                .claim("role", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(Instant.now().toEpochMilli() + jwtExpirationms))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();

        // Update last login (optional)
//        user.setLastLogin(OffsetDateTime.from(Instant.now()));
//        userRepository.save(user);

//        updated last login
        user.setLastLogin(OffsetDateTime.now(ZoneOffset.UTC)); // Fixed
        userRepository.save(user);

//        return statement
        return new LoginResponseDTO(token, user.getId(), user.getUsername(), user.getRole().name());







    }




}
