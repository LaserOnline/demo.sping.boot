package com.example.demo.sping.boot.util.dto;


import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class CreateContentDTO {

    @NotEmpty(message = "name empty")
    @Size(max = 50, message = "name max 50 characters long")
    @Schema(description = "ชื่อเนื้อหา", example = "ทดสอบการอัปโหลดไฟล์")
    private String name;

    @Size(max = 240, message = "message max 240 characters long")
    @Schema(description = "ข้อความเพิ่มเติม (สามารถว่างได้)", example = "Hello Swagger!")
    private String message;

    @Schema(description = "รายการไฟล์ภาพในรูปแบบ Base64 (สามารถว่างได้)", example = "[\"FileBase64-1\", \"FileBase64-2\"]")
    private List<String> files;
}
