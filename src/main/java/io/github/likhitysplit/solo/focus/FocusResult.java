package io.github.likhitysplit.solo.focus;

import io.github.likhitysplit.solo.stats.StatProgress;

public record FocusResult(FocusSession session, StatProgress progress) { }