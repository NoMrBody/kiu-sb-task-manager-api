package org.example.todoapi.dto;

import lombok.Data;
import org.example.todoapi.entity.TaskStatus;

import java.time.LocalDate;

@Data
public class TaskGetDto {
    private Long taskId;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDate dueDate;
    private String ownerUsername;
}
