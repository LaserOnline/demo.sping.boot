package com.example.demo.sping.boot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.sping.boot.service.removefile.RemoveFileService;
import com.example.demo.sping.boot.util.repository.UserRepository;
import com.example.demo.sping.boot.util.repository.UsersInfoRepository;
import com.example.demo.sping.boot.util.response.Message;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Databases Controller", description = "control databases")
@RequestMapping("/databases")
public class DatabasesController {
    private final RemoveFileService removeFileService;
    private final UserRepository userRepository;
    private final UsersInfoRepository usersInfoRepository;

    public DatabasesController(RemoveFileService removeFileService, UserRepository userRepository, UsersInfoRepository usersInfoRepository) {
        this.userRepository = userRepository;
        this.usersInfoRepository = usersInfoRepository;
        this.removeFileService = removeFileService;
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Object> clearDatabases() {
        userRepository.deleteAll();
        usersInfoRepository.deleteAll();
        removeFileService.clearAllFilesInUploadPath();
        return ResponseEntity.ok().body(new Message("All databases have been cleared."));
    }

}