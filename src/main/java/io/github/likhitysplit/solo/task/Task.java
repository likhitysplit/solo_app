package io.github.likhitysplit.solo.task;

import io.github.likhitysplit.solo.stats.Stat;
import io.github.likhitysplit.solo.user.User;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 200)              // CHANGED: removed unique = false (it's the default)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TaskType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Stat stat;

    @Column(name = "base_xp", nullable = false)
    private int baseXp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TaskStatus status;

    @Column(name = "scheduled_date", nullable = false)
    private LocalDate scheduledDate;

    @Column(name = "original_date", nullable = false, updatable = false)
    private LocalDate originalDate;

    @Column(name = "completed_at")
    private Instant completedAt;

    protected Task() { }

    public Task(User user, String title, TaskType type, Stat stat, int baseXp, LocalDate scheduledDate) {
        this.user = user;
        this.title = title;
        this.type = type;
        this.stat = stat;
        this.baseXp = baseXp;
        this.scheduledDate = scheduledDate;
        this.originalDate = scheduledDate;
        this.status = TaskStatus.OPEN;
    }

    public void complete(Instant now) {
        if (this.status == TaskStatus.COMPLETED) {
            throw new IllegalStateException("task " + id + " is already completed");
        }
        this.status = TaskStatus.COMPLETED;
        this.completedAt = now;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getTitle() {
        return title;
    }

    public TaskType getType() {
        return type;
    }

    public Stat getStat() {
        return stat;
    }

    public int getBaseXp() {
        return baseXp;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public LocalDate getOriginalDate() {
        return originalDate;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }
}