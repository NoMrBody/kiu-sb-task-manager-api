package org.example.todoapi.service;

import org.example.todoapi.dto.TaskCreateDto;
import org.example.todoapi.dto.TaskGetDto;
import org.example.todoapi.dto.TaskUpdateDto;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface TaskService {
    TaskGetDto createTask(@RequestBody TaskCreateDto dto);

    List<TaskGetDto> getAllTasks();

    TaskGetDto getTaskById(Long id);

    void deleteTask(Long id);

    TaskGetDto updateTask(Long id, TaskUpdateDto dto);
}
