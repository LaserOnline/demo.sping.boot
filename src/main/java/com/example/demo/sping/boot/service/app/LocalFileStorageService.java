package com.example.demo.sping.boot.service.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LocalFileStorageService implements  FileStorageService {
    private static final String UPLOAD_DIR = Paths.get("").toAbsolutePath().toString() + "/upload";

    @Override
    public String save(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Empty file");
        }
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(filename);
        file.transferTo(filePath.toFile());
        return filename;
    }
}
