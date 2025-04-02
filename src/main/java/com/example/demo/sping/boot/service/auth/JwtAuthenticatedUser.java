package com.example.demo.sping.boot.service.auth;

import java.util.Collection;

import org.springframework.security.core.userdetails.User;

public class JwtAuthenticatedUser extends User {
    private final String usersUuid;

    public JwtAuthenticatedUser(String usersUuid, String username, String password, @SuppressWarnings("rawtypes") Collection authorities) {
        super(username, password, authorities);
        this.usersUuid = usersUuid;
    }

    public String getUsersUuid() {
        return usersUuid;
    }
}