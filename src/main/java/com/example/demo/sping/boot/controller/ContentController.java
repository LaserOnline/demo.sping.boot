package com.example.demo.sping.boot.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.sping.boot.service.content.ContentService;
import com.example.demo.sping.boot.util.dto.CreateContentDTO;
import com.example.demo.sping.boot.util.response.Message;

import jakarta.validation.Valid;



@RestController
@RequestMapping("/content")
public class ContentController {
    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }
    
    @PostMapping("/auth/create")
    public ResponseEntity<Object> create(@Valid @RequestBody CreateContentDTO dto) {
    List<String> uploadedFiles = new ArrayList<>();
        
    if (dto.getFiles() != null) {
        for (String base64 : dto.getFiles()) {
            if (base64 != null && !base64.isEmpty()) {
                try {
                    String savedFilename = contentService.uploadFile(base64);
                    uploadedFiles.add(savedFilename);
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body(new Message("ไฟล์ลำดับ " + (uploadedFiles.size() + 1) + ": " + e.getMessage()));
                }
            }
        }
    }
    return ResponseEntity.ok().body(new Message("อัปโหลดสำเร็จทั้งหมด: " + uploadedFiles));
    }
}
