package org.example.todoapi.controller;

import org.example.todoapi.config.I18nConfig;
import org.example.todoapi.config.SecurityConfig;
import org.example.todoapi.dto.UserGetDto;
import org.example.todoapi.exception.ApiRequestException;
import org.example.todoapi.exception.GlobalExceptionHandler;
import org.example.todoapi.service.CustomUserDetailsService;
import org.example.todoapi.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({GlobalExceptionHandler.class, I18nConfig.class, SecurityConfig.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserById_returns200AndBody() throws Exception {
        UserGetDto dto = buildUserGetDto(1L, "alice", "alice@example.com");

        when(userService.getUserById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserById_returns404_whenUserNotFound() throws Exception {
        when(userService.getUserById(99L))
                .thenThrow(new ApiRequestException("error.user.notFound", HttpStatus.NOT_FOUND, 99L));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found with id: 99"));
    }

    @Test
    @WithMockUser
    void getUserById_returns403_whenNotAdmin() throws Exception {
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Access denied"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_returns200AndList() throws Exception {
        UserGetDto dto = buildUserGetDto(1L, "alice", "alice@example.com");

        when(userService.getAllUsers()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("alice"))
                .andExpect(jsonPath("$[0].email").value("alice@example.com"));
    }

    @Test
    void createUser_returns201() throws Exception {
        UserGetDto dto = buildUserGetDto(1L, "alice", "alice@example.com");

        when(userService.createUser(any())).thenReturn(dto);

        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "alice",
                                  "email": "alice@example.com",
                                  "password": "secret"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    void createUser_returns403_whenRegistrationDisabled() throws Exception {
        when(userService.createUser(any()))
                .thenThrow(new ApiRequestException("error.registration.disabled", HttpStatus.FORBIDDEN));

        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "alice",
                                  "email": "alice@example.com",
                                  "password": "secret"
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("User registration is currently disabled"));
    }

    @Test
    @WithMockUser
    void updateUser_returns200() throws Exception {
        UserGetDto dto = buildUserGetDto(1L, "alice-updated", "alice.updated@example.com");

        when(userService.updateUser(eq(1L), any())).thenReturn(dto);

        mockMvc.perform(put("/api/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "alice-updated",
                                  "email": "alice.updated@example.com"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("alice-updated"))
                .andExpect(jsonPath("$.email").value("alice.updated@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_returns204() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    private UserGetDto buildUserGetDto(Long id, String username, String email) {
        UserGetDto dto = new UserGetDto();
        dto.setId(id);
        dto.setUsername(username);
        dto.setEmail(email);
        return dto;
    }
}
