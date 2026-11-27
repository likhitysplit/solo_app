package io.github.likhitysplit.solo.finance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findTop20ByUserIdOrderBySpentOnDescIdDesc(Long userId);

    @Query("""
            SELECT COALESCE(SUM(e.amountCents), 0)
            FROM Expense e
            WHERE e.user.id = :userId
              AND e.category = :category
              AND e.spentOn BETWEEN :start AND :end
            """)
    long sumSpent(@Param("userId") Long userId,
                  @Param("category") ExpenseCategory category,
                  @Param("start") LocalDate start,
                  @Param("end") LocalDate end);

    long countByUserIdAndSpentOnBetween(Long userId, LocalDate start, LocalDate end);
}
