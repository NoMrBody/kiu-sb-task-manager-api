package org.example.todoapi.service;

import org.example.todoapi.dto.TaskGetDto;
import org.example.todoapi.entity.Task;
import org.example.todoapi.entity.TaskStatus;
import org.example.todoapi.entity.User;
import org.example.todoapi.exception.ApiRequestException;
import org.example.todoapi.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    @Test
    void getTaskById_returnsDto_whenTaskExists() {
        Task task = buildTask(1L, "alice", "Write tests", TaskStatus.TODO, LocalDate.now());

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        TaskGetDto result = taskService.getTaskById(1L);

        assertEquals(1L, result.getTaskId());
        assertEquals("Write tests", result.getTitle());
        assertEquals("Test description", result.getDescription());
        assertEquals(TaskStatus.TODO, result.getStatus());
        assertEquals(task.getDueDate(), result.getDueDate());
        assertEquals("alice", result.getOwnerUsername());
    }

    @Test
    void getTaskById_throwsApiRequestException_whenTaskNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        ApiRequestException exception = assertThrows(
                ApiRequestException.class,
                () -> taskService.getTaskById(99L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("error.task.notFound", exception.getMessageKey());
    }

    private Task buildTask(Long id, String username, String title, TaskStatus status, LocalDate dueDate) {
        User user = new User();
        user.setId(1L);
        user.setUsername(username);

        Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        task.setDescription("Test description");
        task.setTaskStatus(status);
        task.setDueDate(dueDate);
        task.setUser(user);
        return task;
    }
}
