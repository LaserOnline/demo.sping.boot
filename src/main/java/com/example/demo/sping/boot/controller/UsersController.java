package com.example.demo.sping.boot.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.sping.boot.service.auth.AuthService;
import com.example.demo.sping.boot.service.auth.EncodeJwt;
import com.example.demo.sping.boot.service.auth.PayloadData;
import com.example.demo.sping.boot.service.upload.UploadImagesService;
import com.example.demo.sping.boot.service.users.PasswordService;
import com.example.demo.sping.boot.service.users.UserValidationService;
import com.example.demo.sping.boot.service.users.UsersService;
import com.example.demo.sping.boot.util.dto.LoginDTO;
import com.example.demo.sping.boot.util.dto.RegisterDTO;
import com.example.demo.sping.boot.util.entity.UsersEntity;
import com.example.demo.sping.boot.util.response.JwtResponse;
import com.example.demo.sping.boot.util.response.Message;
import com.example.demo.sping.boot.util.response.UsersInfo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
    private final UploadImagesService uploadImagesService;
    private final AuthService authService;

    public UsersController (
        UsersService usersService,
        UserValidationService userValidationService,
        PasswordService passwordService,
        UploadImagesService uploadImagesService,
        AuthService authService
    ) {
        this.usersService = usersService;
        this.userValidationService = userValidationService;
        this.passwordService = passwordService;
        this.uploadImagesService = uploadImagesService;
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "create users")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "register successfully",
        content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = Message.class),
        examples = @ExampleObject(
                name = "Success",
                summary = "Create Users Successfully",
                value = "{ \"message\": \"Register Successfully\" }"
            )
        )),
        @ApiResponse(
            responseCode = "400",
            description = "Property Validation",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Message.class),
                examples = {
                    // Username validation
                    @ExampleObject(
                        name = "UsernameEmpty",
                        summary = "Username is empty",
                        value = "{ \"message\": \"username empty\" }"
                    ),
                    @ExampleObject(
                        name = "UsernameTooShort",
                        summary = "Username too short",
                        value = "{ \"message\": \"username must be between 5 characters\" }"
                    ),
                    @ExampleObject(
                        name = "UsernameTooLong",
                        summary = "Username too long",
                        value = "{ \"message\": \"username max 13 characters long\" }"
                    ),
                    @ExampleObject(
                        name = "UsernameInvalidChars",
                        summary = "Username has invalid characters",
                        value = "{ \"message\": \"username must contain only English letters (a-z, A-Z) and numbers (0-9) without special characters\" }"
                    ),
                    // Email validation
                    @ExampleObject(
                        name = "EmailEmpty",
                        summary = "Email is empty",
                        value = "{ \"message\": \"Email is empty\" }"
                    ),
                    @ExampleObject(
                        name = "EmailInvalid",
                        summary = "Email format is invalid",
                        value = "{ \"message\": \"Email must be valid\" }"
                    ),
                    // Password validation
                    @ExampleObject(
                        name = "PasswordEmpty",
                        summary = "Password is empty",
                        value = "{ \"message\": \"password is empty\" }"
                    ),
                    @ExampleObject(
                        name = "PasswordInvalidFormat",
                        summary = "Password format invalid",
                        value = "{ \"message\": \"Password must be 9-30 characters, contain uppercase, lowercase, number, and special character\" }"
                )
            })
        )
    })
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
    @Operation(summary = "login users get jwt token")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "login successfully",
        content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = JwtResponse.class)
        )),
        @ApiResponse(
        responseCode = "400",
        description = "Property Validation",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Message.class),
            examples = {
                @ExampleObject(
                    name = "username is empty",
                    summary = "Username is empty",
                    value = "{ \"message\": \"username is empty\" }"
                ),
                @ExampleObject(
                    name = "password is empty",
                    summary = "Username is empty",
                    value = "{ \"message\": \"password is empty\" }"
                )
            }
        )),
        @ApiResponse(
        responseCode = "401",
        description = "Invalid password",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Message.class),
            examples = @ExampleObject(
                name = "UNAUTHORIZED",
                summary = "Invalid Password",
                value = "{ \"message\": \"Invalid password\" }"
            )
        )),
        @ApiResponse(
        responseCode = "404",
        description = "username  not found",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Message.class),
            examples = @ExampleObject(
                name = "NOT_FOUND",
                summary = "Email Not Found",
                value = "{ \"message\": \"username  not found\" }"
            )
        ))
    })
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
        
        String encodeUuid = EncodeJwt.encodeJwtBase64(user.getUsersUuid());
        List<String> roles = List.of("USER");
        List<String> encryptedRoles;
        try {
            encryptedRoles = roles.stream()
                .map(role -> {
                    try {
                        return EncodeJwt.encryptAES(role);
                    } catch (Exception e) {
                        throw new RuntimeException("Error encrypting role: " + role, e);
                    }
        })
                .collect(Collectors.toList());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new Message(e.getMessage()));
        }
        // ส่ง token
        String accessToken = authService.generateAccessToken(encodeUuid,encryptedRoles);
        String refreshToken = authService.generateRefreshToken(encodeUuid,"true");

        return ResponseEntity.ok().body(new JwtResponse(accessToken, refreshToken));
    }

    // create access token
    @Operation(summary = "create new access token")
    @ApiResponses({
        @ApiResponse(
        responseCode = "200",
        description = "Create Access Token Successfully",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Message.class),
            examples = @ExampleObject(
                name = "Success",
                summary = "access token",
                value = "{ \"message\": \"accessToken\" }"
            )
        )),
        @ApiResponse(
        responseCode = "401",
        description = "Jwt token is expired",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Message.class),
            examples = @ExampleObject(
                name = "token expired",
                summary = "token expired",
                value = "{ \"message\": \"JWT token is expired\" }"
            )
        )),
        @ApiResponse(
        responseCode = "403",
        description = "Forbidden",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Message.class),
            examples = {
                @ExampleObject(
                    name = "Forbidden",
                    summary = "token malformed JWT",
                    value = "{ \"message\": \"Invalid or malformed JWT token\" }"
                ),
                @ExampleObject(
                    name = "Token type access",
                    summary = "if send access token",
                    value = "{ \"message\": \"Access token cannot be used for this endpoint\" }"
                ),
            }
        ))
    })
    @PostMapping("/generate/access")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Message> generateAccessToken(@AuthenticationPrincipal PayloadData payloadData) {
        String encodeUuid = EncodeJwt.encodeJwtBase64(payloadData.getUsername());
        List<String> roles = List.of("USER");
        List<String> encryptedRoles;
        try {
            encryptedRoles = roles.stream()
                .map(role -> {
                    try {
                        return EncodeJwt.encryptAES(role);
                    } catch (Exception e) {
                        throw new RuntimeException("Error encrypting role: " + role, e);
                    }
        })
                .collect(Collectors.toList());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new Message(e.getMessage()));
        }
        String accessToken = authService.generateAccessToken(encodeUuid,encryptedRoles);
        return ResponseEntity.ok().body(new Message(accessToken));
    }

    // create new refresh token
    @PostMapping("generate/refresh")
    @Operation(summary = "create new refresh token")
    @ApiResponses({
        @ApiResponse(
        responseCode = "200",
        description = "Create Refresh Token Successfully",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Message.class),
            examples = @ExampleObject(
                name = "Success",
                summary = "refresh token",
                value = "{ \"message\": \"refreshToken\" }"
            )
        )),
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Message> generateRefreshToken(@AuthenticationPrincipal PayloadData payloadData) {
        long remainingTime = payloadData.getRemainingTime();
        System.err.println(remainingTime);
        return ResponseEntity.ok().body(new Message("hello"));
    }

    // ดึงข้อมูลผู้ใช้
    @Operation(summary = "Users Info")
    @GetMapping("/auth/fetch/info")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Fetch User Info Successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UsersInfo.class)
            )),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Message.class),
                examples = @ExampleObject(
                    name = "token expired",
                    summary = "token expired",
                    value = "{ \"message\": \"JWT token is expired\" }"
                )
            )),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Message.class),
            examples = @ExampleObject(
                name = "Forbidden",
                summary = "if send refresh Token",
                value = "{ \"message\": \"Refresh token cannot be used for this endpoint\" }"
            )
        )),
        @ApiResponse(
            responseCode = "404",
            description = "users not found",
            content = @Content(
                mediaType = "application/json",
                    schema = @Schema(implementation = Message.class),
                    examples = {
                        @ExampleObject(
                            name = "user not found",
                            summary = "users entity not found",
                            value = "{ \"message\": \"JWT token is expired\" }"
                        ),
                        @ExampleObject(
                            name = "user info not found",
                            summary = "user info not found",
                            value = "{ \"message\": \"user info not found\" }"
                        )
                    }
                )),   
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<?> usersInfo(@AuthenticationPrincipal PayloadData payloadData) {
        String usersUuid = payloadData.getUsername();

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
        
//     @PostMapping("/auth/edit/info")
//     @SecurityRequirement(name = "Bearer Authentication")
//     public ResponseEntity<Object> updateInfo(
//             @Valid @RequestBody UsersInfoDTO usersInfoDTO,
//             @AuthenticationPrincipal JwtAuthenticatedUser principal) {

//         return ResponseEntity.ok().body(new Message(principal.getUsersUuid()));
//     }

//     @PostMapping(value = "/auth/upload/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//     @SecurityRequirement(name = "Bearer Authentication")
//     @ApiResponses({
//         @ApiResponse(
//             responseCode = "200",
//             description = "Upload Success",
//             content = @Content(
//                 mediaType = "application/json",
//                 schema = @Schema(implementation = Message.class)
//             )
//         ),
//         @ApiResponse(
//             responseCode = "400",
//             description = "File is not a valid image",
//             content = @Content(
//                 mediaType = "application/json",
//                 schema = @Schema(implementation = Message.class)
//             )
//         ),
//         @ApiResponse(
//             responseCode = "401",
//             description = "Token missing or invalid",
//             content = @Content(
//                 mediaType = "application/json",
//                 schema = @Schema(implementation = Message.class)
//             )
//         ),
//         @ApiResponse(
//             responseCode = "403",
//             description = "Forbidden - you do not have permission",
//             content = @Content(
//                 mediaType = "application/json",
//                 schema = @Schema(implementation = Message.class)
//             )
//         ),
//     })
//     public ResponseEntity<Object> uploadProfile(
//     @AuthenticationPrincipal JwtAuthenticatedUser principal,
//     @RequestPart("file") MultipartFile file
//     ) {
//         String oldFilename = usersService.getProfileImageByUsersUuid(principal.getUsersUuid())
//         .orElse(""); 
//         System.out.println("-> " + oldFilename);
//         boolean remove = uploadImagesService.removeImagesOld(oldFilename);

//         if (!remove) {
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                 .body(new Message("error update image old")); 
//         }

//         String filename = uploadImagesService.processImage(file, principal.getUsersUuid());
//         boolean updated = usersService.updateUserProfileImage(principal.getUsersUuid(), filename);
//         if (!updated) {
//             return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                 .body(new Message("User info not found"));
//         }
    
//         return ResponseEntity.ok().body(new Message("Upload Successfully"));
//     }

//     @PutMapping("/auth/reset/password")
//     @SecurityRequirement(name = "Bearer Authentication")
//     public ResponseEntity<Object> resetPassword(
//         @Valid @RequestBody PasswordDTO passwordDTO,
//         @AuthenticationPrincipal JwtAuthenticatedUser principal
//     ) {
//         return ResponseEntity.ok().body(new Message(principal.getUsersUuid()));
//     }
    
//     @PostMapping("/auth/access/token")
//     @SecurityRequirement(name = "Bearer Authentication")
//     public ResponseEntity<Object> createAccessToken(@RequestBody String entity) {
//         return ResponseEntity.ok().body(new Message(""));
//     }

//     @PostMapping("/auth/refresh/token")
//     @SecurityRequirement(name = "Bearer Authentication")
//     public ResponseEntity<Object> createRefreshToken(@RequestBody String entity) {
//         return ResponseEntity.ok().body(new Message(""));
//     }
    
    
}
