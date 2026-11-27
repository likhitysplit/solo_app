package io.github.likhitysplit.solo.finance;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public final class PeriodMath {

    private PeriodMath() { }

    public static LocalDate startOf(BudgetPeriod period, LocalDate date) {
        return switch (period) {
            case WEEKLY -> date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            case MONTHLY -> date.withDayOfMonth(1);
        };
    }

    public static LocalDate endOf(BudgetPeriod period, LocalDate start) {
        return switch (period) {
            case WEEKLY -> start.plusWeeks(1).minusDays(1);
            case MONTHLY -> start.plusMonths(1).minusDays(1);
        };
    }

    public static LocalDate nextStart(BudgetPeriod period, LocalDate date) {
        LocalDate start = startOf(period, date);
        return switch (period) {
            case WEEKLY -> start.plusWeeks(1);
            case MONTHLY -> start.plusMonths(1);
        };
    }

    public static LocalDate minusPeriods(BudgetPeriod period, LocalDate start, int count) {
        return switch (period) {
            case WEEKLY -> start.minusWeeks(count);
            case MONTHLY -> start.minusMonths(count);
        };
    }
}