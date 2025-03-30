package com.example.demo.sping.boot.service.auth;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        // ลองดึง Username จาก token ก่อน
        String username;
        try {
            username = jwtService.getUsernameFromToken(jwt);
        } catch (Exception e) {
            // Token ผิดรูป หรือถอดแล้วพัง
            filterChain.doFilter(request, response);
            return;
        }

        // สร้าง principal เพื่อใช้เช็คกับ isTokenValid(...)
        User principal = new User(
            username,
            "", 
            new ArrayList<>() // ไม่มี authorities ก็ได้
        );

        // **เพิ่มขั้นตอนนี้**: เช็คว่าเป็น Access Token จริง ๆ หรือไม่
        // (token_type=access, ไม่ expired, username ตรง)
        if (!jwtService.isTokenValid(jwt, principal)) {
            // ถ้าไม่ valid -> ไม่ set Authen ให้ -> สุดท้าย Security จะให้ 401
            filterChain.doFilter(request, response);
            return;
        }

        // ถ้า valid -> สร้าง Auth แล้วใส่ใน SecurityContextHolder
        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}