package io.github.likhitysplit.solo.stats;

import io.github.likhitysplit.solo.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.Fetch;

@Entity
@Table(name = "stat_progress")
public class StatProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Stat stat;

    @Column(name = "total_xp", nullable = false)
    private int totalXp;

    protected StatProgress() { }

    public StatProgress(User user, Stat stat) {
        this.user = user;
        this.stat = stat;
    }

    public void addXp(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("xp amount must be positive, got: " + amount);
        }
        totalXp += amount;
    }

    public int level() {
        return LevelCurve.levelFor(totalXp);
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Stat getStat() {
        return stat;
    }

    public int getTotalXp() {
        return totalXp;
    }
}