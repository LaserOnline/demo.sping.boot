package com.example.demo.sping.boot.service.csv;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;

@Service
public class CsvService {
public ByteArrayInputStream generateCsv() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Name,Email\n");
        sb.append("1,John Doe,john@example.com\n");
        sb.append("2,Jane Smith,jane@example.com\n");

        return new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8));
    }
}
