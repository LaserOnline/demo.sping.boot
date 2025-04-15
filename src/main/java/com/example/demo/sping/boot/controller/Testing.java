package com.example.demo.sping.boot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.sping.boot.util.response.Message;

import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@Tag(name = "Testing Controller", description = "example api check")
@RequestMapping("/testing")
public class Testing {
    @GetMapping("/message")
    public ResponseEntity<Message> getMethodName() {
        return ResponseEntity.ok().body(new Message("Hello World"));
    }
}
