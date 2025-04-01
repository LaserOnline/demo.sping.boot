package com.example.demo.sping.boot.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.sping.boot.service.app.AppService;
import com.example.demo.sping.boot.util.response.Message;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;




@RestController
@Tag(name = "App Controller", description = "test api")
@RequestMapping("/")
public class AppController {
    private final AppService appService;

    public AppController(AppService appService) {
        this.appService = appService;
    }

    @Operation(summary = "Test Response Api")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "App Test Api",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"message\": \"Hello Spring Boot\"}")))
    })
    @GetMapping("/hello")
    public ResponseEntity<Message> sayHello() {
        return ResponseEntity.ok().body(new Message("Hello Spring Boot"));
    }

    @Operation(
    summary = "Say hello via query param",
    description = "ส่งชื่อผ่าน query string (เช่น ?name=John) แล้วระบบจะทักกลับ"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Response OK",
            content = @io.swagger.v3.oas.annotations.media.Content(
                examples = @ExampleObject(value = "{\"message\": \"Hello John\"}")
            )
        )
    })
    @PostMapping("/hello")
    public ResponseEntity<Message> getMessage(
        @Parameter(
            description = "ชื่อของผู้ใช้",
            example = "John"
        )
        @RequestParam(name = "name", required = false) String name
    ) {
        String data = name;

        if (name == null || name.trim().isEmpty()) {
            data = "data is empty";
        }

        return ResponseEntity.ok().body(new Message(data));
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    @Operation(
        summary = "upload file",
        description = "อัปโหลดไฟล์ภาพไปยังเซิร์ฟเวอร์"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "อัปโหลดสำเร็จ",
            content = @Content(
                examples = @ExampleObject(value = "{\"message\": \"File uploaded successfully\"}")
            )
        ),
        @ApiResponse(responseCode = "400", description = "อัปโหลดล้มเหลว")
    })
    public ResponseEntity<Message> uploadFile(
        @Parameter(
            description = "เลือกไฟล์ภาพเพื่ออัปโหลด",
            content = @Content(mediaType = "multipart/form-data")
        )
        @RequestParam("file") MultipartFile file
    ) {
        try {
            appService.saveFile(file);
            return ResponseEntity.ok().body(new Message("File uploaded successfully"));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(new Message("Failed to upload file: " + e.getMessage()));
        }
    }
    
}
