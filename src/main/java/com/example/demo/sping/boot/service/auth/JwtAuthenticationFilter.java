package com.example.demo.sping.boot.service.auth;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
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
        String usersUuid;
    
        try {
            usersUuid = jwtService.getUsernameFromToken(jwt);
        } catch (ExpiredJwtException expiredEx) {
            // ✅ แก้ตรงนี้เลย
            handleExpiredToken(response);
            return;  // สำคัญมาก ต้องหยุดการทำงานที่ตรงนี้
        } catch (Exception e) {
            filterChain.doFilter(request, response);
            return;
        }
    
        JwtAuthenticatedUser principal = new JwtAuthenticatedUser(
            usersUuid, usersUuid, "", new ArrayList<>()
        );
    
        if (jwtService.isTokenValid(jwt, principal)) {
            UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        } else {
            handleExpiredToken(response);
            return;  // สำคัญมาก ต้องหยุดการทำงานที่ตรงนี้เช่นกัน
        }
    
        filterChain.doFilter(request, response);
    }
    
    // ✅ ฟังก์ชั่นช่วยเซ็ต response ให้ง่ายขึ้น
    private void handleExpiredToken(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);  // 403 ตามที่ต้องการ
        response.getWriter().write("""
            {"message": "Access token expired"}
        """);
    }
}