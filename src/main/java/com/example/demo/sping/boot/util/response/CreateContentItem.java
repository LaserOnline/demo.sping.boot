package com.example.demo.sping.boot.util.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateContentItem {
    private String userUuid;
    private String contentUuid;
    private String name;
    private String message;
    private List<String> files;
}
