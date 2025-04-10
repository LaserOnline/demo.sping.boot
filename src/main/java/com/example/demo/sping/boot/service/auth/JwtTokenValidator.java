package com.example.demo.sping.boot.service.auth;

import java.io.IOException;
import java.time.LocalTime;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.JWTClaimsSet;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;


@Component
public class JwtTokenValidator {

    private final JwtParser parser;

    public JwtTokenValidator(SecretKey secretKey) {
        this.parser = Jwts.parser()
                .verifyWith(secretKey)
                .build();
    }

    public Claims parseToken(String token) throws ExpiredJwtException, JwtException {
        return parser.parseSignedClaims(token).getPayload();
    }

    public boolean isAccessToken(Claims claims) {
        return "access".equals(claims.get("type", String.class));
    }

    public boolean isRefreshToken(Claims claims) {
        return "refresh".equals(claims.get("type", String.class));
    }

    public static boolean isTokenExpired(Claims claims) {
        Date expiration = claims.getExpiration();
        return expiration.before(new Date());
    }

    public static long getRemainingTime(Claims claims) {
        Date expiration = claims.getExpiration();
        long nowMillis = System.currentTimeMillis();
        long expMillis = expiration.getTime();
        long remainingMillis = expMillis - nowMillis;
        return remainingMillis > 0 ? remainingMillis / 1000 : 0;
    }

    // แยกส่วน ของ ประเภท toke type
    public String extractTokenType(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid JWT token format");
        }

        byte[] decodedPayload = Base64.getUrlDecoder().decode(parts[1]);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> payload = objectMapper.readValue(decodedPayload, Map.class);
            return (String) payload.get("type");
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to parse JWT payload", e);
        }
    }

    public static Claims convertToClaims(JWTClaimsSet jwtClaimsSet) {
        Map<String, Object> map = new java.util.LinkedHashMap<>();
        
        map.put("sub", jwtClaimsSet.getSubject());
        map.put("iss", jwtClaimsSet.getIssuer());
        map.put("iat", jwtClaimsSet.getIssueTime());
        map.put("exp", jwtClaimsSet.getExpirationTime());
        map.put("nbf", jwtClaimsSet.getNotBeforeTime());
        map.put("jti", jwtClaimsSet.getJWTID());

        jwtClaimsSet.getClaims().forEach((k, v) -> {
            if (!List.of("sub", "iss", "iat", "exp", "nbf", "jti").contains(k)) {
                map.put(k, v);
            }
        });

        // ใช้ claims จาก Jwts เพื่อ wrap
        return Jwts.claims().add(map).build();
    }

    public static boolean isWithinAllowedWindow() {
        LocalTime now = LocalTime.now();

        List<TimeWindow> allowedWindows = List.of(
            new TimeWindow(LocalTime.of(9, 0), LocalTime.of(12, 0)),
            new TimeWindow(LocalTime.of(15, 0), LocalTime.of(16, 0)),
            new TimeWindow(LocalTime.of(20, 0), LocalTime.of(22, 0))
        );

        return allowedWindows.stream().anyMatch(window -> window.isWithin(now));
    }

    private static class TimeWindow {
        private final LocalTime start;
        private final LocalTime end;

        public TimeWindow(LocalTime start, LocalTime end) {
            this.start = start;
            this.end = end;
        }

        public boolean isWithin(LocalTime time) {
            return !time.isBefore(start) && !time.isAfter(end);
        }
    }
}