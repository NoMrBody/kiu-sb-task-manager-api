package org.example.todoapi.service;

import lombok.extern.slf4j.Slf4j;
import org.example.todoapi.dto.TaskCreateDto;
import org.example.todoapi.dto.TaskGetDto;
import org.example.todoapi.dto.TaskUpdateDto;
import org.example.todoapi.entity.Task;
import org.example.todoapi.entity.User;
import org.example.todoapi.exception.ApiRequestException;
import org.example.todoapi.repository.TaskRepository;
import org.example.todoapi.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
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
                .orElseThrow(()-> {
                    log.warn("Cannot create task: user {} not found", dto.getUserId());
                    return new ApiRequestException("error.user.notFound", HttpStatus.NOT_FOUND, dto.getUserId());
                });
        Task task = new Task();
        task.setUser(u);
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setTaskStatus(dto.getStatus());
        task.setDueDate(dto.getDueDate());
        Task saved = taskRepository.save(task);
        log.info("Created task {} for user {}", saved.getId(), u.getUsername());
        return mapToDto(saved);
    }

    @Override
    public List<TaskGetDto> getAllTasks() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        log.debug("Fetching tasks for user {} (admin={})", username, isAdmin);
        if (isAdmin) {
            return taskRepository.findAll().stream().map(this::mapToDto).toList();
        } else {
            return taskRepository.findByUserUsername(username).stream().map(this::mapToDto).toList();
        }
    }

    @Override
    public TaskGetDto getTaskById(Long id) {
        log.debug("Fetching task {}", id);
        Task t = taskRepository.findById(id)
                .orElseThrow(()-> {
                    log.warn("Task {} not found", id);
                    return new ApiRequestException("error.task.notFound", HttpStatus.NOT_FOUND, id);
                });
        return mapToDto(t);
    }

    @Override
    public void deleteTask(Long id) {
        taskRepository.findById(id)
                .orElseThrow(()-> {
                    log.warn("Cannot delete task: task {} not found", id);
                    return new ApiRequestException("error.task.notFound", HttpStatus.NOT_FOUND, id);
                });
        taskRepository.deleteById(id);
        log.info("Deleted task {}", id);
    }

    @Override
    public TaskGetDto updateTask(Long id, TaskUpdateDto dto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(()-> {
                    log.warn("Cannot update task: task {} not found", id);
                    return new ApiRequestException("error.task.notFound", HttpStatus.NOT_FOUND, id);
                });
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setTaskStatus(dto.getStatus());
        task.setDueDate(dto.getDueDate());
        Task saved = taskRepository.save(task);
        log.info("Updated task {}", id);
        return mapToDto(saved);
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
