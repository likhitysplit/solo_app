package io.github.likhitysplit.solo.task.dto;

import io.github.likhitysplit.solo.stats.dto.StatProgressResponse;
import io.github.likhitysplit.solo.task.TaskCompletion;

public record CompleteTaskResponse(
        TaskResponse task,
        StatProgressResponse stat,
        int xpGained
) {
    public static CompleteTaskResponse from(TaskCompletion c) {
        return new CompleteTaskResponse(
                TaskResponse.from(c.task()),
                StatProgressResponse.from(c.progress()),
                c.xpGained()
        );
    }
}