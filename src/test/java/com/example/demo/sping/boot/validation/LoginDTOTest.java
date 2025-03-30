package com.example.demo.sping.boot.validation;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.demo.sping.boot.util.dto.LoginDTO;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class LoginDTOTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }


    @Test
    @DisplayName("use case username is empty")
    void testInputUsernameEmpty() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("");
        Set<ConstraintViolation<LoginDTO>> violations = validator.validate(loginDTO);
        violations.forEach(v -> {
            if (v.getPropertyPath().toString().equals("username")) {
                System.out.println("message error "+ v.getMessage());
            }
        });
        assertFalse(violations.isEmpty());
        boolean foundErrorForUsernameEmpty = violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("username") && v.getMessage().contains("username is empty"));
        assertTrue(foundErrorForUsernameEmpty);
    }

    @Test
    @DisplayName("use case password is empty")
    void testInputPasswordEmpty() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setPassword("");
        Set<ConstraintViolation<LoginDTO>> violations = validator.validate(loginDTO);
        violations.forEach(v -> {
            if (v.getPropertyPath().toString().equals("password")) {
                System.out.println("message error "+ v.getMessage());
            }
        });
    }
}
