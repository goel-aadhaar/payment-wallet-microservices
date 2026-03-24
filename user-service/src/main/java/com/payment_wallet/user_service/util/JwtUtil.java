package com.payment_wallet.user_service.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class JwtUtil {

    private static final String SECRET = "secret123secret123secret123secret123secret123secret123";

    private Key getSigninKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String extractEmail(String token) {
        return Jwts.parser()
                .setSigningKey(getSigninKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token, String username) {
        try {
            extractEmail(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(getSigninKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String generateToken(Map<String, Object> claims, String email) {
        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(getSigninKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractRole(String token) {
        return (String) Jwts.parser()
                .setSigningKey(getSigninKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role");
    }
}