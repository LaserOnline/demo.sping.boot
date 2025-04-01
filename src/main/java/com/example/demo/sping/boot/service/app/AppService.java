package com.example.demo.sping.boot.service.app;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AppService {
    private final FileStorageService fileStorageService;

    public AppService(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    public String saveFile(MultipartFile file) throws IOException {
        return fileStorageService.save(file);
    }
}
