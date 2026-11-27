package io.github.likhitysplit.solo.finance;

import io.github.likhitysplit.solo.stats.Stat;
import io.github.likhitysplit.solo.stats.StatService;
import io.github.likhitysplit.solo.user.User;
import io.github.likhitysplit.solo.user.UserNotFoundException;
import io.github.likhitysplit.solo.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BudgetCloseService {

    static final int MAX_LOOKBACK_PERIODS = 12;      // never evaluate more than ~3 months of weeks / a year of months

    private final BudgetRepository budgets;
    private final BudgetResultRepository results;
    private final ExpenseRepository expenses;
    private final UserRepository users;
    private final StatService statService;

    public BudgetCloseService(BudgetRepository budgets, BudgetResultRepository results,
                              ExpenseRepository expenses, UserRepository users, StatService statService) {
        this.budgets = budgets;
        this.results = results;
        this.expenses = expenses;
        this.users = users;
        this.statService = statService;
    }

    /** Evaluates every finished period that hasn't been evaluated yet. Safe to call repeatedly. */
    @Transactional
    public List<BudgetResult> closeFinishedPeriods(Long userId, LocalDate today) {
        User user = users.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        List<BudgetResult> closed = new ArrayList<>();

        for (BudgetPeriod period : BudgetPeriod.values()) {
            Optional<Budget> earliest = budgets.findFirstByUserIdAndPeriodOrderByEffectiveFromAsc(userId, period);
            if (earliest.isEmpty()) {
                continue;                                             // no budgets of this type
            }

            LocalDate currentStart = PeriodMath.startOf(period, today);
            LocalDate oldestAllowed = PeriodMath.minusPeriods(period, currentStart, MAX_LOOKBACK_PERIODS);
            LocalDate start = earliest.get().getEffectiveFrom().isAfter(oldestAllowed)
                    ? earliest.get().getEffectiveFrom()
                    : oldestAllowed;

            while (start.isBefore(currentStart)) {                    // only FINISHED periods
                if (!results.existsByUserIdAndPeriodAndPeriodStart(userId, period, start)) {
                    closed.add(evaluate(user, period, start));
                }
                start = PeriodMath.nextStart(period, start);
            }
        }
        return closed;
    }

    private BudgetResult evaluate(User user, BudgetPeriod period, LocalDate start) {
        Long userId = user.getId();
        LocalDate end = PeriodMath.endOf(period, start);

        int total = 0;
        int met = 0;
        for (ExpenseCategory category : ExpenseCategory.values()) {
            Optional<Budget> active = budgets
                    .findFirstByUserIdAndCategoryAndEffectiveFromLessThanEqualOrderByEffectiveFromDesc(
                            userId, category, start);
            if (active.isEmpty() || active.get().getPeriod() != period) {
                continue;                                             // no budget of THIS type for the category
            }
            total++;
            long spent = expenses.sumSpent(userId, category, start, end);
            if (spent <= active.get().getLimitCents()) {
                met++;
            }
        }

        long logged = expenses.countByUserIdAndSpentOnBetween(userId, start, end);
        boolean qualified = total > 0 && logged >= minExpenses(period);

        int xp = 0;
        if (qualified) {
            xp = met * xpPerCategory(period);
            if (met == total) {
                xp += allMetBonus(period);
            }
        }
        if (xp > 0) {
            statService.awardXp(userId, Stat.DILIGENCE, xp);
        }

        return results.save(new BudgetResult(user, period, start, qualified, met, total, xp));
    }

    static int minExpenses(BudgetPeriod period) {
        return switch (period) {
            case WEEKLY -> 2;
            case MONTHLY -> 5;
        };
    }

    static int xpPerCategory(BudgetPeriod period) {
        return switch (period) {
            case WEEKLY -> 5;
            case MONTHLY -> 15;
        };
    }

    static int allMetBonus(BudgetPeriod period) {
        return switch (period) {
            case WEEKLY -> 10;
            case MONTHLY -> 25;
        };
    }
}