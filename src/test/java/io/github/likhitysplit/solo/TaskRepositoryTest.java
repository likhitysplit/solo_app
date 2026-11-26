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

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class TaskRepositoryTest {

    @Autowired private TaskRepository tasks;
    @Autowired private UserRepository users;

    private User user;
    private final LocalDate today = LocalDate.of(2026, 11, 26);

    @BeforeEach
    void setUp() {
        user = users.save(new User("tester", "tester@example.com", "test-hash", "America/New_York"));
    }

    @Test
    void findsTasksForUserOnDate() {
        tasks.save(new Task(user, "finish lab report", TaskType.ANCHOR, Stat.DILIGENCE, 15, today));
        tasks.save(new Task(user, "call the pharmacy", TaskType.ANCHOR, Stat.SOCIABILITY, 10, today));
        tasks.save(new Task(user, "go for a run", TaskType.EXTRA, Stat.FITNESS, 10, today.plusDays(1)));

        List<Task> found = tasks.findByUserIdAndScheduledDate(user.getId(), today);

        assertThat(found).hasSize(2);
    }

    @Test
    void countsAnchorsForDate() {
        tasks.save(new Task(user, "finish lab report", TaskType.ANCHOR, Stat.DILIGENCE, 15, today));
        tasks.save(new Task(user, "call the pharmacy", TaskType.ANCHOR, Stat.SOCIABILITY, 10, today));
        tasks.save(new Task(user, "go for a run", TaskType.EXTRA, Stat.FITNESS, 10, today));

        long anchors = tasks.countByUserIdAndScheduledDateAndType(user.getId(), today, TaskType.ANCHOR);

        assertThat(anchors).isEqualTo(2);
    }

    @Test
    void newTaskStartsOpenWithMatchingDates() {
        Task saved = tasks.save(new Task(user, "finish lab report", TaskType.ANCHOR, Stat.DILIGENCE, 15, today));

        assertThat(saved.getStatus()).isEqualTo(TaskStatus.OPEN);
        assertThat(saved.getCompletedAt()).isNull();
        assertThat(saved.getOriginalDate()).isEqualTo(saved.getScheduledDate());
    }

    @Test
    void completingTwiceThrows() {
        Task task = new Task(user, "finish lab report", TaskType.ANCHOR, Stat.DILIGENCE, 15, today);
        task.complete(Instant.now());

        assertThatThrownBy(() -> task.complete(Instant.now()))
                .isInstanceOf(IllegalStateException.class);
    }
}