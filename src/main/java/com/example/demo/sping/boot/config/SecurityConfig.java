package com.example.demo.sping.boot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.demo.sping.boot.service.auth.JwtAuthenticationFilter;
import com.example.demo.sping.boot.util.dto.validated.InvalidTokenTypeException;
import com.example.demo.sping.boot.util.dto.validated.TokenExpiredAuthenticationException;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.servlet.http.HttpServletResponse;


@Configuration
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info().title("Swagger Spring Boot Demo")
            .version("1.0")
            .description("API documentation")
            .contact(new Contact().name("Laser Online").email("kaochiam@gmail.com").url("https://github.com/LaserOnline"))
            .license(new License().name("API License").url("https://github.com/LaserOnline")))
            .components(new Components().addSecuritySchemes("Bearer Authentication",
                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/users/register", "/users/login", 
                                "/app/**",
                                "/swagger/**", "/api-docs/**", "/v3/api-docs/**",
                                "/swagger-ui/**", "/swagger-ui.html")
                    .permitAll()
                .requestMatchers("/users/auth/**").authenticated()
                .anyRequest().denyAll()
            )

            .exceptionHandling(exception -> exception
            .authenticationEntryPoint((request, response, authException) -> {
                response.setContentType("application/json;charset=UTF-8");
                    if (authException instanceof TokenExpiredAuthenticationException) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
                        response.getWriter().write("""
                            {"message": "Access token expired"}
                        """);
                    } else if (authException.getCause() instanceof InvalidTokenTypeException) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("""
                            {"message": "Invalid refresh token"}
                        """);
                    } else {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("""
                            {"message": "Token missing or invalid"}
                        """);
                    }
                })
            )

            .addFilterBefore(jwtAuthFilter, 
                org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    
    
}