package com.example.demo.sping.boot.service.auth;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.demo.sping.boot.config.Config;

@Service
public class AuthService {

    private final Config config;
    private final JwtTokenFactory jwtTokenFactory;

    public AuthService(Config config, JwtTokenFactory jwtTokenFactory) {
        this.config = config;
        this.jwtTokenFactory = jwtTokenFactory;
    }

    public String generateAccessToken(String usersUuid, List<String> roles) {
        long expiration = config.getAccessTokenExpireMs();
        return jwtTokenFactory.createToken(usersUuid, expiration, Map.of(
                "type", "access",
                "roles", roles,
                "iss", "auth-service"
        ));
    }

    public String generateRefreshToken(String usersUuid,String jti) {
        long expiration = config.getRefreshTokenExpireMs();
        return jwtTokenFactory.createToken(usersUuid, expiration, Map.of(
                "type", "refresh",
                "iss", "auth-service",
                "jti", jti
        ));
    }
}