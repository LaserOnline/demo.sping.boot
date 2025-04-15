package com.example.demo.sping.boot.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.sping.boot.service.auth.PayloadData;
import com.example.demo.sping.boot.service.content.ContentService;
import com.example.demo.sping.boot.service.uuid.UuidService;
import com.example.demo.sping.boot.util.dto.CreateContentDTO;
import com.example.demo.sping.boot.util.response.ContentResponse;
import com.example.demo.sping.boot.util.response.Message;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;




@RestController
@RequestMapping("/content")
public class ContentController {
    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }
    
    @PostMapping("/auth/create")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Object> create(@Valid @RequestBody CreateContentDTO dto, @AuthenticationPrincipal PayloadData payloadData) {
        List<String> images = new ArrayList<>();

        if (dto.getFiles() != null && !dto.getFiles().isEmpty()) {
            try {
                images = contentService.uploadFiles(dto.getFiles());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(new Message(e.getMessage()));
            }
        }

        String uuid = UuidService.generateUuidWithTimestamp();
        ContentResponse content = new ContentResponse(payloadData.getUsername(),uuid,dto.getName(),dto.getMessage(),images);
        contentService.createContent(content);

        return ResponseEntity.ok().body(new Message("create successfully"));
    }

    @GetMapping("/fetch/all")
    public ResponseEntity<Message> getMethodName() {
        return ResponseEntity.ok().body(new Message("Testing"));
    }
    
}
