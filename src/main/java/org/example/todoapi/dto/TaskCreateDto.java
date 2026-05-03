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
    @NotNull
    @Size(min = 1, max = 100)
    private String title;

    private String description;

    @NotNull
    private TaskStatus status;

    @FutureOrPresent
    private LocalDate dueDate;

    @NotNull
    private Long userId;
}
