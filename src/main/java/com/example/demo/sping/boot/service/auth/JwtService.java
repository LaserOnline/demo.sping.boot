package com.example.demo.sping.boot.service.auth;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    private final SecretKey key;
    private final long accessTokenExpirationMs = 1000 * 60 * 15; // 15 นาที
    private final long refreshTokenExpirationMs = 1000 * 60 * 60 * 24 * 7; // 7 วัน

    public JwtService(JwtUtil jwtUtil) {
        this.key = Keys.hmacShaKeyFor(jwtUtil.getJwtSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", encrypt(username));
        claims.put("token_type", "access");

        return buildToken(claims, accessTokenExpirationMs);
    }

    // 2️⃣ สร้าง Refresh Token
    public String generateRefreshToken(String username) {
        // token_type = refresh
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", encrypt(username));
        claims.put("token_type", "refresh");
    
        return buildToken(claims, refreshTokenExpirationMs);
    }

    private String buildToken(Map<String, Object> claims, long expirationMs) {
        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    private Claims getAllClaims(String token) {
        return Jwts.parser()
                   .verifyWith(key)
                   .build()
                   .parseSignedClaims(token)
                   .getPayload();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        Claims claims = getAllClaims(token);
    
        String tokenType = claims.get("token_type", String.class);
        if (!"access".equals(tokenType)) {
            return false; 
        }
    
        final String username = decrypt(claims.getSubject());
        if (!username.equals(userDetails.getUsername())) {
            return false;
        }
    
        return !isTokenExpired(token);
    }
    
    private boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    
    private Date getExpirationDateFromToken(String token) {
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        return claims.getExpiration();
    }


    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        String encryptedUsername = claims.getSubject(); // มาจาก field "sub"
        return decrypt(encryptedUsername);
    }

    private String encrypt(String input) {
        String reversed = new StringBuilder(input).reverse().toString();
        return Base64.getEncoder().encodeToString(reversed.getBytes(StandardCharsets.UTF_8));
    }

    private String decrypt(String encrypted) {
        byte[] decodedBytes = Base64.getDecoder().decode(encrypted);
        String reversed = new String(decodedBytes, StandardCharsets.UTF_8);
        return new StringBuilder(reversed).reverse().toString();
    }

    public String extractUsername(String jwt) {
        throw new UnsupportedOperationException("Unimplemented method 'extractUsername'");
    }
}
