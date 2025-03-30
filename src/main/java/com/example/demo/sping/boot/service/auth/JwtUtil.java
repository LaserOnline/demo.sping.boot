package com.example.demo.sping.boot.service.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class JwtUtil {

    private final String jwtSecret;

    public JwtUtil(@Value("${jwt.secret}") String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

}