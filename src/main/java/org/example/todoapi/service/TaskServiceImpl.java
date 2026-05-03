package org.example.todoapi.service;

import org.example.todoapi.dto.TaskCreateDto;
import org.example.todoapi.dto.TaskGetDto;
import org.example.todoapi.dto.TaskUpdateDto;
import org.example.todoapi.entity.Task;
import org.example.todoapi.entity.User;
import org.example.todoapi.exception.ApiRequestException;
import org.example.todoapi.repository.TaskRepository;
import org.example.todoapi.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService{

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }


    @Override
    public TaskGetDto createTask(TaskCreateDto dto) {
        User u = userRepository.findById(dto.getUserId())
                .orElseThrow(()-> new ApiRequestException("User not found with id: "+dto.getUserId(), HttpStatus.NOT_FOUND));
        Task task = new Task();
        task.setUser(u);
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setTaskStatus(dto.getStatus());
        task.setDueDate(dto.getDueDate());
        return mapToDto(taskRepository.save(task));
    }

    @Override
    public List<TaskGetDto> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public TaskGetDto getTaskById(Long id) {
        Task t = taskRepository.findById(id)
                .orElseThrow(()-> new ApiRequestException("Task not found with id: "+id, HttpStatus.NOT_FOUND));
        return mapToDto(t);
    }

    @Override
    public void deleteTask(Long id) {
        taskRepository.findById(id)
                .orElseThrow(()-> new ApiRequestException("Task not found with id: "+id, HttpStatus.NOT_FOUND));
        taskRepository.deleteById(id);
    }

    @Override
    public TaskGetDto updateTask(Long id, TaskUpdateDto dto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(()-> new ApiRequestException("Task not found with id: "+id, HttpStatus.NOT_FOUND));
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setTaskStatus(dto.getStatus());
        task.setDueDate(dto.getDueDate());
        return mapToDto(taskRepository.save(task));
    }

    private TaskGetDto mapToDto(Task t){
        TaskGetDto taskGetDto = new TaskGetDto();
        taskGetDto.setTaskId(t.getId());
        taskGetDto.setTitle(t.getTitle());
        taskGetDto.setDescription(t.getDescription());
        taskGetDto.setDueDate(t.getDueDate());
        taskGetDto.setStatus(t.getTaskStatus());
        taskGetDto.setOwnerUsername(t.getUser().getUsername());
        return taskGetDto;
    }

}
