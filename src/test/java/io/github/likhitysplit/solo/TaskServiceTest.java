package io.github.likhitysplit.solo.task;

import io.github.likhitysplit.solo.stats.Stat;
import io.github.likhitysplit.solo.user.User;
import io.github.likhitysplit.solo.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class TaskServiceTest {

    @Autowired private TaskService taskService;
    @Autowired private UserRepository users;

    private Long userId;
    private final LocalDate today = LocalDate.of(2026, 11, 26);

    @BeforeEach
    void setUp() {
        User user = users.save(new User("tester", "tester@example.com", "test-hash", "America/New_York"));
        userId = user.getId();
    }

    private Task createAnchor(LocalDate date) {
        return taskService.createTask(userId, "anchor", TaskType.ANCHOR, Stat.DILIGENCE, 10, date);
    }

    @Test
    void allowsUpToThreeAnchors() {
        createAnchor(today);
        createAnchor(today);

        Task third = createAnchor(today);

        assertThat(third.getId()).isNotNull();
    }

    @Test
    void rejectsFourthAnchor() {
        createAnchor(today);
        createAnchor(today);
        createAnchor(today);

        assertThatThrownBy(() -> createAnchor(today))
                .isInstanceOf(AnchorLimitExceededException.class);
    }

    @Test
    void extrasAreNotLimited() {
        createAnchor(today);
        createAnchor(today);
        createAnchor(today);

        Task extra = taskService.createTask(userId, "extra", TaskType.EXTRA, Stat.FITNESS, 10, today);

        assertThat(extra.getId()).isNotNull();
        assertThat(extra.getType()).isEqualTo(TaskType.EXTRA);
    }

    @Test
    void limitIsPerDay() {
        createAnchor(today);
        createAnchor(today);
        createAnchor(today);

        Task tomorrowAnchor = createAnchor(today.plusDays(1));

        assertThat(tomorrowAnchor.getId()).isNotNull();
        assertThat(tomorrowAnchor.getScheduledDate()).isEqualTo(today.plusDays(1));
    }

    @Test
    void unknownUserIsRejected() {
        assertThatThrownBy(() ->
                taskService.createTask(999_999L, "ghost", TaskType.ANCHOR, Stat.DILIGENCE, 10, today))
                .isInstanceOf(IllegalArgumentException.class);
    }
}