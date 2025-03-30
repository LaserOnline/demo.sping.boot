package com.example.demo.sping.boot.util.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class JwtResponse {
    public JwtResponse(String accessToken,String refreshToken ) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    @Schema(description = "accessToken", example = "AccessToken")
    private String accessToken;

    @Schema(description = "refreshToken", example = "RefreshToken")
    private String refreshToken;
}
