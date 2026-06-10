package org.example.todoapi.service;

import lombok.extern.slf4j.Slf4j;
import org.example.todoapi.config.AppSettings;
import org.example.todoapi.dto.UserCreateDto;
import org.example.todoapi.dto.UserGetDto;
import org.example.todoapi.dto.UserUpdateDto;
import org.example.todoapi.entity.Role;
import org.example.todoapi.entity.User;
import org.example.todoapi.exception.ApiRequestException;
import org.example.todoapi.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppSettings appSettings;

    @Override
    public List<UserGetDto> getAllUsers() {
        log.debug("Fetching all users");
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public UserGetDto createUser(UserCreateDto userCreateDto) {
        if (!appSettings.isRegistrationEnabled()) {
            log.warn("Registration attempt for username {} blocked: registration disabled", userCreateDto.getUsername());
            throw new ApiRequestException("error.registration.disabled", HttpStatus.FORBIDDEN);
        }
        User user = new User();
        user.setUsername(userCreateDto.getUsername());
        user.setPassword(passwordEncoder.encode(userCreateDto.getPassword()));
        user.setEmail(userCreateDto.getEmail());
        user.setRole(Role.USER);
        User saved = userRepository.save(user);
        log.info("Created user {} with id {}", saved.getUsername(), saved.getId());
        return mapToDto(saved);
    }

    @Override
    public UserGetDto getUserById(Long id) {
        log.debug("Fetching user {}", id);
        User u = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User {} not found", id);
                    return new ApiRequestException("error.user.notFound", HttpStatus.NOT_FOUND, id);
                });
        return mapToDto(u);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.findById(id)
                .orElseThrow(()-> {
                    log.warn("Cannot delete user: user {} not found", id);
                    return new ApiRequestException("error.user.notFound", HttpStatus.NOT_FOUND, id);
                });
        userRepository.deleteById(id);
        log.info("Deleted user {}", id);
    }

    @Override
    public UserGetDto updateUser(Long id, UserUpdateDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cannot update user: user {} not found", id);
                    return new ApiRequestException("error.user.notFound", HttpStatus.NOT_FOUND, id);
                });
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());
        User saved = userRepository.save(user);
        log.info("Updated user {}", id);
        return mapToDto(saved);
    }

    private UserGetDto mapToDto(User u) {
        UserGetDto ugdto = new UserGetDto();
        ugdto.setId(u.getId());
        ugdto.setUsername(u.getUsername());
        ugdto.setEmail(u.getEmail());
        return ugdto;
    }

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, AppSettings appSettings) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.appSettings = appSettings;
    }

}
