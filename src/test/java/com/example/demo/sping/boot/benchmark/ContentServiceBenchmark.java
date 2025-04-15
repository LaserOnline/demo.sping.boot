package com.example.demo.sping.boot.benchmark;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.demo.sping.boot.config.Config;
import com.example.demo.sping.boot.service.content.ContentService;

public class ContentServiceBenchmark {
    @Test
    void benchmarkUploadFiles() throws Exception {
        // เตรียม Service
        Config config = new Config();
        config.getNginxUploadPath(); // เปลี่ยนตาม path จริงของคุณ
        ContentService contentService = new ContentService(config);

        Path imagePath = Path.of("src/test/java/com/example/demo/sping/boot/images/testing_scripted.jpg");
        if (!Files.exists(imagePath)) {
            throw new IllegalArgumentException("not found path: " + imagePath.toAbsolutePath());
        }
        byte[] fileBytes = Files.readAllBytes(imagePath);
        String base64 = Base64.getEncoder().encodeToString(fileBytes);

        List<String> base64List = List.of(base64);

        // เริ่มจับเวลา
        long start = System.nanoTime();

        System.out.println("🔤 base64 : " + base64.substring(0, 100) + "...");

        // เรียกฟังชันที่ต้องการวัดเวลา
        try {
            contentService.uploadFiles(base64List);
        } catch (Exception e) {
            System.err.println("❌ uploadFiles ล้มเหลว: " + e.getMessage());
            throw e;
        }

        // จบการจับเวลา
        long end = System.nanoTime();
        double durationMillis = (end - start) / 1_000_000.0;

        System.out.printf("⏱️ uploadFiles use time: %.2f ms%n", durationMillis);
    }
}
