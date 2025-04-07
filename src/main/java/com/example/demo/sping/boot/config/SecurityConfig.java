package com.example.demo.sping.boot.config;

import javax.crypto.SecretKey;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.demo.sping.boot.service.auth.JwtAuthenticationFilter;

import io.jsonwebtoken.security.Keys;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.servlet.http.HttpServletResponse;


@Configuration
@EnableMethodSecurity
public class SecurityConfig {

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
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 1) Whitelist เฉพาะ path ที่ไม่ต้องใช้ Token
                .requestMatchers(
                    "/users/register", // สมัครสมาชิก
                    "/users/login",    // ล็อกอิน
                    "/app/**",
                    "/swagger/**",
                    "/api-docs/**",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll()

                .requestMatchers("/users/generate/**").authenticated()
                .requestMatchers("/users/auth/**").authenticated()
                .anyRequest().authenticated()
            )

            // กำหนด exception handling ให้ส่ง 401 เมื่อไม่มี/ไม่ผ่าน Auth
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
                })
            )

            // สั่งสร้าง session แบบ stateless
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // ใส่ Filter ของเรา (JWT) ก่อน UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecretKey jwtSecretKey(Config config) {
        return Keys.hmacShaKeyFor(config.getJwtSecret().getBytes());
    }
    
}