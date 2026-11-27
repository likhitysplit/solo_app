package io.github.likhitysplit.solo.finance;

import io.github.likhitysplit.solo.common.InvalidInputException;
import io.github.likhitysplit.solo.user.User;
import io.github.likhitysplit.solo.user.UserNotFoundException;
import io.github.likhitysplit.solo.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenses;
    private final UserRepository users;

    public ExpenseService(ExpenseRepository expenses, UserRepository users) {
        this.expenses = expenses;
        this.users = users;
    }

    @Transactional
    public Expense createExpense(Long userId, int amountCents, String merchant, ExpenseCategory category,
                                 LocalDate spentOn, String note) {
        User user = users.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        String cleanMerchant = merchant == null ? "" : merchant.trim();
        if (cleanMerchant.isEmpty()) {
            throw new InvalidInputException("Merchant can't be empty");
        }

        if (spentOn == null) {
            throw new InvalidInputException("Date is required");
        }
        if (spentOn.isAfter(LocalDate.now())) {
            throw new InvalidInputException("The date of the expense can't be in the future");
        }

        String cleanNote = (note == null || note.isBlank()) ? null : note.trim();

        return expenses.save(new Expense(user, amountCents, cleanMerchant, category, spentOn, cleanNote));
    }

    @Transactional(readOnly = true)
    public List<Expense> getRecent(Long userId) {
        return expenses.findTop20ByUserIdOrderBySpentOnDescIdDesc(userId);
    }
}