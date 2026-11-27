package io.github.likhitysplit.solo.task;

import io.github.likhitysplit.solo.stats.Stat;
import io.github.likhitysplit.solo.stats.StatProgress;
import io.github.likhitysplit.solo.stats.StatProgressRepository;
import io.github.likhitysplit.solo.user.User;
import io.github.likhitysplit.solo.user.UserNotFoundException;
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
    @Autowired private StatProgressRepository statProgress;

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
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void completingTaskAwardsXpToItsStat() {
        Task task = taskService.createTask(userId, "run", TaskType.ANCHOR, Stat.FITNESS, 15, today);

        TaskCompletion result = taskService.completeTask(userId, task.getId());

        assertThat(result.task().getStatus()).isEqualTo(TaskStatus.COMPLETED);
        assertThat(result.task().getCompletedAt()).isNotNull();
        assertThat(result.xpGained()).isEqualTo(15);
        assertThat(result.progress().getStat()).isEqualTo(Stat.FITNESS);
        assertThat(result.progress().getTotalXp()).isEqualTo(15);
    }

    @Test
    void completingTwiceDoesNotAwardXpTwice() {
        Task task = taskService.createTask(userId, "run", TaskType.ANCHOR, Stat.FITNESS, 15, today);
        taskService.completeTask(userId, task.getId());

        assertThatThrownBy(() -> taskService.completeTask(userId, task.getId()))
                .isInstanceOf(TaskAlreadyCompletedException.class);

        StatProgress fitness = statProgress.findByUserIdAndStat(userId, Stat.FITNESS).orElseThrow();
        assertThat(fitness.getTotalXp()).isEqualTo(15);
    }

    @Test
    void cannotCompleteAnotherUsersTask() {
        Task task = taskService.createTask(userId, "run", TaskType.ANCHOR, Stat.FITNESS, 15, today);
        User other = users.save(new User("other", "other@example.com", "test-hash", "America/New_York"));

        assertThatThrownBy(() -> taskService.completeTask(other.getId(), task.getId()))
                .isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    void unknownTaskIsRejected() {
        assertThatThrownBy(() -> taskService.completeTask(userId, 999_999L))
                .isInstanceOf(TaskNotFoundException.class);
    }
}