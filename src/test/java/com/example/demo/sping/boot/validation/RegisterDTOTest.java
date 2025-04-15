package com.example.demo.sping.boot.validation;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.demo.sping.boot.util.dto.RegisterDTO;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class RegisterDTOTest {
    private Validator validator;
// False
    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Use Case: username is empty should trigger validation error")
    void testUsernameEmpty() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("");

        Set<ConstraintViolation<RegisterDTO>> violations = validator.validate(registerDTO);

        violations.forEach(v -> {
            if (v.getPropertyPath().toString().equals("username")) {
                System.out.print("Message Error: " + v.getMessage() + "\n\n");
            }
        });

        assertFalse(violations.isEmpty());
        
        boolean foundErrorForUsernameEmpty = 
        violations.stream().anyMatch(v -> v.getPropertyPath()
            .toString()
            .equals("username") && v.getMessage().contains("username empty"));
        assertTrue(foundErrorForUsernameEmpty);
    }

    @Test
    @DisplayName("Use Case username not eng")
    void testUsernameNotEng() {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("มีหวานใจยังครับฮาฟู");

        Set<ConstraintViolation<RegisterDTO>> response = validator.validate(dto);

        response.forEach(v -> {
            if (v.getPropertyPath().toString().equals("username")) {
                System.out.print("Message Error: " + v.getMessage() + "\n\n");
            }
        });

        assertFalse(response.isEmpty());

        boolean usernameFormatEng = response.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("username") && v.getMessage().contains("username must contain only English letters (a-z, A-Z) and numbers (0-9) without special characters"));
        assertTrue(usernameFormatEng);
    }

    @Test
    @DisplayName("use case username must be between 5")
    void testUsernameMin() {
        RegisterDTO dto = new RegisterDTO();

        dto.setUsername("1234");

        Set<ConstraintViolation<RegisterDTO>> response = validator.validate(dto);

        response.forEach(v -> {
            if (v.getPropertyPath().toString().equals("username")) {
                System.out.print("Message Error: " + v.getMessage() + "\n\n");
            }
        });

        assertFalse(response.isEmpty());

        boolean username = response.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("username") && v.getMessage().contains("username must be between 5 characters"));
            assertTrue(username);
    }

    @Test
    @DisplayName("use case username max 13 characters long")
    void testUsernameMax() {
        RegisterDTO dto = new RegisterDTO();

        dto.setUsername("1234567890123456");

        Set<ConstraintViolation<RegisterDTO>> response = validator.validate(dto);
        
        response.forEach(v -> {
            if (v.getPropertyPath().toString().equals("username")) {
                System.out.print("Message Error: " + v.getMessage() + "\n\n");
            }
        });

        assertFalse(response.isEmpty());

        boolean username = response.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("username") && v.getMessage().contains("username max 13 characters long"));
            assertTrue(username);
    }
    
    @Test
    @DisplayName("Use Case: username contains special characters should trigger validation error")
    void testUsernameWithSpecialCharacters() {
        RegisterDTO dto = new RegisterDTO();

        dto.setUsername("user@#123");
        Set<ConstraintViolation<RegisterDTO>> response = validator.validate(dto);

        // แสดง message เฉพาะ username
        response.forEach(v -> {
            if (v.getPropertyPath().toString().equals("username")) {
                System.out.print("Message Error: " + v.getMessage() + "\n\n");
            }
        });

        // ต้องมี error
        assertFalse(response.isEmpty());

        // ตรวจว่ามี error ที่เกี่ยวกับการใช้ตัวอักษรพิเศษใน username
        boolean containsSpecialCharError = response.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("username") &&
                        v.getMessage().contains("only English letters (a-z, A-Z) and numbers (0-9)"));
        assertTrue(containsSpecialCharError);
    }

    @Test
    @DisplayName("Use Case: email is empty should trigger validation error")
    void testEmailEmpty() {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("ValidUser");
        dto.setPassword("Aa!123456");
        dto.setEmail(""); // ค่าว่าง

        Set<ConstraintViolation<RegisterDTO>> violations = validator.validate(dto);

        violations.forEach(v -> {
            if (v.getPropertyPath().toString().equals("email")) {
                System.out.println("Message Error: " + v.getMessage() + "\n");
            }
        });

        assertFalse(violations.isEmpty());

        boolean emailEmptyError = violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("email")
                        && v.getMessage().contains("Email is empty"));

        assertTrue(emailEmptyError);
    }

    @Test
    @DisplayName("Use Case: email format is invalid should trigger validation error")
    void testEmailInvalidFormat() {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("ValidUser");
        dto.setPassword("Aa!123456");
        dto.setEmail("invalid-email-format"); // ไม่มี @ หรือ .com

        Set<ConstraintViolation<RegisterDTO>> violations = validator.validate(dto);

        violations.forEach(v -> {
            if (v.getPropertyPath().toString().equals("email")) {
                System.out.println("Message Error: " + v.getMessage() + "\n");
            }
        });

        assertFalse(violations.isEmpty());

        boolean emailFormatError = violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("email")
                        && v.getMessage().contains("Email must be valid"));

        assertTrue(emailFormatError);
    }

    @Test
    @DisplayName("Use Case: password is empty")
    void testPasswordEmpty() {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("ValidUser");
        dto.setEmail("test@example.com");
        dto.setPassword("");
    
        Set<ConstraintViolation<RegisterDTO>> violations = validator.validate(dto);
    
        violations.stream()
            .filter(v -> v.getPropertyPath().toString().equals("password"))
            .forEach(v -> System.out.println("Message Error: " + v.getMessage() + "\n"));
    
        assertFalse(violations.isEmpty());
    
        boolean foundEmptyError = violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("password") &&
                           v.getMessage().contains("password is empty"));
        assertTrue(foundEmptyError);
    }

    @Test
    @DisplayName("Use Case: password missing uppercase")
    void testPasswordMissingUppercase() {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("ValidUser");
        dto.setEmail("test@example.com");
        dto.setPassword("abc123!@#"); // ไม่มีตัวพิมพ์ใหญ่
    
        Set<ConstraintViolation<RegisterDTO>> violations = validator.validate(dto);
    
        assertFalse(violations.isEmpty());
    
        boolean formatError = violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("password") &&
                           v.getMessage().contains("Password must be"));
        assertTrue(formatError);
    }

    @Test
    @DisplayName("Use Case: password missing special character")
    void testPasswordMissingSpecialCharacter() {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("ValidUser");
        dto.setEmail("test@example.com");
        dto.setPassword("Abc123456"); // ไม่มีอักขระพิเศษ
    
        Set<ConstraintViolation<RegisterDTO>> violations = validator.validate(dto);
    
        // ✅ แสดงข้อความ error ของ password
        violations.stream()
            .filter(v -> v.getPropertyPath().toString().equals("password"))
            .forEach(v -> System.out.println("Message Error: " + v.getMessage() + "\n"));
    
        assertFalse(violations.isEmpty());
    
        boolean formatError = violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("password") &&
                           v.getMessage().contains("Password must be"));
        assertTrue(formatError);
    }

    @Test
    @DisplayName("Use Case: password valid")
    void testPasswordValid() {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("ValidUser");
        dto.setEmail("test@example.com");
        dto.setPassword("Aa@123456"); // ถูกต้องทุกเงื่อนไข
    
        Set<ConstraintViolation<RegisterDTO>> violations = validator.validate(dto);
    
        assertTrue(violations.isEmpty(), "Password ควรผ่านการ validate");
    }
// False

    // True 
    @Test
    @DisplayName("Use Case: username is valid")
    void TestUsername() {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("LaserOnline");
        Set<ConstraintViolation<RegisterDTO>> violations = validator.validateProperty(dto, "username");
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Use Case: password is valid")
    void TestPassword() {
        RegisterDTO dto = new RegisterDTO();
        dto.setPassword("!@LaserOnline1988");
        Set<ConstraintViolation<RegisterDTO>> violations = validator.validateProperty(dto, "password");
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Use Case: email is valid")
    void TestEmail() {
        RegisterDTO dto = new RegisterDTO();
        dto.setEmail("email@gmail.com");
        Set<ConstraintViolation<RegisterDTO>> violations = validator.validateProperty(dto, "email");
        assertTrue(violations.isEmpty());
    }
    // True 
}