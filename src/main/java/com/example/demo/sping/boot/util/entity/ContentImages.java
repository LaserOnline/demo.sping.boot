package com.example.demo.sping.boot.util.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "content_images")
public class ContentImages {
    @Id
    private String id;

    private String contentUuid;

    private List<String> file;
}
