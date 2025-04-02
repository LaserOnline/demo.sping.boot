package com.example.demo.sping.boot.util.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "ข้อมูลผู้ใช้ลงทะเบียน")
@Data
public class RegisterDTO {
    @Schema(description = "username", example = "LaserOnline")
    @NotEmpty(message = "username empty")
    @Size(min = 5, message = "username must be between 5 characters")
    @Size(max = 13, message = "username max 13 characters long")
    @Pattern(
    regexp = "^[a-zA-Z0-9]+$",
    message = "username must contain only English letters (a-z, A-Z) and numbers (0-9) without special characters"
    )
    private String username;

    @Schema(description = "email", example = "Email@email.com")
    @Email(message = "Email must be valid")
    @NotEmpty(message = "Email is empty")
    private String email;

    @Schema(description = "password", example = "!@LaserOnline1988")
    @Pattern(
    regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}\\[\\]:\";'<>?,./]).{9,30}$",
    message = "Password must be 9-30 characters, contain uppercase, lowercase, number, and special character"   
    )   
    @NotEmpty(message = "password is empty")
    private String password;

}
