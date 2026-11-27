package io.github.likhitysplit.solo.finance;

import java.time.LocalDate;

public record BudgetStatus(
        ExpenseCategory category,
        BudgetPeriod period,
        int limitCents,
        long spentCents,
        LocalDate periodStart,
        LocalDate periodEnd,
        long daysLeft
) { }