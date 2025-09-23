package com.funflare.funflare.component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private final String jwtSecret;


    public JwtUtil( @Value("${jwt.secret}") String jwtSecret) {

        this.jwtSecret = jwtSecret;
    }

    public String extractUserId(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        try {
            Claims claims = Jwts.parser()

                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();
            String userId = claims.get("userId", String.class);
            if (userId == null) {
                throw new IllegalArgumentException("User ID not found in JWT");
            }
            return userId;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token: " + e.getMessage());
        }
    }
}