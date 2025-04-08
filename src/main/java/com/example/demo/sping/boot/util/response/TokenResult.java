package com.example.demo.sping.boot.util.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResult {
    private String token;
    private long iat;
    private long exp;
}
