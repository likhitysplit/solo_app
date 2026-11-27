package io.github.likhitysplit.solo.finance.dto;

import io.github.likhitysplit.solo.finance.Expense;
import io.github.likhitysplit.solo.finance.ExpenseCategory;

import java.time.Instant;
import java.time.LocalDate;

public record ExpenseResponse(
        Long id,
        int amountCents,
        String merchant,
        ExpenseCategory category,
        LocalDate spentOn,
        String note,
        Instant createdAt
) {
    public static ExpenseResponse from(Expense e) {
        return new ExpenseResponse(e.getId(), e.getAmountCents(), e.getMerchant(), e.getCategory(),
                e.getSpentOn(), e.getNote(), e.getCreatedAt());
    }
}