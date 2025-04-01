package com.example.demo.sping.boot.service.app;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface FileStorageService {
    String save(MultipartFile file) throws IOException;
}
