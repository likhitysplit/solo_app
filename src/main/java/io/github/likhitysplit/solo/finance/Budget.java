package io.github.likhitysplit.solo.finance;

import io.github.likhitysplit.solo.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ExpenseCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private BudgetPeriod period;

    @Column(name = "limit_cents", nullable = false)
    private int limitCents;

    @Column(name = "effective_from", nullable = false, updatable = false)
    private LocalDate effectiveFrom;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Budget() { }

    public Budget(User user, ExpenseCategory category, BudgetPeriod period,
                  int limitCents, LocalDate effectiveFrom) {
        this.user = user;
        this.category = category;
        this.period = period;
        this.limitCents = limitCents;
        this.effectiveFrom = effectiveFrom;
    }

    public void update(BudgetPeriod period, int limitCents) {
        if (limitCents < 100 || limitCents > 10_000_000) {
            throw new IllegalArgumentException("Budget must be between $1 and $100,000");
        }
        this.period = period;
        this.limitCents = limitCents;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public BudgetPeriod getPeriod() {
        return period;
    }

    public int getLimitCents() {
        return limitCents;
    }

    public LocalDate getEffectiveFrom() {
        return effectiveFrom;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}