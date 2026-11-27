package io.github.likhitysplit.solo.log;

import io.github.likhitysplit.solo.stats.Stat;
import io.github.likhitysplit.solo.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "log_entries")
public class LogEntry {

    public static final int MAX_LENGTH = 1000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = MAX_LENGTH)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Stat tag;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected LogEntry() { }

    public LogEntry(User user, String content, Stat tag) {
        this.user = user;
        this.content = content;
        this.tag = tag;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getContent() {
        return content;
    }

    public Stat getTag() {
        return tag;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}