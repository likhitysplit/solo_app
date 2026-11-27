package io.github.likhitysplit.solo.focus;

import io.github.likhitysplit.solo.common.InvalidInputException;
import io.github.likhitysplit.solo.stats.StatProgress;
import io.github.likhitysplit.solo.stats.StatService;
import io.github.likhitysplit.solo.task.Task;
import io.github.likhitysplit.solo.task.TaskNotFoundException;
import io.github.likhitysplit.solo.task.TaskRepository;
import io.github.likhitysplit.solo.task.TaskStatus;
import io.github.likhitysplit.solo.user.User;
import io.github.likhitysplit.solo.user.UserNotFoundException;
import io.github.likhitysplit.solo.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class FocusService {

    static final int MIN_MINUTES = 5;
    static final int MAX_MINUTES = 120;

    private final FocusSessionRepository sessions;
    private final TaskRepository tasks;
    private final UserRepository users;
    private final StatService statService;

    public FocusService(FocusSessionRepository sessions, TaskRepository tasks,
                        UserRepository users, StatService statService) {
        this.sessions = sessions;
        this.tasks = tasks;
        this.users = users;
        this.statService = statService;
    }

    @Transactional
    public FocusSession start(Long userId, Long taskId, int plannedMinutes) {
        if (plannedMinutes < MIN_MINUTES || plannedMinutes > MAX_MINUTES) {
            throw new InvalidInputException(
                    "Focus length must be between " + MIN_MINUTES + " and " + MAX_MINUTES + " minutes");
        }
        if (sessions.existsByUserIdAndEndedAtIsNull(userId)) {
            throw new FocusConflictException("You already have a focus session running");
        }

        User user = users.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Task task = null;
        if (taskId != null) {
            task = tasks.findById(taskId)
                    .filter(t -> t.getUser().getId().equals(userId))
                    .orElseThrow(() -> new TaskNotFoundException(taskId));
            if (task.getStatus() == TaskStatus.COMPLETED) {
                throw new InvalidInputException("Can't focus on a task that's already completed");
            }
        }

        return sessions.save(new FocusSession(user, task, plannedMinutes, Instant.now()));
    }

    @Transactional
    public FocusResult finish(Long userId, Long sessionId) {
        FocusSession session = sessions.findById(sessionId)
                .filter(s -> s.getUser().getId().equals(userId))
                .orElseThrow(() -> new FocusSessionNotFoundException(sessionId));

        int minutes = session.finish(Instant.now());
        int xp = FocusSession.xpForMinutes(minutes);

        StatProgress progress = null;
        if (session.getTask() != null && xp > 0) {
            progress = statService.awardXp(userId, session.getTask().getStat(), xp);
            session.recordXp(xp);
        }

        return new FocusResult(session, progress);
    }

    @Transactional(readOnly = true)
    public Optional<FocusSession> getActive(Long userId) {
        return sessions.findByUserIdAndEndedAtIsNull(userId);
    }
}