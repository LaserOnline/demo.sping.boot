package com.example.demo.sping.boot.util.dto;

import com.example.demo.sping.boot.util.dto.validated.ValidBase64;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Schema(description = "upload file base64")
@Data
public class Base64DTO {
    @Schema(description = "file base64", example = "aGVsbG93b3JsZA==")
    @NotEmpty(message = "input is empty")
    @ValidBase64
    private String base64;
}
