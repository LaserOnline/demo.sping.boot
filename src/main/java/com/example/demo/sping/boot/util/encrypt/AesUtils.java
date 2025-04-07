package com.example.demo.sping.boot.util.encrypt;

import java.security.SecureRandom;

import javax.crypto.spec.IvParameterSpec;

public class AesUtils {
public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16]; // ขนาด IV สำหรับ AES คือ 16 ไบต์
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv); // สุ่มค่าลงใน iv
        return new IvParameterSpec(iv);
    }
}
