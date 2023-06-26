package org.goodstorage.service;

import org.goodstorage.domain.User;
import org.goodstorage.exceptions.AlreadyExistException;
import org.goodstorage.exceptions.NotFoundException;
import org.goodstorage.repository.UserRepository;
import org.goodstorage.util.RandomUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getUsers_ShouldReturnAllUsers() {
        List<User> expectedUsers = Arrays.asList(
                RandomUtil.radnomUser(), RandomUtil.radnomUser()
        );

        when(userRepository.findAll()).thenReturn(expectedUsers);
        List<User> actualUsers = userService.getUsers();

        assertEquals(expectedUsers.size(), actualUsers.size());
        assertEquals(expectedUsers.get(0), actualUsers.get(0));
        assertEquals(expectedUsers.get(1), actualUsers.get(1));
        verify(userRepository).findAll();
    }

    @Test
    void getById_ExistingId_ShouldReturnUser() {
        User expectedUser = RandomUtil.radnomUser();
        String userId = expectedUser.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));
        User actualUser = userService.getById(userId);

        assertEquals(expectedUser, actualUser);
        verify(userRepository).findById(userId);
    }

    @Test
    void getById_NonExistingId_ShouldThrowNotFoundException() {
        String userId = UUID.randomUUID().toString();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getById(userId));
        verify(userRepository).findById(userId);
    }

    @Test
    void create_ExistingUsername_ShouldThrowAlreadyExistException() {
        // Arrange
        UserService.CreateUserRequest request = new UserService.CreateUserRequest("John Doe", "johndoe", "password", "ROLE_USER");

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);

        // Act and Assert
        assertThrows(AlreadyExistException.class, () -> userService.create(request));
        verify(userRepository).existsByUsername(request.getUsername());
        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void update_ExistingUsername_ShouldThrowAlreadyExistException() {
        String userId = UUID.randomUUID().toString();
        UserService.UpdateUserRequest request = new UserService.UpdateUserRequest();
        request.setUsername(RandomUtil.randomString(10));
        request.setPassword(RandomUtil.randomString(10));
        request.setFullName(RandomUtil.randomString(10));

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);

        assertThrows(AlreadyExistException.class, () -> userService.update(userId, request));
        verify(userRepository).existsByUsername(request.getUsername());
        verify(userRepository, never()).findById(userId);
        verify(userRepository, never()).update(any(User.class));
    }

    @Test
    void update_NonExistingUserId_ShouldThrowNotFoundException() {
        String userId = UUID.randomUUID().toString();
        UserService.UpdateUserRequest request = new UserService.UpdateUserRequest();
        request.setUsername(RandomUtil.randomString(10));
        request.setPassword(RandomUtil.randomString(10));
        request.setFullName(RandomUtil.randomString(10));

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NotFoundException.class, () -> userService.update(userId, request));
        verify(userRepository).existsByUsername(request.getUsername());
        verify(userRepository).findById(userId);
        verify(userRepository, never()).update(any(User.class));
    }

    @Test
    void delete_ExistingId_ShouldDeleteUser() {
        String userId = UUID.randomUUID().toString();

        when(userRepository.existsById(userId)).thenReturn(false);
        userService.delete(userId);

        verify(userRepository).existsById(userId);
        verify(userRepository).delete(userId);
    }

    @Test
    void delete_NonExistingId_ShouldThrowNotFoundException() {
        String userId = UUID.randomUUID().toString();

        when(userRepository.existsById(userId)).thenReturn(true);

        assertThrows(NotFoundException.class, () -> userService.delete(userId));
        verify(userRepository).existsById(userId);
        verify(userRepository, never()).delete(userId);
    }
}
