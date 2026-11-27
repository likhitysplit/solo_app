package io.github.likhitysplit.solo.task;

import io.github.likhitysplit.solo.stats.StatProgress;

public record TaskCompletion(Task task, StatProgress progress, int xpGained) { }