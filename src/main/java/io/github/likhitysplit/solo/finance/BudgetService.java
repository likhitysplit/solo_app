package io.github.likhitysplit.solo.finance;

import io.github.likhitysplit.solo.user.User;
import io.github.likhitysplit.solo.user.UserNotFoundException;
import io.github.likhitysplit.solo.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BudgetService {

    private final BudgetRepository budgets;
    private final ExpenseRepository expenses;
    private final UserRepository users;

    public BudgetService(BudgetRepository budgets, ExpenseRepository expenses, UserRepository users) {
        this.budgets = budgets;
        this.expenses = expenses;
        this.users = users;
    }

    /** Schedules a budget (or a change to one) for the start of the next period. */
    @Transactional
    public Budget setBudget(Long userId, ExpenseCategory category, BudgetPeriod period,
                            int limitCents, LocalDate today) {
        LocalDate effectiveFrom = PeriodMath.nextStart(period, today);

        return budgets.findByUserIdAndCategoryAndEffectiveFrom(userId, category, effectiveFrom)
                .map(existing -> {
                    existing.update(period, limitCents);        // edit the pending change
                    return existing;
                })
                .orElseGet(() -> {
                    User user = users.findById(userId)
                            .orElseThrow(() -> new UserNotFoundException(userId));
                    return budgets.save(new Budget(user, category, period, limitCents, effectiveFrom));
                });
    }

    /** Spending vs. limit for every budget active today. */
    @Transactional(readOnly = true)
    public List<BudgetStatus> getSummary(Long userId, LocalDate today) {
        List<BudgetStatus> result = new ArrayList<>();

        for (ExpenseCategory category : ExpenseCategory.values()) {
            Optional<Budget> active = budgets
                    .findFirstByUserIdAndCategoryAndEffectiveFromLessThanEqualOrderByEffectiveFromDesc(
                            userId, category, today);
            if (active.isEmpty()) {
                continue;
            }

            Budget budget = active.get();
            LocalDate start = PeriodMath.startOf(budget.getPeriod(), today);
            LocalDate end = PeriodMath.endOf(budget.getPeriod(), start);
            long spent = expenses.sumSpent(userId, category, start, end);
            long daysLeft = ChronoUnit.DAYS.between(today, end) + 1;

            result.add(new BudgetStatus(category, budget.getPeriod(), budget.getLimitCents(),
                    spent, start, end, daysLeft));
        }
        return result;
    }
}