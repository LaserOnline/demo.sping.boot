package com.example.demo.sping.boot.util.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "content")
public class ContentEntity {
    @Id
    private String id;

    private String contentUuid;

    private String usersUuid;

    private String name;

    private String message;
}
