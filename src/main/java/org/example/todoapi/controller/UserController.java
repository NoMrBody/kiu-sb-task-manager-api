package org.example.todoapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.example.todoapi.dto.UserCreateDto;
import org.example.todoapi.dto.UserGetDto;
import org.example.todoapi.dto.UserUpdateDto;
import org.example.todoapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get all users")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserGetDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "Get user with specific id")
    @GetMapping("/{id}")
    public ResponseEntity<UserGetDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(summary = "Create user")
    @PostMapping
    public ResponseEntity<UserGetDto> createUser(@Valid @RequestBody UserCreateDto dto) {
        return ResponseEntity.status(201).body(userService.createUser(dto));
    }

    @Operation(summary = "Update user with id")
    @PutMapping("/{id}")
    public ResponseEntity<UserGetDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDto dto){
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    @Operation(summary = "Delete specific user")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    public UserController(UserService userService) {
        this.userService = userService;
    }


}

