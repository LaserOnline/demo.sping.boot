package com.example.demo.sping.boot.service.auth;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.demo.sping.boot.config.Config;
import com.example.demo.sping.boot.util.dto.validated.InvalidTokenTypeException;
import com.example.demo.sping.boot.util.dto.validated.TokenExpiredException;
import com.example.demo.sping.boot.util.encrypt.EncryptionUtil;

import io.jsonwebtoken.Claims;

@Service
public class JwtService {
    private final JwtTokenBuilder tokenBuilder;
    private final JwtTokenParser tokenParser;

    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;

    public JwtService(JwtUtil jwtUtil,Config config) {
        this.tokenBuilder = new JwtTokenBuilder(jwtUtil.getJwtSecret());
        this.tokenParser = new JwtTokenParser(jwtUtil.getJwtSecret());

        this.accessTokenExpirationMs = config.getAccessTokenExpireMs();
        this.refreshTokenExpirationMs = config.getRefreshTokenExpireMs();
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

        String tokenType = claims.get("token_type", String.class);
        if (!"access".equals(tokenType)) {
            throw new InvalidTokenTypeException("Not an access token");
        }

        String username = EncryptionUtil.decrypt(claims.getSubject());

        if (!username.equals(userDetails.getUsername())) {
            return false;
        }

        if (claims.getExpiration().before(new Date())) {
            throw new TokenExpiredException("Access token expired");
        }

        return true;
    }
}
