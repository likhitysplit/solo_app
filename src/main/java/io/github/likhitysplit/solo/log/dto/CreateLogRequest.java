package io.github.likhitysplit.solo.log.dto;

import io.github.likhitysplit.solo.stats.Stat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateLogRequest(
        @NotBlank(message = "Log entry can't be empty")
        @Size(max = 1000, message = "Log entry must be at most 1000 characters")
        String content,

        Stat tag
) { }