package com.example.demo.sping.boot.util.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Schema(description = "ResetPassword")
@Data
public class PasswordDTO {
    @Schema(description = "password", example = "!@LaserOnline1988")
    @Pattern(
    regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}\\[\\]:\";'<>?,./]).{9,30}$",
    message = "Password must be 9-30 characters, contain uppercase, lowercase, number, and special character"   
    )   
    private String password;
}
