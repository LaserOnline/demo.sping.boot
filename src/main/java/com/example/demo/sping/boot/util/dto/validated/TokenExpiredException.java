package com.example.demo.sping.boot.util.dto.validated;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String message) {
        super(message);
    }
}
