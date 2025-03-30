package com.example.demo.sping.boot.util.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.sping.boot.util.entity.UsersEntity;

public interface UserRepository extends MongoRepository<UsersEntity, String> {
    Optional<UsersEntity> findByUsersUuid(String usersUuid);
    Optional<UsersEntity> findByUsername(String username);
    Optional<UsersEntity> findByEmail(String email);
    Optional<UsersEntity> findByPassword(String password);
}
