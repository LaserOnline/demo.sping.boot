package com.example.demo.sping.boot.util.encrypt;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class EncryptionUtil {
    // เข้ารหัา
    public static String encrypt(String input) {
        String reversed = new StringBuilder(input).reverse().toString();
        return Base64.getEncoder().encodeToString(reversed.getBytes(StandardCharsets.UTF_8));
    }
    // ถอดรหัส
    public static String decrypt(String encrypted) {
        byte[] decodedBytes = Base64.getDecoder().decode(encrypted);
        String reversed = new String(decodedBytes, StandardCharsets.UTF_8);
        return new StringBuilder(reversed).reverse().toString();
    }
}
