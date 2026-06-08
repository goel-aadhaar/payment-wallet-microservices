package com.payment_wallet.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

/**
 * Shared JWT helper used by the API gateway (token validation) and the user-service
 * (token issuance + validation). Previously duplicated in both services.
 */
public class JwtUtil {

    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtUtil(String secret, long expirationMs) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = expirationMs;
    }

    /** Parses and verifies the token, returning its claims. Throws if invalid/expired. */
    public Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /** Returns true if the token is valid and its subject matches the given username. */
    public boolean validateToken(String token, String username) {
        try {
            return extractUsername(token).equals(username);
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return validateToken(token).getSubject();
    }

    public String extractRole(String token) {
        return (String) validateToken(token).get("role");
    }

    public String generateToken(Map<String, Object> claims, String email) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expirationMs))
                .signWith(signingKey)
                .compact();
    }
}
