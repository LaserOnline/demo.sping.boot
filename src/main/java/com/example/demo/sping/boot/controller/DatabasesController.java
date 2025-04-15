package com.example.demo.sping.boot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.sping.boot.service.removefile.RemoveFileService;
import com.example.demo.sping.boot.util.repository.ContentImagesRepository;
import com.example.demo.sping.boot.util.repository.ContentRepository;
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
    private final ContentRepository contentRepository;
    private final ContentImagesRepository contentImagesRepository;

    public DatabasesController(
        RemoveFileService removeFileService, 
        UserRepository userRepository, 
        UsersInfoRepository usersInfoRepository,
        ContentRepository contentRepository,
        ContentImagesRepository contentImagesRepository
        ) {
        this.userRepository = userRepository;
        this.usersInfoRepository = usersInfoRepository;
        this.removeFileService = removeFileService;
        this.contentRepository = contentRepository;
        this.contentImagesRepository = contentImagesRepository;
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Object> clearDatabases() {
        userRepository.deleteAll();
        usersInfoRepository.deleteAll();
        contentRepository.deleteAll();
        contentImagesRepository.deleteAll();
        removeFileService.clearAllFilesInUploadPath();
        return ResponseEntity.ok().body(new Message("All databases have been cleared."));
    }

}