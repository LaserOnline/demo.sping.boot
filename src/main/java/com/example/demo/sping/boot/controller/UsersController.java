package com.example.demo.sping.boot.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.sping.boot.service.auth.JwtAuthenticatedUser;
import com.example.demo.sping.boot.service.auth.JwtService;
import com.example.demo.sping.boot.service.upload.UploadImagesService;
import com.example.demo.sping.boot.service.users.PasswordService;
import com.example.demo.sping.boot.service.users.UserValidationService;
import com.example.demo.sping.boot.service.users.UsersService;
import com.example.demo.sping.boot.util.dto.LoginDTO;
import com.example.demo.sping.boot.util.dto.PasswordDTO;
import com.example.demo.sping.boot.util.dto.RegisterDTO;
import com.example.demo.sping.boot.util.dto.UsersInfoDTO;
import com.example.demo.sping.boot.util.entity.UsersEntity;
import com.example.demo.sping.boot.util.response.JwtResponse;
import com.example.demo.sping.boot.util.response.Message;
import com.example.demo.sping.boot.util.response.UsersInfo;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;





@RestController
@Tag(name = "Users Controller", description = "method users all")
@RequestMapping("/users")
public class UsersController {
    private  final UsersService usersService;
    private final UserValidationService userValidationService;
    private final PasswordService passwordService;
    private final JwtService jwtService;
    private final UploadImagesService uploadImagesService;

    public UsersController (
        UsersService usersService,
        UserValidationService userValidationService,
        PasswordService passwordService,
        JwtService jwtService,
        UploadImagesService uploadImagesService
    ) {
        this.usersService = usersService;
        this.userValidationService = userValidationService;
        this.passwordService = passwordService;
        this.jwtService = jwtService;
        this.uploadImagesService = uploadImagesService;
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
        
        String accessToken = jwtService.generateAccessToken(user.getUsersUuid());
        String refreshToken = jwtService.generateRefreshToken(user.getUsersUuid());

        return ResponseEntity.ok().body(new JwtResponse(accessToken, refreshToken));
    }
    

    @GetMapping("/auth/message")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Message> authMessage() {
        return ResponseEntity.ok().body(new Message("Hello"));
    }

    @GetMapping("/auth/fetch/info")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<?> usersInfo(@AuthenticationPrincipal JwtAuthenticatedUser principal) {
        String usersUuid = principal.getUsersUuid();

        // เช็คว่า usersEntity มีไหม
        if (usersService.findByUsersUuid(usersUuid).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Message("user not found"));
        }

        // เช็คว่า usersInfoEntity มีไหม
        if (!usersService.hasUsersInfo(usersUuid)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Message("user info not found"));
        }

        UsersInfo userInfo = usersService.getUserInfoByUuid(usersUuid);
        return ResponseEntity.ok(userInfo);
}
        
    @PostMapping("/auth/edit/info")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Object> updateInfo(
            @Valid @RequestBody UsersInfoDTO usersInfoDTO,
            @AuthenticationPrincipal JwtAuthenticatedUser principal) {

        return ResponseEntity.ok().body(new Message(principal.getUsersUuid()));
    }

    @PostMapping(value = "/auth/upload/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Upload Success",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Message.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "File is not a valid image",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Message.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Token missing or invalid",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Message.class)
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - you do not have permission",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Message.class)
            )
        ),
    })
    public ResponseEntity<Object> uploadProfile(
    @AuthenticationPrincipal JwtAuthenticatedUser principal,
    @RequestPart("file") MultipartFile file
    ) {
        String oldFilename = usersService.getProfileImageByUsersUuid(principal.getUsersUuid())
        .orElse(""); 
        System.out.println("-> " + oldFilename);
        boolean remove = uploadImagesService.removeImagesOld(oldFilename);

        if (!remove) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new Message("error update image old")); 
        }

        String filename = uploadImagesService.processImage(file, principal.getUsersUuid());
        boolean updated = usersService.updateUserProfileImage(principal.getUsersUuid(), filename);
        if (!updated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new Message("User info not found"));
        }
    
        return ResponseEntity.ok().body(new Message("Upload Successfully"));
    }

    @PutMapping("/auth/reset/password")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Object> resetPassword(
        @Valid @RequestBody PasswordDTO passwordDTO,
        @AuthenticationPrincipal JwtAuthenticatedUser principal
    ) {
        return ResponseEntity.ok().body(new Message(principal.getUsersUuid()));
    }
    
    @PostMapping("/auth/access/token")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Object> createAccessToken(@RequestBody String entity) {
        return ResponseEntity.ok().body(new Message(""));
    }

    @PostMapping("/auth/refresh/token")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Object> createRefreshToken(@RequestBody String entity) {
        return ResponseEntity.ok().body(new Message(""));
    }
    
    
}
