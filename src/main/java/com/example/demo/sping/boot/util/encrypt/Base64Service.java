package com.example.demo.sping.boot.util.encrypt;

import java.util.Base64;

import org.springframework.stereotype.Service;

@Service
public class Base64Service {
    public String base64Encode(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes());

    }

    public String base64Decode(String input) {
        byte[] decodedBytes = Base64.getDecoder().decode(input);
        return new String(decodedBytes);
    }
}
