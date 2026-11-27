package io.github.likhitysplit.solo.stats;

import io.github.likhitysplit.solo.user.User;
import io.github.likhitysplit.solo.user.UserNotFoundException;
import io.github.likhitysplit.solo.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class StatServiceTest {

    @Autowired private StatService statService;
    @Autowired private StatProgressRepository statProgress;
    @Autowired private UserRepository users;

    private Long userId;

    @BeforeEach
    void setUp() {
        User user = users.save(new User("tester", "tester@example.com", "test-hash", "America/New_York"));
        userId = user.getId();
    }

    @Test
    void firstAwardCreatesProgress() {
        StatProgress progress = statService.awardXp(userId, Stat.FITNESS, 40);

        assertThat(progress.getId()).isNotNull();
        assertThat(progress.getTotalXp()).isEqualTo(40);
        assertThat(progress.level()).isEqualTo(1);
    }

    @Test
    void awardsAccumulate() {
        statService.awardXp(userId, Stat.FITNESS, 60);

        StatProgress progress = statService.awardXp(userId, Stat.FITNESS, 50);

        assertThat(progress.getTotalXp()).isEqualTo(110);
        assertThat(progress.level()).isEqualTo(2);
    }

    @Test
    void statsAreTrackedSeparately() {
        statService.awardXp(userId, Stat.FITNESS, 30);
        statService.awardXp(userId, Stat.DILIGENCE, 20);

        StatProgress fitness = statProgress.findByUserIdAndStat(userId, Stat.FITNESS).orElseThrow();
        StatProgress diligence = statProgress.findByUserIdAndStat(userId, Stat.DILIGENCE).orElseThrow();
        assertThat(fitness.getTotalXp()).isEqualTo(30);
        assertThat(diligence.getTotalXp()).isEqualTo(20);
    }

    @Test
    void rejectsNonPositiveXp() {
        assertThatThrownBy(() -> statService.awardXp(userId, Stat.FITNESS, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void unknownUserIsRejected() {
        assertThatThrownBy(() -> statService.awardXp(999_999L, Stat.FITNESS, 10))
                .isInstanceOf(UserNotFoundException.class);
    }
}