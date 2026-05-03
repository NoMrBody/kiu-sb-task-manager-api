package org.example.todoapi.dto;

import lombok.Data;

@Data
public class UserGetDto {
    private Long id;
    private String username;
    private String email;
}
