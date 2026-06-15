package com.cafetron.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration}")
    private long expiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // ── Generate token ─────────────────────────────────────────────

    public String generateToken(UserPrincipal principal) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role",       principal.getRole());
        claims.put("userId",     principal.getId());
        claims.put("email",      principal.getUser().getEmail());
        claims.put("name",       principal.getUser().getName());
        claims.put("employeeId", principal.getUser().getEmployeeId());

        return Jwts.builder()
                .claims(claims)
                .subject(String.valueOf(principal.getId()))  // ← user.id as subject
                .issuedAt(new Date())
                .expiration(new Date(
                        System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    // ── Extract userId from token ───────────────────────────────────

    public String extractUserId(String token) {
        return extractAllClaims(token).getSubject();
    }

    // ── Validate token ──────────────────────────────────────────────

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userId = extractUserId(token);
        return userId.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    // ── Private helpers ─────────────────────────────────────────────

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }
}