package io.github.likhitysplit.solo.log.dto;

import io.github.likhitysplit.solo.log.LogEntry;
import io.github.likhitysplit.solo.stats.Stat;

import java.time.Instant;

public record LogEntryResponse(Long id, String content, Stat tag, Instant createdAt) {
    public static LogEntryResponse from(LogEntry e) {
        return new LogEntryResponse(e.getId(), e.getContent(), e.getTag(), e.getCreatedAt());
    }
}
