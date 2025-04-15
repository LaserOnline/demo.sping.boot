package com.example.demo.sping.boot.util.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UpdateContentDTO {

    @NotEmpty(message = "name empty")
    private String uuid;

    private String name;

    private String message;

    private List<String> files;
    
}
