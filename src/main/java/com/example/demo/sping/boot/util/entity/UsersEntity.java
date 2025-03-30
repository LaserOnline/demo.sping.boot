package com.example.demo.sping.boot.util.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
@Document(collection = "users")
public class UsersEntity {
    @Id
    private String id;
    @Field("users_uuid")
    private String usersUuid;
    private String username;
    private String email;
    private String password;
}
