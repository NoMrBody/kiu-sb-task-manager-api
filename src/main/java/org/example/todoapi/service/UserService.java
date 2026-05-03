package org.example.todoapi.service;

import org.example.todoapi.dto.UserCreateDto;
import org.example.todoapi.dto.UserGetDto;
import org.example.todoapi.dto.UserUpdateDto;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface UserService {
    List<UserGetDto> getAllUsers();

    UserGetDto createUser(@RequestBody UserCreateDto userCreateDto);

    UserGetDto getUserById(Long id);

    void deleteUser(Long id);

    UserGetDto updateUser(Long id, UserUpdateDto dto);

}
