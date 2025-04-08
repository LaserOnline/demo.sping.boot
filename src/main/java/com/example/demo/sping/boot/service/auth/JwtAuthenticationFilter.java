package com.example.demo.sping.boot.service.auth;

import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.example.demo.sping.boot.util.dto.validated.InvalidTokenException;
import com.example.demo.sping.boot.util.dto.validated.TokenExpiredException;
import com.nimbusds.jwt.JWTClaimsSet;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenValidator tokenValidator;
    private final HandlerExceptionResolver exceptionResolver;
    private final RSAPrivateKey rsaPrivateKey;

    public JwtAuthenticationFilter(JwtTokenValidator tokenValidator,
        @Qualifier("handlerExceptionResolver") 
        HandlerExceptionResolver exceptionResolver,
        RSAPrivateKey rsaPrivateKey) {
    this.tokenValidator = tokenValidator;
    this.exceptionResolver = exceptionResolver;
    this.rsaPrivateKey = rsaPrivateKey;
}

    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    protected void doFilterInternal(
        @SuppressWarnings("null") HttpServletRequest request,
        @SuppressWarnings("null") HttpServletResponse response,
        @SuppressWarnings("null") FilterChain filterChain) 
        throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            Claims claims;

            if (EncodeJwt.isEncryptedToken(token)) {
                // เป็น refresh
                JWTClaimsSet joseClaims = EncodeJwt.decryptEncryptedToken(token, rsaPrivateKey);
                // helper ที่จะแปลง JWTClaimsSet → Claims
                claims = JwtTokenValidator.convertToClaims(joseClaims);
            } else {
                // ✅ เป็น access token แบบธรรมดา
                claims = tokenValidator.parseToken(token);
            }

            if (JwtTokenValidator.isTokenExpired(claims)) {
                throw new TokenExpiredException("JWT token is expired");
            }
         

            // ดึง path url และ type token
            String tokenType = claims.get("type", String.class);
            String requestPath = request.getRequestURI();
            
            // token ที่จะ มีสิทธิรับ access ใหม่ จะต้องเป็น type refresh token เท่านั้น 
            if ("refresh".equals(tokenType) && !requestPath.startsWith("/users/generate/")) {
                exceptionResolver.resolveException(request, response, null, 
                    new InvalidTokenException("Refresh token cannot be used for this endpoint"));
                return;
            }
            
            // หาก access ร้องขอ access token ใหม่ จะทำการ เตะออก
            if ("access".equals(tokenType) && requestPath.startsWith("/users/generate/")) {
                exceptionResolver.resolveException(request, response, null, 
                    new InvalidTokenException("Access token cannot be used for this endpoint"));
                return;
            }

            String userUuid = claims.getSubject();
            // Decode uuid ของ users
            String decodeUsersUuid = EncodeJwt.decodeJwtBase64(userUuid);
            List<String> encryptedRoles = claims.get("roles", List.class);
            List<GrantedAuthority> authorities = new ArrayList<>();
            if (encryptedRoles != null) {
                for (String encryptedRole : encryptedRoles) {
                    try {
                        // Decrypt the encrypted role
                        String decryptedRole = EncodeJwt.decryptAES(encryptedRole);
                        // Add as a GrantedAuthority
                        authorities.add(new SimpleGrantedAuthority(decryptedRole));
                    } catch (Exception e) {
                        // จัดการข้อผิดพลาดในการถอดรหัส
                        e.printStackTrace();
                    }
                }
            }
            // ส่ง อายุ token
            Date expiration = claims.getExpiration();
            long remainingTime = (expiration.getTime() - System.currentTimeMillis()) / 1000;
            // สร้าง PayloadData object เพื่อส่งไป cotnroller
            PayloadData payloadData = new PayloadData(decodeUsersUuid, authorities,remainingTime);
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(payloadData, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            exceptionResolver.resolveException(request, response, null,
                    new TokenExpiredException("JWT token is expired"));
        } catch (JwtException | IllegalArgumentException e) {
            exceptionResolver.resolveException(request, response, null,
                    new InvalidTokenException("Invalid or malformed JWT token"));
        }
    }
}