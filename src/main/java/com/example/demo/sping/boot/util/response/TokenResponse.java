package com.example.demo.sping.boot.util.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class TokenResponse {
    @Schema(description = "token", example = "token")
    private String token;

    private long iat;

    private long exp;

    public TokenResponse(String token,long iat, long exp) {
        this.token = token;
        this.iat = iat;
        this.exp = exp;
    }
}
