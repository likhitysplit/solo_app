package io.github.likhitysplit.solo.finance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findTop20ByUserIdOrderBySpentOnDescIdDesc(Long userId);}
