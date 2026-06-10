package org.example.todoapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserCreateDto {
    @NotNull(message = "{validation.username.notNull}")
    @Size(min = 3, max = 40, message = "{validation.username.size}")
    private String username;

    @NotNull(message = "{validation.email.notNull}")
    @Email(message = "{validation.email.invalid}")
    private String email;

    @NotNull(message = "{validation.password.notNull}")
    private String password;

}