package io.github.likhitysplit.solo.stats;

public final class LevelCurve {

    static final int BASE_XP = 100;
    static final int STEP_XP = 50;

    private LevelCurve() { }

    public static int xpToAdvanceFrom(int level) {
        return BASE_XP + STEP_XP * (level - 1);
    }

    public static int levelFor(int totalXp) {
        int level = 1;
        int remaining = totalXp;
        while (remaining >= xpToAdvanceFrom(level)) {
            remaining -= xpToAdvanceFrom(level);
            level++;
        }
        return level;
    }

    public static int xpIntoLevel(int totalXp) {
        int level = 1;
        int remaining = totalXp;
        while (remaining >= xpToAdvanceFrom(level)) {
            remaining -= xpToAdvanceFrom(level);
            level++;
        }
        return remaining;
    }
}