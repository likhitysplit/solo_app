package io.github.likhitysplit.solo.task;

import io.github.likhitysplit.solo.stats.Stat;
import io.github.likhitysplit.solo.stats.StatProgress;
import io.github.likhitysplit.solo.stats.StatService;
import io.github.likhitysplit.solo.user.User;
import io.github.likhitysplit.solo.user.UserNotFoundException;
import io.github.likhitysplit.solo.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {

    static final int MAX_ANCHORS_PER_DAY = 3;

    private final TaskRepository tasks;
    private final UserRepository users;
    private final StatService statService;

    public TaskService(TaskRepository tasks, UserRepository users, StatService statService) {
        this.tasks = tasks;
        this.users = users;
        this.statService = statService;
    }

    @Transactional
    public Task createTask(Long userId, String title, TaskType type,
                           Stat stat, int baseXp, LocalDate date) {

        User user = users.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (type == TaskType.ANCHOR) {
            long anchorsToday = tasks.countByUserIdAndScheduledDateAndType(userId, date, TaskType.ANCHOR);
            if (anchorsToday >= MAX_ANCHORS_PER_DAY) {
                throw new AnchorLimitExceededException(userId, date);
            }
        }

        Task task = new Task(user, title, type, stat, baseXp, date);
        return tasks.save(task);
    }

    @Transactional(readOnly = true)
    public List<Task> getTasksForDay(Long userId, LocalDate date) {
        return tasks.findByUserIdAndScheduledDate(userId, date);
    }

    @Transactional
    public TaskCompletion completeTask(Long userId, Long taskId) {
        Task task = tasks.findById(taskId)
                .filter(t -> t.getUser().getId().equals(userId))
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        task.complete(Instant.now());
        int xp = task.getBaseXp();
        StatProgress progress = statService.awardXp(userId, task.getStat(), xp);

        return new TaskCompletion(task, progress, xp);
    }
}