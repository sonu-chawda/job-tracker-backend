package com.jobtracker.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    @Value("${jwt.secret}") private String secret;
    @Value("${jwt.expiration-ms}") private long expirationMs;

    private SecretKey key() { return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); }

    public String generateToken(String email, String role) {
        return Jwts.builder().subject(email).claim("role", role)
                .issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key()).compact();
    }

    // Token galat ya expired ho to JwtException throw hota hai
    public String extractEmail(String token) {
        return Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload().getSubject();
    }
}
