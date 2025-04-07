package com.example.demo.sping.boot.service.jwt;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import com.example.demo.sping.boot.service.auth.EncodeJwt;

public class EncodeJwtTest {

    @Test
    void testScramble() {
        String input = "aad28fc2cc01401098eb3520909ef114";
        String result = EncodeJwt.encodeJwtBase64(input);
        System.err.println("result: " + result);
    }

    @Test
    void testDecodeBase64() {
        String input = "SHBJNDExZmU5MDkwMjUzYmU4OTAxMDQxMGNjMmNmODJkYWFBd3p5Qg==";
        String result = EncodeJwt.decodeJwtBase64(input);
        System.err.println("result: " + result);
    }

    @Test
    void testGenerateKey() throws Exception {
        // เรียก generateKey() เพื่อสร้างคีย์
        SecretKey key = EncodeJwt.generateKey();

        // แปลงคีย์เป็น String (เพื่อตรวจสอบง่ายขึ้น)
        String keyString = EncodeJwt.keyToString(key);

        // ตรวจสอบว่าคีย์ที่ได้ไม่ใช่ null
        assertNotNull(key);
        System.err.println("Generated key: " + keyString);
    }

    @Test
    void testEncryptAndDecryptAES() throws Exception {
        // Arrange
        String input = "myTestData";

        // Act
        String encrypted = EncodeJwt.encryptAES(input);
        String decrypted = EncodeJwt.decryptAES(encrypted);

        // Assert
        assertEquals(input, decrypted, "Decrypted data should match original input");

        // Optionally print out the encrypted result (for debug purposes)
        System.out.println("Encrypted: " + encrypted);
        System.out.println("Decrypted: " + decrypted);

    }
}