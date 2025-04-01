package com.example.demo.sping.boot.service.auth;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.demo.sping.boot.util.encrypt.EncryptionUtil;

import io.jsonwebtoken.Claims;

@Service
public class JwtService {
    private final JwtTokenBuilder tokenBuilder;
    private final JwtTokenParser tokenParser;

    private final long accessTokenExpirationMs = 1000 * 60 * 15;
    private final long refreshTokenExpirationMs = 1000 * 60 * 60 * 24 * 7;

    public JwtService(JwtUtil jwtUtil) {
        this.tokenBuilder = new JwtTokenBuilder(jwtUtil.getJwtSecret());
        this.tokenParser = new JwtTokenParser(jwtUtil.getJwtSecret());
    }

    public String generateAccessToken(String input) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("token_type", "access");

        JwtPayload payload = new JwtPayload(EncryptionUtil.encrypt(input), new Date(), new Date(System.currentTimeMillis() + accessTokenExpirationMs), claims);
        return tokenBuilder.build(payload);
    }

    public String generateRefreshToken(String input) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("token_type", "refresh");
        JwtPayload payload = new JwtPayload(EncryptionUtil.encrypt(input), new Date(), new Date(System.currentTimeMillis() + refreshTokenExpirationMs), claims);
        return tokenBuilder.build(payload);
    }

    public String getUsernameFromToken(String token) {
        JwtPayload payload = tokenParser.parse(token);
        return EncryptionUtil.decrypt(payload.getSubject());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        Claims claims = tokenParser.getClaims(token);

        if (!"access".equals(claims.get("token_type", String.class))) return false;

        String username = EncryptionUtil.decrypt(claims.getSubject());
        return username.equals(userDetails.getUsername()) &&
               !claims.getExpiration().before(new Date());
    }
}
