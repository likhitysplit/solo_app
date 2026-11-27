package io.github.likhitysplit.solo.finance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Optional<Budget> findByUserIdAndCategoryAndEffectiveFrom(
            Long userId, ExpenseCategory category, LocalDate effectiveFrom);

    /** The version active on a date: newest one that started on or before it. */
    Optional<Budget> findFirstByUserIdAndCategoryAndEffectiveFromLessThanEqualOrderByEffectiveFromDesc(
            Long userId, ExpenseCategory category, LocalDate date);

    /** Earliest budget of a period type (used in 30b). */
    Optional<Budget> findFirstByUserIdAndPeriodOrderByEffectiveFromAsc(Long userId, BudgetPeriod period);
}