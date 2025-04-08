package com.example.demo.sping.boot.util.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class JwtResponse {
    public JwtResponse(String accessToken,String refreshToken,long iat,long exp ) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.iat = iat;
        this.exp = exp;
    }

    @Schema(description = "accessToken", example = "accessToken")
    private String accessToken;

    @Schema(description = "refreshToken", example = "refreshToken")
    private String refreshToken;

    private long iat;

    private long exp;
}
