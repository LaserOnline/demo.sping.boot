package com.example.demo.sping.boot.service.auth;

import java.util.Base64;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.example.demo.sping.boot.util.encrypt.AesUtils;

public class EncodeJwt {
    private static final String keyAES = "iGH5iClX3hhMvkyexZHKXw==";

    // สร้าง SecretKey จาก keyAES ตายตัว
    public static SecretKey getFixedKey() {
        byte[] decodedKey = Base64.getDecoder().decode(keyAES);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }


    private static String randomString(int length) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(alphabet.length());
            result.append(alphabet.charAt(index));
        }
        return result.toString();
    }
    
    public static String encodeJwtBase64(String input) {
        // กลับลำดับตัวอักษรใน input
        String reversedInput = new StringBuilder(input).reverse().toString();
        // สร้างรหัสสุ่มสำหรับส่วนต้น (r) และส่วนท้าย (l)
        String r = randomString(3);
        String l = randomString(5);
        // รวม r, reversedInput และ l เข้าด้วยกัน
        String combined = r + reversedInput + l;
        // เข้ารหัสเป็น Base64
        return Base64.getEncoder().encodeToString(combined.getBytes());
    }

    public static String decodeJwtBase64(String input) {
        // 1. แปลงข้อมูลจาก Base64 กลับเป็นข้อความเดิม
        String decoded = new String(Base64.getDecoder().decode(input));
        
        // 2. ตัดตัวอักษรส่วนต้น (หน้า 3 ตัว) และส่วนท้าย (หลัง 5 ตัว)
        if (decoded.length() > 8) { // ตรวจสอบว่าข้อมูลที่แปลงกลับมายาวเพียงพอ
            decoded = decoded.substring(3, decoded.length() - 5);
        }
        
        // 3. สลับตัวอักษรจากหน้าเรียงหลัง (กลับลำดับ)
        String reversed = new StringBuilder(decoded).reverse().toString();
        
        // 4. คืนค่าข้อความที่ได้หลังจากการแปลง
        return reversed;
    }

    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        return keyGen.generateKey();
    }

    // ฟังก์ชันสำหรับแปลง SecretKey เป็น String
    public static String keyToString(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    // ฟังก์ชันเข้ารหัส AES พร้อม IV
    public static String encryptAES(String input) throws Exception {
        SecretKey secretKey = getFixedKey();
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        // สร้าง IV ใหม่ทุกครั้งที่ทำการเข้ารหัส
        IvParameterSpec iv = AesUtils.generateIv();
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

        byte[] encryptedBytes = cipher.doFinal(input.getBytes());

        // รวม IV กับข้อมูลที่เข้ารหัส (เพื่อใช้ในภายหลังสำหรับการถอดรหัส)
        byte[] combined = new byte[iv.getIV().length + encryptedBytes.length];
        System.arraycopy(iv.getIV(), 0, combined, 0, iv.getIV().length);
        System.arraycopy(encryptedBytes, 0, combined, iv.getIV().length, encryptedBytes.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    // ฟังก์ชันถอดรหัส AES พร้อม IV
    public static String decryptAES(String encryptedInput) throws Exception {
        SecretKey secretKey = getFixedKey();
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        // แยก IV และข้อมูลที่เข้ารหัสออกจากกัน
        byte[] combinedBytes = Base64.getDecoder().decode(encryptedInput);
        byte[] ivBytes = new byte[16];
        System.arraycopy(combinedBytes, 0, ivBytes, 0, 16);
        byte[] encryptedBytes = new byte[combinedBytes.length - 16];
        System.arraycopy(combinedBytes, 16, encryptedBytes, 0, encryptedBytes.length);

        // ตั้งค่า IV ก่อนถอดรหัส
        IvParameterSpec iv = new IvParameterSpec(ivBytes);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

        byte[] originalBytes = cipher.doFinal(encryptedBytes);
        return new String(originalBytes);
    }
}
