package io.github.likhitysplit.solo.finance;

import io.github.likhitysplit.solo.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "budget_results")
public class BudgetResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private BudgetPeriod period;

    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    @Column(nullable = false)
    private boolean qualified;

    @Column(name = "categories_met", nullable = false)
    private int categoriesMet;

    @Column(name = "categories_total", nullable = false)
    private int categoriesTotal;

    @Column(name = "xp_awarded", nullable = false)
    private int xpAwarded;

    @CreationTimestamp
    @Column(name = "evaluated_at", nullable = false, updatable = false)
    private Instant evaluatedAt;

    protected BudgetResult() { }

    public BudgetResult(User user, BudgetPeriod period, LocalDate periodStart, boolean qualified,
                        int categoriesMet, int categoriesTotal, int xpAwarded) {
        this.user = user;
        this.period = period;
        this.periodStart = periodStart;
        this.qualified = qualified;
        this.categoriesMet = categoriesMet;
        this.categoriesTotal = categoriesTotal;
        this.xpAwarded = xpAwarded;
    }

    public Long getId() { return id; }
    public BudgetPeriod getPeriod() { return period; }
    public LocalDate getPeriodStart() { return periodStart; }
    public boolean isQualified() { return qualified; }
    public int getCategoriesMet() { return categoriesMet; }
    public int getCategoriesTotal() { return categoriesTotal; }
    public int getXpAwarded() { return xpAwarded; }
    public Instant getEvaluatedAt() { return evaluatedAt; }
}