package com.example.demo.sping.boot.service.users;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.sping.boot.service.uuid.UuidService;
import com.example.demo.sping.boot.util.dto.RegisterDTO;
import com.example.demo.sping.boot.util.entity.UsersEntity;
import com.example.demo.sping.boot.util.repository.UserRepository;

@Service
public class UsersService {
    private final UserRepository userRepository;
    private final UuidService uuidService;
    
    public UsersService(UserRepository userRepository,UuidService uuidService) {
        this.userRepository = userRepository;
        this.uuidService = uuidService;
    }

    public  UsersEntity mapToUsersEntity(RegisterDTO dto) {
        UsersEntity entity = new UsersEntity();
        entity.setUsersUuid(uuidService.generateUuid());
        entity.setUsername(dto.getUsername().toLowerCase());
        entity.setEmail(dto.getEmail().toLowerCase());
        entity.setPassword(dto.getPassword());
        return entity;
    } 

    public UsersEntity saveUser(UsersEntity user) {
        return userRepository.save(user);
    }

    public Optional<UsersEntity> findByUsername(String username) {
        return userRepository.findByUsername(username.toLowerCase());
    }
}
