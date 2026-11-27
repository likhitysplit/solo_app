package io.github.likhitysplit.solo.finance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface BudgetResultRepository extends JpaRepository<BudgetResult, Long> {
    boolean existsByUserIdAndPeriodAndPeriodStart(Long userId, BudgetPeriod period, LocalDate periodStart);
}