package io.github.likhitysplit.solo.focus;

import io.github.likhitysplit.solo.task.Task;
import io.github.likhitysplit.solo.user.User;
import jakarta.persistence.*;

import java.time.Duration;
import java.time.Instant;

@Entity
@Table(name = "focus_sessions")
public class FocusSession {

    public static final int MINUTES_PER_XP = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    @Column(name = "planned_minutes", nullable = false)
    private int plannedMinutes;

    @Column(name = "started_at", nullable = false, updatable = false)
    private Instant startedAt;

    @Column(name = "ended_at")
    private Instant endedAt;

    @Column(name = "xp_awarded", nullable = false)
    private int xpAwarded;

    protected FocusSession() { }

    public FocusSession(User user, Task task, int plannedMinutes, Instant startedAt) {
        this.user = user;
        this.task = task;
        this.plannedMinutes = plannedMinutes;
        this.startedAt = startedAt;
    }

    public boolean isActive() {
        return endedAt == null;
    }

    public int finish(Instant now) {
        if (!isActive()) {
            throw new FocusConflictException("Focus session " + id + " is already finished");
        }
        this.endedAt = now;
        long elapsed = Duration.between(startedAt, now).toMinutes();
        return (int) Math.min(elapsed, plannedMinutes);
    }

    public void recordXp(int xp) {
        this.xpAwarded = xp;
    }

    public static int xpForMinutes(int minutes) {
        return minutes / MINUTES_PER_XP;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Task getTask() {
        return task;
    }

    public int getPlannedMinutes() {
        return plannedMinutes;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getEndedAt() {
        return endedAt;
    }

    public int getXpAwarded() {
        return xpAwarded;
    }
}