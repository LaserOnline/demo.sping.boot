package com.example.demo.sping.boot.util.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "users_info")
public class UsersInfoEntity {
    @Id
    private String id;
    private String usersUuid;
    private String firstName;
    private String lastName;
    private String profile;
    private String address;
}
