package org.example.todoapi.service;

import org.example.todoapi.dto.UserCreateDto;
import org.example.todoapi.dto.UserGetDto;
import org.example.todoapi.dto.UserUpdateDto;
import org.example.todoapi.entity.User;
import org.example.todoapi.exception.ApiRequestException;
import org.example.todoapi.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserGetDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public UserGetDto createUser(UserCreateDto userCreateDto) {
        User user = new User();
        user.setUsername(userCreateDto.getUsername());
        user.setEmail(userCreateDto.getEmail());
        return mapToDto(userRepository.save(user));
    }

    @Override
    public UserGetDto getUserById(Long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new ApiRequestException("User not found with id: " + id, HttpStatus.NOT_FOUND));
        return mapToDto(u);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.findById(id)
                .orElseThrow(()-> new ApiRequestException("User not found with id: "+id, HttpStatus.NOT_FOUND));
        userRepository.deleteById(id);
    }

    @Override
    public UserGetDto updateUser(Long id, UserUpdateDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiRequestException("User not found with id: "+id, HttpStatus.NOT_FOUND));
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());
        return mapToDto(userRepository.save(user));
    }

    private UserGetDto mapToDto(User u) {
        UserGetDto ugdto = new UserGetDto();
        ugdto.setId(u.getId());
        ugdto.setUsername(u.getUsername());
        ugdto.setEmail(u.getEmail());
        return ugdto;
    }

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

}
