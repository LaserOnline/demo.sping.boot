package com.example.demo.sping.boot.service.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.sping.boot.config.Config;
import com.example.demo.sping.boot.service.uuid.UuidService;

@Service
public class LocalFileStorageService implements  FileStorageService {

    private final UuidService uuidService;
    private final String uploadDir;

    @Autowired
    public LocalFileStorageService(Config config, UuidService uuidService) {
        this.uploadDir = Paths.get("").toAbsolutePath().resolve(config.getNginxUploadPath()).toString();
        this.uuidService = uuidService;
    }

    @Override
    public String save(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Empty file");
        }
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String newFilename = uuidService.generateUuidWithTimestamp();

        String originalFilename = file.getOriginalFilename();
        String extension = "";

        @SuppressWarnings("null")
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex);
        }

        // รวมชื่อไฟล์ใหม่กับนามสกุลเดิม
        String filename = newFilename + extension;

        Path filePath = uploadPath.resolve(filename);
        file.transferTo(filePath.toFile());
     
        return filename;
    }
}
