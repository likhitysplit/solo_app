package io.github.likhitysplit.solo.finance.dto;

import io.github.likhitysplit.solo.finance.BudgetPeriod;
import io.github.likhitysplit.solo.finance.BudgetResult;
import io.github.likhitysplit.solo.finance.PeriodMath;

import java.time.LocalDate;

public record BudgetResultResponse(
        BudgetPeriod period,
        LocalDate periodStart,
        LocalDate periodEnd,
        boolean qualified,
        int categoriesMet,
        int categoriesTotal,
        int xpAwarded
) {
    public static BudgetResultResponse from(BudgetResult r) {
        return new BudgetResultResponse(r.getPeriod(), r.getPeriodStart(),
                PeriodMath.endOf(r.getPeriod(), r.getPeriodStart()),
                r.isQualified(), r.getCategoriesMet(), r.getCategoriesTotal(), r.getXpAwarded());
    }
}