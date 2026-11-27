package io.github.likhitysplit.solo.finance;

import io.github.likhitysplit.solo.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "amount_cents", nullable = false)
    private int amountCents;

    @Column(nullable = false, length = 100)
    private String merchant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ExpenseCategory category;

    @Column(name = "spent_on", nullable = false)
    private LocalDate spentOn;

    @Column(length = 500)
    private String note;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Expense() {}

    public Expense(User user, int amountCents, String merchant, ExpenseCategory category, LocalDate spentOn, String note) {
        this.user = user;
        this.amountCents = amountCents;
        this.merchant = merchant;
        this.category = category;
        this.spentOn = spentOn;
        this.note = note;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public int getAmountCents() {
        return amountCents;
    }

    public String getMerchant() {
        return merchant;
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public LocalDate getSpentOn() {
        return spentOn;
    }

    public String getNote() {
        return note;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
