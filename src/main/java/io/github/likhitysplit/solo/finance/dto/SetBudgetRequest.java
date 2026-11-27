package io.github.likhitysplit.solo.finance.dto;

import io.github.likhitysplit.solo.finance.BudgetPeriod;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SetBudgetRequest(
        @NotNull(message = "Period is required")
        BudgetPeriod period,

        @Min(value = 100, message = "Budget must be at least $1")
        @Max(value = 10_000_000, message = "Budget must be at most $100,000")
        int limitCents
) { }