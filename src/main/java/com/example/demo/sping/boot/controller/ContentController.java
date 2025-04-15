package com.example.demo.sping.boot.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.sping.boot.service.auth.PayloadData;
import com.example.demo.sping.boot.service.content.ContentService;
import com.example.demo.sping.boot.service.users.UsersService;
import com.example.demo.sping.boot.service.uuid.UuidService;
import com.example.demo.sping.boot.util.dto.CreateContentDTO;
import com.example.demo.sping.boot.util.dto.UpdateContentDTO;
import com.example.demo.sping.boot.util.response.ContentData;
import com.example.demo.sping.boot.util.response.CreateContentItem;
import com.example.demo.sping.boot.util.response.Message;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/content")
@Tag(name = "Content Controller", description = "create get put delete")
public class ContentController {
    private final ContentService contentService;
    private final UsersService usersService;

    public ContentController(ContentService contentService,UsersService usersService) {
        this.contentService = contentService;
        this.usersService = usersService;
    }
    
    @PostMapping("/auth/create")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Object> create(@Valid @RequestBody CreateContentDTO dto, @AuthenticationPrincipal PayloadData payloadData) {

        // step 1
        boolean userExists = usersService.findByUsersUuid(payloadData.getUsername()).isPresent();
        if (!userExists) {
            return ResponseEntity.status(404).body(new Message("users not found"));
        }
        // step 1

        // step 2
        List<String> images = new ArrayList<>();

        if (dto.getFiles() != null && !dto.getFiles().isEmpty()) {
            try {
                images = contentService.uploadFiles(dto.getFiles());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(new Message(e.getMessage()));
            }
        }
        // step 2

        // step 3
        String uuid = UuidService.generateUuidWithTimestamp();
        CreateContentItem content = new CreateContentItem(payloadData.getUsername(),uuid,dto.getName(),dto.getMessage(),images);
        contentService.createContent(content);
        // step 3

        return ResponseEntity.ok().body(new Message("create successfully"));
    }

    @GetMapping("/fetch/{uuid}")
    @Operation(summary = "ค้นหา Content ด้วย UUID", description = "ใช้ UUID เพื่อดึงข้อมูล content")
    public ResponseEntity<Object> findContent(
        @Parameter(description = "UUID ของ content ที่ต้องการค้นหา", example = "20250414113022_abc123xyz")
        @PathVariable("uuid") String uuid
        ) {
        // step 1
        boolean exists = contentService.findContentUuid(uuid);
        if (exists) {
            // step 2
            ContentData data = contentService.getContentDataUuid(uuid);
            return ResponseEntity.ok().body(data);
            // step 2
        } else {
            return ResponseEntity.status(404).body(new Message("not found"));
        }
        // step 1
    }

    @GetMapping("/fetch/")
    @Operation(summary = "ดึงข้อมูล Content ทั้งหมด")
    public ResponseEntity<List<ContentData>> getAll() {
        List<ContentData> allContent = contentService.fetchAll();
        return ResponseEntity.ok().body(allContent);
    }

    @PutMapping("auth/update")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Object> update(@Valid @RequestBody UpdateContentDTO dto,
                                        @AuthenticationPrincipal PayloadData payloadData) {
        boolean userExists = usersService.findByUsersUuid(payloadData.getUsername()).isPresent();
        if (!userExists) {
            return ResponseEntity.status(404).body(new Message("users not found"));
        }

        try {
            contentService.updateContent(dto, payloadData.getUsername());
            return ResponseEntity.ok().body(new Message("update success"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new Message(e.getMessage()));
        }
    }
    
    @DeleteMapping("/auth/{uuid}")
    @Operation(summary = "delete content")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Object> remove(
        @PathVariable("uuid") String uuid,
        @AuthenticationPrincipal PayloadData payloadData) {
        boolean userExists = usersService.findByUsersUuid(payloadData.getUsername()).isPresent();
        if (!userExists) {
            return ResponseEntity.status(404).body(new Message("users not found"));
        }
        try {
            contentService.deleteContent(uuid, payloadData.getUsername());
            return ResponseEntity.ok().body(new Message("successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new Message(e.getMessage()));
        }
    }
}
