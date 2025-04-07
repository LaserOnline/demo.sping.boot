package com.example.demo.sping.boot.service.auth;

import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;

@Component
public class JwtTokenFactory {

    private final SecretKey secretKey;

    public JwtTokenFactory(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    public String createToken(String subject, long expirationMillis, Map<String, Object> claims) {
        long now = System.currentTimeMillis();

        JwtBuilder builder = Jwts.builder()
                .subject(subject)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expirationMillis));

        if (claims != null) {
            claims.forEach(builder::claim);
        }

        return builder
                .signWith(secretKey)
                .compact();
    }
}