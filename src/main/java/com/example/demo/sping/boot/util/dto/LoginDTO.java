package com.example.demo.sping.boot.util.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Schema(description = "login")
@Data
public class LoginDTO {
    @Schema(description = "username", example = "username")
    @NotEmpty(message = "username is empty")
    private String username;

    @Schema(description = "password", example = "password")
    @NotEmpty(message = "password is empty")
    private String password;
}
