package io.github.likhitysplit.solo.task.dto;

import io.github.likhitysplit.solo.stats.Stat;
import io.github.likhitysplit.solo.task.Task;
import io.github.likhitysplit.solo.task.TaskStatus;
import io.github.likhitysplit.solo.task.TaskType;

import java.time.Instant;
import java.time.LocalDate;

public record TaskResponse(
        Long id,
        String title,
        TaskType type,
        Stat stat,
        int baseXp,
        TaskStatus status,
        LocalDate scheduledDate,
        LocalDate originalDate,
        Instant completedAt
) {
    public static TaskResponse from(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getType(),
                task.getStat(),
                task.getBaseXp(),
                task.getStatus(),
                task.getScheduledDate(),
                task.getOriginalDate(),
                task.getCompletedAt()
        );
    }
}