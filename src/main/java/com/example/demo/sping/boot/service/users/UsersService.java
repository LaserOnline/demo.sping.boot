package com.example.demo.sping.boot.service.users;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.sping.boot.service.uuid.UuidService;
import com.example.demo.sping.boot.util.dto.RegisterDTO;
import com.example.demo.sping.boot.util.entity.UsersEntity;
import com.example.demo.sping.boot.util.entity.UsersInfoEntity;
import com.example.demo.sping.boot.util.repository.UserRepository;
import com.example.demo.sping.boot.util.repository.UsersInfoRepository;
import com.example.demo.sping.boot.util.response.UsersInfo;

@Service
public class UsersService {
    private final UserRepository userRepository;
    private final UsersInfoRepository usersInfoRepository;
    private final UuidService uuidService;
    
    public UsersService(UserRepository userRepository,UuidService uuidService,UsersInfoRepository usersInfoRepository) {
        this.userRepository = userRepository;
        this.uuidService = uuidService;
        this.usersInfoRepository = usersInfoRepository;
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
        UsersEntity savedUser = userRepository.save(user);
        
        UsersInfoEntity usersInfo = createDefaultUsersInfo(savedUser.getUsersUuid());
        usersInfoRepository.save(usersInfo);
    
        return savedUser;
    }

    private UsersInfoEntity createDefaultUsersInfo(String usersUuid) {
        UsersInfoEntity usersInfo = new UsersInfoEntity();
        usersInfo.setUsersUuid(usersUuid);
        usersInfo.setFirstName("");
        usersInfo.setLastName("");
        usersInfo.setProfile("");
        usersInfo.setAddress("");
        return usersInfo;
    }
    
    public UsersInfo getUserInfoByUuid(String usersUuid) {
        Optional<UsersEntity> userEntityOpt = userRepository.findByUsersUuid(usersUuid);
        Optional<UsersInfoEntity> userInfoOpt = usersInfoRepository.findByUsersUuid(usersUuid);

        UsersInfo response = new UsersInfo();

        userEntityOpt.ifPresentOrElse(usersEntity -> {
            response.setUsername(usersEntity.getUsername());
            response.setEmail(usersEntity.getEmail());
        }, () -> {
            response.setUsername("");
            response.setEmail("");
        });

        userInfoOpt.ifPresentOrElse(usersInfoEntity -> {
            response.setFirstName(usersInfoEntity.getFirstName());
            response.setLastName(usersInfoEntity.getLastName());
            response.setProfile(usersInfoEntity.getProfile());
            response.setAddress(usersInfoEntity.getAddress());
        }, () -> {
            response.setFirstName("");
            response.setLastName("");
            response.setProfile("");
            response.setAddress("");
        });

        return response;
    }

    public boolean updateUserProfileImage(String usersUuid, String filename) {
        Optional<UsersInfoEntity> infoOpt = usersInfoRepository.findByUsersUuid(usersUuid);
    
        if (infoOpt.isPresent()) {
            UsersInfoEntity userInfo = infoOpt.get();
            userInfo.setProfile(filename);
            usersInfoRepository.save(userInfo);
            return true;
        }
    
        return false; // หากไม่พบ usersUuid
    }

    public Optional<String> getProfileImageByUsersUuid(String usersUuid) {
        return usersInfoRepository.findByUsersUuid(usersUuid)
                .map(UsersInfoEntity::getProfile);
    }

    public boolean hasUsersInfo(String usersUuid) {
        return usersInfoRepository.findByUsersUuid(usersUuid).isPresent();
    }

    public Optional<UsersEntity> findByUsersUuid(String usersUuid) {
        return userRepository.findByUsersUuid(usersUuid);
    }

    public Optional<UsersEntity> findByUsername(String username) {
        return userRepository.findByUsername(username.toLowerCase());
    }
}
