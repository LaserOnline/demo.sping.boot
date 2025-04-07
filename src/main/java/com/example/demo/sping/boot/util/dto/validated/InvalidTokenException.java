package com.example.demo.sping.boot.util.dto.validated;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
}