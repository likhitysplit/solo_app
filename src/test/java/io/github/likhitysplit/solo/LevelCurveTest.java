package io.github.likhitysplit.solo.stats;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LevelCurveTest {

    @Test
    void startsAtLevelOne() {
        assertThat(LevelCurve.levelFor(0)).isEqualTo(1);
    }

    @Test
    void levelsUpExactlyAtThreshold() {
        assertThat(LevelCurve.levelFor(99)).isEqualTo(1);
        assertThat(LevelCurve.levelFor(100)).isEqualTo(2);
    }

    @Test
    void costGrowsEachLevel() {
        assertThat(LevelCurve.levelFor(249)).isEqualTo(2);
        assertThat(LevelCurve.levelFor(250)).isEqualTo(3);
        assertThat(LevelCurve.levelFor(450)).isEqualTo(4);
    }

    @Test
    void tracksProgressWithinLevel() {
        assertThat(LevelCurve.xpIntoLevel(0)).isEqualTo(0);
        assertThat(LevelCurve.xpIntoLevel(300)).isEqualTo(50);
    }
}