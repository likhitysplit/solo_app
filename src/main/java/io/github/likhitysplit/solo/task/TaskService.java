package io.github.likhitysplit.solo.task;

import io.github.likhitysplit.solo.stats.Stat;
import io.github.likhitysplit.solo.user.User;
import io.github.likhitysplit.solo.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class TaskService {

    static final int MAX_ANCHORS_PER_DAY = 3;

    private final TaskRepository tasks;
    private final UserRepository users;

    public TaskService(TaskRepository tasks, UserRepository users) {
        this.tasks = tasks;
        this.users = users;
    }

    @Transactional
    public Task createTask(Long userId, String title, TaskType type,
                           Stat stat, int baseXp, LocalDate date) {

        User user = users.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user " + userId + " not found"));

        if (type == TaskType.ANCHOR) {
            long anchorsToday = tasks.countByUserIdAndScheduledDateAndType(userId, date, TaskType.ANCHOR);
            if (anchorsToday >= MAX_ANCHORS_PER_DAY) {
                throw new AnchorLimitExceededException(userId, date);
            }
        }

        Task task = new Task(user, title, type, stat, baseXp, date);
        return tasks.save(task);
    }
}