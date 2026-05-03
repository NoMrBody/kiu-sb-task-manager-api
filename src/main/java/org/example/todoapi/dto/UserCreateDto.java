package org.example.todoapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserCreateDto {
    @NotNull
    @Size(min = 3, max = 40)
    private String username;

    @NotNull
    @Email
    private String email;

}