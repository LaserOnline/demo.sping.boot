package com.example.demo.sping.boot.service.auth;

import java.util.Date;
import java.util.Map;

import lombok.Data;


@Data
public class JwtPayload {
    private String subject;
    private Date issuedAt;
    private Date expiration;
    private Map<String, Object> additionalClaims;

    public JwtPayload(
        String subject,
        Date issuedAt,
        Date expiration,
        Map<String, Object> additionalClaims
    ) {
        this.subject = subject;
        this.issuedAt = issuedAt;
        this.expiration = expiration;
        this.additionalClaims = additionalClaims;
    }
}
