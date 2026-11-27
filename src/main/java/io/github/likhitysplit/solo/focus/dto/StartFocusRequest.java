package io.github.likhitysplit.solo.focus.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record StartFocusRequest(
        Long taskId,

        @Min(value = 5, message = "Focus must be at least 5 minutes")
        @Max(value = 120, message = "Focus must be at most 120 minutes")
        int plannedMinutes
) { }