package com.example.demo.sping.boot.service.auth;

import java.util.Map;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtTokenBuilder {
    private final SecretKey key;

    public JwtTokenBuilder(String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String build(JwtPayload payload) {
        Map<String, Object> claims = payload.getAdditionalClaims();
        claims.put("sub", payload.getSubject());

        return Jwts.builder()
                .claims(claims)
                .issuedAt(payload.getIssuedAt())
                .expiration(payload.getExpiration())
                .signWith(key)
                .compact();
    }
}
