package com.example.demo.sping.boot.util.response;

import lombok.Data;

@Data
public class Message {
    private String message;
    public Message(String message) {
        this.message = message;
    }
}
