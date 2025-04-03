package com.example.demo.sping.boot.service.users;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.example.demo.sping.boot.service.uuid.UuidService;
import com.example.demo.sping.boot.util.entity.UsersEntity;
import com.example.demo.sping.boot.util.entity.UsersInfoEntity;
import com.example.demo.sping.boot.util.repository.UserRepository;
import com.example.demo.sping.boot.util.repository.UsersInfoRepository;

public class UsersServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UsersInfoRepository usersInfoRepository;

    @Mock
    private UuidService uuidService;

    @InjectMocks
    private UsersService usersService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // hasUsersInfo_shouldReturnTrue_WhenUserInfoExists 
    //  -> เช็คว่า hasUsersInfo() คืนค่า true หากข้อมูลมี
    @Test
    void hasUsersInfo_shouldReturnTrue_WhenUserInfoExists() {
        // Arrange
        String usersUuid = "test-uuid";
        UsersInfoEntity infoEntity = new UsersInfoEntity();
        when(usersInfoRepository.findByUsersUuid(usersUuid)).thenReturn(Optional.of(infoEntity));

        // Act
        boolean result = usersService.hasUsersInfo(usersUuid);

        // Assert
        assertTrue(result);
    }

    //  hasUsersInfo_shouldReturnFalse_WhenUserInfoDoesNotExist
    // -> เช็คว่า hasUsersInfo() คืน false หากไม่มีข้อมูล
    @Test
    void hasUsersInfo_shouldReturnFalse_WhenUserInfoDoesNotExist() {
        // Arrange
        String usersUuid = "not-found-uuid";
        when(usersInfoRepository.findByUsersUuid(usersUuid)).thenReturn(Optional.empty());

        // Act
        boolean result = usersService.hasUsersInfo(usersUuid);

        // Assert
        assertFalse(result);
    }

    // findByUsersUuid_shouldReturnOptional_WhenUserExists
    // -> เช็คว่า user ถูกพบ
    @Test
    void findByUsersUuid_shouldReturnOptional_WhenUserExists() {
        // Arrange
        String uuid = "found-uuid";
        UsersEntity user = new UsersEntity();
        user.setUsersUuid(uuid);
        when(userRepository.findByUsersUuid(uuid)).thenReturn(Optional.of(user));

        // Act
        Optional<UsersEntity> result = usersService.findByUsersUuid(uuid);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(uuid, result.get().getUsersUuid());
    }
    
    // findByUsersUuid_shouldReturnEmpty_WhenUserNotExists
    // -> เช็คว่า user ไม่ถูกพบ
    @Test
    void findByUsersUuid_shouldReturnEmpty_WhenUserNotExists() {
        // Arrange
        String uuid = "missing-uuid";
        when(userRepository.findByUsersUuid(uuid)).thenReturn(Optional.empty());

        // Act
        Optional<UsersEntity> result = usersService.findByUsersUuid(uuid);

        // Assert
        assertTrue(result.isEmpty());
    }

}