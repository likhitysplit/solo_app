package io.github.likhitysplit.solo.log.dto;

import io.github.likhitysplit.solo.stats.Stat;

public record CreateLogRequest(String content, Stat tag) { }
