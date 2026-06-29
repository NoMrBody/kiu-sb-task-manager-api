package org.example.todoapi.service;

import org.example.todoapi.config.AppSettings;
import org.example.todoapi.dto.UserCreateDto;
import org.example.todoapi.dto.UserGetDto;
import org.example.todoapi.dto.UserUpdateDto;
import org.example.todoapi.entity.Role;
import org.example.todoapi.entity.User;
import org.example.todoapi.exception.ApiRequestException;
import org.example.todoapi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AppSettings appSettings;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getUserById_returnsDto_whenUserExists() {
        User user = buildUser(1L, "alice", "alice@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserGetDto result = userService.getUserById(1L);

        assertEquals(1L, result.getId());
        assertEquals("alice", result.getUsername());
        assertEquals("alice@example.com", result.getEmail());
    }

    @Test
    void getUserById_throwsApiRequestException_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ApiRequestException exception = assertThrows(
                ApiRequestException.class,
                () -> userService.getUserById(99L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("error.user.notFound", exception.getMessageKey());
    }

    @Test
    void getAllUsers_returnsMappedDtos() {
        User user = buildUser(1L, "alice", "alice@example.com");

        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserGetDto> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().getId());
        assertEquals("alice", result.getFirst().getUsername());
        assertEquals("alice@example.com", result.getFirst().getEmail());
    }

    @Test
    void createUser_returnsDto_whenRegistrationEnabled() {
        UserCreateDto dto = new UserCreateDto("alice", "alice@example.com", "secret");
        User saved = buildUser(1L, "alice", "alice@example.com");

        when(appSettings.isRegistrationEnabled()).thenReturn(true);
        when(passwordEncoder.encode("secret")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(saved);

        UserGetDto result = userService.createUser(dto);

        assertEquals(1L, result.getId());
        assertEquals("alice", result.getUsername());
        assertEquals("alice@example.com", result.getEmail());
        verify(passwordEncoder).encode("secret");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_throwsApiRequestException_whenRegistrationDisabled() {
        UserCreateDto dto = new UserCreateDto("alice", "alice@example.com", "secret");

        when(appSettings.isRegistrationEnabled()).thenReturn(false);

        ApiRequestException exception = assertThrows(
                ApiRequestException.class,
                () -> userService.createUser(dto)
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("error.registration.disabled", exception.getMessageKey());
    }

    @Test
    void updateUser_returnsDto_whenUserExists() {
        User user = buildUser(1L, "alice", "alice@example.com");
        UserUpdateDto dto = new UserUpdateDto("alice-updated", "alice.updated@example.com");
        User saved = buildUser(1L, "alice-updated", "alice.updated@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(saved);

        UserGetDto result = userService.updateUser(1L, dto);

        assertEquals(1L, result.getId());
        assertEquals("alice-updated", result.getUsername());
        assertEquals("alice.updated@example.com", result.getEmail());
    }

    @Test
    void updateUser_throwsApiRequestException_whenUserNotFound() {
        UserUpdateDto dto = new UserUpdateDto("alice-updated", "alice.updated@example.com");

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ApiRequestException exception = assertThrows(
                ApiRequestException.class,
                () -> userService.updateUser(99L, dto)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("error.user.notFound", exception.getMessageKey());
    }

    @Test
    void deleteUser_deletes_whenUserExists() {
        User user = buildUser(1L, "alice", "alice@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_throwsApiRequestException_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ApiRequestException exception = assertThrows(
                ApiRequestException.class,
                () -> userService.deleteUser(99L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("error.user.notFound", exception.getMessageKey());
        verify(userRepository, never()).deleteById(99L);
    }

    private User buildUser(Long id, String username, String email) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("encoded-password");
        user.setRole(Role.USER);
        return user;
    }
}
