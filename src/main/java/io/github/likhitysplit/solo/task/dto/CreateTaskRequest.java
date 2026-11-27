package io.github.likhitysplit.solo.task.dto;

import io.github.likhitysplit.solo.stats.Stat;
import io.github.likhitysplit.solo.task.TaskType;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CreateTaskRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title must be at most 200 characters")
        String title,

        @NotNull(message = "Type is required")
        TaskType type,

        @NotNull(message = "Stat is required")
        Stat stat,

        @Min(value = 1, message = "XP must be at least 1")
        @Max(value = 20, message = "XP must be at most 20")
        int baseXp,

        @NotNull(message = "Date is required")
        LocalDate date
) { }