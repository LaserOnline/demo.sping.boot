package com.example.demo.sping.boot.service.users;

import org.springframework.stereotype.Service;

import com.example.demo.sping.boot.util.repository.UserRepository;

@Service
public class UserValidationService {
    private final UserRepository userRepository;

    public UserValidationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public  boolean  haveEmailExists(String email) {
        return !userRepository.findByEmail(email.toLowerCase()).isEmpty();
    }

    public boolean haveUsernameExists(String username) {
        return !userRepository.findByUsername(username.toLowerCase()).isEmpty();
    }
}
