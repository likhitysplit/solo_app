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
    public static StatProgressResponse of(Stat stat, int totalXp) {
        int level = LevelCurve.levelFor(totalXp);
        return new StatProgressResponse(
                stat,
                totalXp,
                level,
                LevelCurve.xpIntoLevel(totalXp),
                LevelCurve.xpToAdvanceFrom(level)
        );
    }

    public static StatProgressResponse from(StatProgress p) {
        return of(p.getStat(), p.getTotalXp());
    }
}
