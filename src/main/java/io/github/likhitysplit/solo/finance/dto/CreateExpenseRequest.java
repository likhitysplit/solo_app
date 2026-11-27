package io.github.likhitysplit.solo.finance.dto;

import io.github.likhitysplit.solo.finance.ExpenseCategory;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CreateExpenseRequest(
        @Min(value = 1, message = "Amount must be at least 1")
        @Max(value = 10000000, message = "Amount must be at most $100,000")
        int amountCents,

        @NotBlank(message = "Merchant is required")
        @Size(max = 100, message = "Merchant must be at most 100 characters")
        String merchant,

        @NotNull(message = "Category is required")
        ExpenseCategory category,

        @NotNull(message = "Date is required")
        @PastOrPresent(message = "Date can't be in the future")
        LocalDate spentOn,

        @Size(max = 500, message = "Note must be at most 500 characters")
        String note
) { }
