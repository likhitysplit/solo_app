package io.github.likhitysplit.solo.stats.dto;

import io.github.likhitysplit.solo.stats.LevelCurve;
import io.github.likhitysplit.solo.stats.Stat;
import io.github.likhitysplit.solo.stats.StatProgress;

public record StatProgressResponse(
        Stat stat,
        int totalXp,
        int level,
        int xpIntoLevel,
        int xpToNextLevel
) {
    public static StatProgressResponse from(StatProgress p) {
        int level = p.level();
        return new StatProgressResponse(
                p.getStat(),
                p.getTotalXp(),
                level,
                LevelCurve.xpIntoLevel(p.getTotalXp()),
                LevelCurve.xpToAdvanceFrom(level)
        );
    }
}
