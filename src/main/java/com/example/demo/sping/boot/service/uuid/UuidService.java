package com.example.demo.sping.boot.service.uuid;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class UuidService {
    public String generateUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public String generateUuidWithTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                                                       .withZone(ZoneId.systemDefault());
        String timestamp = formatter.format(Instant.now());

        return timestamp + "_" + UUID.randomUUID().toString().replace("-", "");
    }
}
