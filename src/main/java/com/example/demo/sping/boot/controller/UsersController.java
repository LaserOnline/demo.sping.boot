package com.example.demo.sping.boot.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.sping.boot.service.auth.JwtService;
import com.example.demo.sping.boot.service.users.PasswordService;
import com.example.demo.sping.boot.service.users.UserValidationService;
import com.example.demo.sping.boot.service.users.UsersService;
import com.example.demo.sping.boot.util.dto.LoginDTO;
import com.example.demo.sping.boot.util.dto.RegisterDTO;
import com.example.demo.sping.boot.util.entity.UsersEntity;
import com.example.demo.sping.boot.util.response.JwtResponse;
import com.example.demo.sping.boot.util.response.Message;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;



@RestController
@Tag(name = "Users", description = "Users Controller")
@RequestMapping("/users")
public class UsersController {
    private  final UsersService usersService;
    private final UserValidationService userValidationService;
    private final PasswordService passwordService;
    private final JwtService jwtService;

    public UsersController (
        UsersService usersService,
        UserValidationService userValidationService,
        PasswordService passwordService,
        JwtService jwtService
    ) {
        this.usersService = usersService;
        this.userValidationService = userValidationService;
        this.passwordService = passwordService;
        this.jwtService = jwtService;
    }
    @PostMapping("/register")
    public ResponseEntity<Message> register (@Valid @RequestBody RegisterDTO registerDTO) {
        if (userValidationService.haveEmailExists(registerDTO.getEmail())) {
            return ResponseEntity.badRequest().body(new Message("Email already exists"));
        }

        if (userValidationService.haveUsernameExists(registerDTO.getUsername())) {
            return ResponseEntity.badRequest().body(new Message("Username already exists"));
        }

        String encodePassword = passwordService.encodePassword(registerDTO.getPassword());
        registerDTO.setPassword(encodePassword);
        
        UsersEntity user = usersService.mapToUsersEntity(registerDTO);
        usersService.saveUser(user);

        return ResponseEntity.ok(new Message("Register Successfully"));
    }


    @PostMapping("/login")
    public ResponseEntity<Object> loginUser(@Valid @RequestBody LoginDTO loginDTO) {
        Optional<UsersEntity> userOptional = usersService.findByUsername(loginDTO.getUsername());

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("username  not found"));
        }

        UsersEntity user = userOptional.get();

        if (!passwordService.matches(loginDTO.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body(new Message("Invalid password"));
        }
        
        String accessToken = jwtService.generateAccessToken(user.getUsername());
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());

        return ResponseEntity.ok().body(new JwtResponse(accessToken, refreshToken));
    }
    

    @GetMapping("/jwt/message")
    public ResponseEntity<Message> authMessage() {
        return ResponseEntity.ok().body(new Message("HelloWorld"));
    }
    
    
}
