package com.example.demo.sping.boot.util.dto.validated;

import org.springframework.security.core.AuthenticationException;

public class TokenExpiredAuthenticationException extends AuthenticationException {
    public TokenExpiredAuthenticationException(String msg) {
        super(msg);
    }
}