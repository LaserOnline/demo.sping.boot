package com.example.demo.sping.boot.service.auth;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtTokenParser {
    private final SecretKey key;

    public JwtTokenParser(String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public JwtPayload parse(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String subject = claims.getSubject();
        Date issuedAt = claims.getIssuedAt();
        Date expiration = claims.getExpiration();

        Map<String, Object> additionalClaims = new HashMap<>(claims);
        additionalClaims.remove("sub");
        additionalClaims.remove("iat");
        additionalClaims.remove("exp");

        return new JwtPayload(subject, issuedAt, expiration, additionalClaims);
    }
    
    public Claims getClaims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
