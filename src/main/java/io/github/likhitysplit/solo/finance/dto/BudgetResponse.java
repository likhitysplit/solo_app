package io.github.likhitysplit.solo.finance.dto;

import io.github.likhitysplit.solo.finance.Budget;
import io.github.likhitysplit.solo.finance.BudgetPeriod;
import io.github.likhitysplit.solo.finance.ExpenseCategory;

import java.time.LocalDate;

public record BudgetResponse(ExpenseCategory category, BudgetPeriod period,
                             int limitCents, LocalDate effectiveFrom) {
    public static BudgetResponse from(Budget b) {
        return new BudgetResponse(b.getCategory(), b.getPeriod(), b.getLimitCents(), b.getEffectiveFrom());
    }
}