package com.example.demo.sping.boot.util.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.sping.boot.util.entity.UsersInfoEntity;

public interface UsersInfoRepository extends MongoRepository<UsersInfoEntity, String> {
    Optional<UsersInfoEntity> findByUsersUuid(String usersUuid);
    Optional<UsersInfoEntity> findByProfile(String profile);
    Optional<UsersInfoEntity> findByFirstName(String firstName); 
    Optional<UsersInfoEntity> findByLastName(String lastName);
    Optional<UsersInfoEntity> findByAddress(String address);
}
