package io.github.likhitysplit.solo.focus.dto;

import io.github.likhitysplit.solo.focus.FocusResult;
import io.github.likhitysplit.solo.stats.dto.StatProgressResponse;

public record FinishFocusResponse(FocusSessionResponse session, StatProgressResponse stat) {
    public static FinishFocusResponse from(FocusResult r) {
        return new FinishFocusResponse(
                FocusSessionResponse.from(r.session()),
                r.progress() == null ? null : StatProgressResponse.from(r.progress())
        );
    }
}