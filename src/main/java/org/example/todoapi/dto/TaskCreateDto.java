package org.example.todoapi.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.todoapi.entity.TaskStatus;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class TaskCreateDto {
    @NotNull(message = "{validation.task.title.notNull}")
    @Size(min = 1, max = 100, message = "{validation.task.title.size}")
    private String title;

    private String description;

    @NotNull(message = "{validation.task.status.notNull}")
    private TaskStatus status;

    @FutureOrPresent(message = "{validation.task.dueDate.futureOrPresent}")
    private LocalDate dueDate;

    @NotNull
    private Long userId;
}
