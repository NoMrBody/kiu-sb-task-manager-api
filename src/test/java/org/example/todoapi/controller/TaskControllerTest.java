package org.example.todoapi.controller;

import org.example.todoapi.config.I18nConfig;
import org.example.todoapi.dto.TaskCreateDto;
import org.example.todoapi.dto.TaskGetDto;
import org.example.todoapi.entity.TaskStatus;
import org.example.todoapi.exception.ApiRequestException;
import org.example.todoapi.exception.GlobalExceptionHandler;
import org.example.todoapi.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@Import({GlobalExceptionHandler.class, I18nConfig.class})
@WithMockUser
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Test
    void getTaskById_returns200AndBody() throws Exception {
        TaskGetDto dto = buildTaskGetDto(1L, "Write tests", TaskStatus.TODO, "alice");

        when(taskService.getTaskById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").value(1))
                .andExpect(jsonPath("$.title").value("Write tests"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.ownerUsername").value("alice"));
    }

    @Test
    void getTaskById_returns404_whenTaskNotFound() throws Exception {
        when(taskService.getTaskById(99L))
                .thenThrow(new ApiRequestException("error.task.notFound", HttpStatus.NOT_FOUND, 99L));

        mockMvc.perform(get("/api/tasks/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Task not found with id: 99"));
    }

    @Test
    void getAllTasks_returns200AndList() throws Exception {
        TaskGetDto dto = buildTaskGetDto(1L, "Write tests", TaskStatus.TODO, "alice");

        when(taskService.getAllTasks()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].taskId").value(1))
                .andExpect(jsonPath("$[0].title").value("Write tests"));
    }

    @Test
    void createTask_returns201() throws Exception {
        TaskGetDto dto = buildTaskGetDto(1L, "New task", TaskStatus.TODO, "alice");

        when(taskService.createTask(any(TaskCreateDto.class))).thenReturn(dto);

        mockMvc.perform(post("/api/tasks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "New task",
                                  "description": "Description",
                                  "status": "TODO",
                                  "dueDate": "%s",
                                  "userId": 1
                                }
                                """.formatted(LocalDate.now())))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.taskId").value(1))
                .andExpect(jsonPath("$.title").value("New task"));
    }

    @Test
    void deleteTask_returns204() throws Exception {
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/api/tasks/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    private TaskGetDto buildTaskGetDto(Long id, String title, TaskStatus status, String ownerUsername) {
        TaskGetDto dto = new TaskGetDto();
        dto.setTaskId(id);
        dto.setTitle(title);
        dto.setDescription("Description");
        dto.setStatus(status);
        dto.setDueDate(LocalDate.now());
        dto.setOwnerUsername(ownerUsername);
        return dto;
    }
}
