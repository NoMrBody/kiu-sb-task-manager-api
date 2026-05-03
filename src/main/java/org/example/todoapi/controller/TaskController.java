package org.example.todoapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.example.todoapi.dto.TaskCreateDto;
import org.example.todoapi.dto.TaskGetDto;
import org.example.todoapi.dto.TaskUpdateDto;
import org.example.todoapi.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "Get all tasks")
    @GetMapping
    public ResponseEntity<List<TaskGetDto>> getAllTasks(){
        return ResponseEntity.status(200).body(taskService.getAllTasks());
    }

    @Operation(summary = "Get specifict task with id")
    @GetMapping("{id}")
    public ResponseEntity<TaskGetDto> getTaskById(@PathVariable Long id){
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @Operation(summary = "Create task")
    @PostMapping
    public ResponseEntity<TaskGetDto> createTask(@Valid @RequestBody TaskCreateDto dto){
        return ResponseEntity.status(201).body(taskService.createTask(dto));
    }

    @Operation(summary = "Update specifict task")
    @PutMapping("{id}")
    public ResponseEntity<TaskGetDto> updateTask(@PathVariable Long id, @Valid @RequestBody TaskUpdateDto dto){
        return ResponseEntity.ok(taskService.updateTask(id, dto));
    }

    @Operation(summary = "Delete task with id")
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id){
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    public TaskController(TaskService taskService){
        this.taskService = taskService;
    }

}
