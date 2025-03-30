package com.example.demo.sping.boot.service.uuid;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class UuidService {
    public String generateUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
