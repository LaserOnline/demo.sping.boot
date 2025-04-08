package com.example.demo.sping.boot.service.auth;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.demo.sping.boot.config.Config;
import com.example.demo.sping.boot.util.response.TokenResult;

@Service
public class AuthService {

    private final Config config;
    private final JwtTokenFactory jwtTokenFactory;    
    private final KeyPair rsaKeyPair;
    private final EncodeJwt encodeJwt;


    public AuthService(Config config, JwtTokenFactory jwtTokenFactory,KeyPair rsaKeyPair,EncodeJwt encodeJwt) {
        this.config = config;
        this.jwtTokenFactory = jwtTokenFactory;
        this.rsaKeyPair = rsaKeyPair;
        this.encodeJwt = encodeJwt;
    }

    public TokenResult generateAccessToken(String usersUuid, List<String> roles) {
        long now = System.currentTimeMillis();
        long expiration = config.getAccessTokenExpireMs();
        long exp = now + expiration;

        String token = jwtTokenFactory.createToken(usersUuid, expiration, Map.of(
                "type", "access",
                "roles", roles,
                "iss", "auth-service"
        ));
        return new TokenResult(token, now / 1000, exp / 1000);
    }

    public TokenResult generateRefreshToken(String userId, String jti) {
        long now = System.currentTimeMillis();
        long expiration = config.getRefreshTokenExpireMs();
        long exp = now + expiration;

        String token = EncodeJwt.createEncryptedToken(userId, jti, expiration, (RSAPublicKey) rsaKeyPair.getPublic());

        return new TokenResult(token, now / 1000, exp / 1000);
    }
}