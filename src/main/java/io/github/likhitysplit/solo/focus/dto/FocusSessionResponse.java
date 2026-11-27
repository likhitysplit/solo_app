package io.github.likhitysplit.solo.focus.dto;

import io.github.likhitysplit.solo.focus.FocusSession;

import java.time.Instant;

public record FocusSessionResponse(
        Long id,
        Long taskId,
        int plannedMinutes,
        Instant startedAt,
        Instant endedAt,
        int xpAwarded
) {
    public static FocusSessionResponse from(FocusSession s) {
        return new FocusSessionResponse(
                s.getId(),
                s.getTask() == null ? null : s.getTask().getId(),
                s.getPlannedMinutes(),
                s.getStartedAt(),
                s.getEndedAt(),
                s.getXpAwarded()
        );
    }
}