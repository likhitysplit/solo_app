package io.github.likhitysplit.solo.task.dto;

import io.github.likhitysplit.solo.stats.Stat;
import io.github.likhitysplit.solo.task.TaskType;

import java.time.LocalDate;

public record CreateTaskRequest(
        String title,
        TaskType type,
        Stat stat,
        int baseXp,
        LocalDate date
) { }