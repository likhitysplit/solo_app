package io.github.likhitysplit.solo.finance;

import io.github.likhitysplit.solo.finance.dto.CreateExpenseRequest;
import io.github.likhitysplit.solo.finance.dto.ExpenseResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseResponse create(@PathVariable Long userId, @Valid @RequestBody CreateExpenseRequest req) {
        return ExpenseResponse.from(expenseService.createExpense(userId, req.amountCents(),
                req.merchant(), req.category(), req.spentOn(), req.note()));
    }

    @GetMapping
    public List<ExpenseResponse> recent(@PathVariable Long userId) {
        return expenseService.getRecent(userId).stream().map(ExpenseResponse::from).toList();
    }
}
