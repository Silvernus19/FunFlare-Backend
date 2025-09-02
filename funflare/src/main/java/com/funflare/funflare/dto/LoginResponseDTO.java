package com.funflare.funflare.dto;

public class LoginResponseDTO {
    private final String jwtToken;
    private final Long id;
    private final String username;
    private final String role;

    public LoginResponseDTO(String jwtToken, Long id, String username, String role) {
        if (jwtToken == null || id == null) {
            throw new IllegalArgumentException("jwtToken and id must not be null");
        }
        this.jwtToken = jwtToken;
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public String getJwtToken() { return jwtToken; }
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
}