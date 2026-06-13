package com.ediae.ecotrack_office.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HexFormat;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    // Convierte el secret del yaml en una clave criptográfica
    private SecretKey getSigningKey() {
        byte[] keyBytes = HexFormat.of().parseHex(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // ─────────────────────────────────────────────
    // Genera un token JWT con el id y el rol del usuario
    // ─────────────────────────────────────────────
    public String generateToken(Long userId, String role) {
        return Jwts.builder()
                .subject(String.valueOf(userId))   // quien es el usuario
                .claim("role", role)               // su rol
                .issuedAt(new Date())              // cuando se generó
                .expiration(new Date(System.currentTimeMillis() + expiration)) // cuando expira
                .signWith(getSigningKey())         // firmamos con nuestra clave secreta
                .compact();
    }

    // ─────────────────────────────────────────────
    // Extrae el id del usuario del token
    // ─────────────────────────────────────────────
    public Long extractUserId(String token) {
        return Long.parseLong(extractClaims(token).getSubject());
    }

    // ─────────────────────────────────────────────
    // Extrae el rol del usuario del token
    // ─────────────────────────────────────────────
    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    // ─────────────────────────────────────────────
    // Verifica que el token es válido y no ha expirado
    // ─────────────────────────────────────────────
    public boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ─────────────────────────────────────────────
    // Extrae todos los datos (claims) del token
    // ─────────────────────────────────────────────
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}