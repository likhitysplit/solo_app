package io.github.likhitysplit.solo.finance;

import io.github.likhitysplit.solo.auth.AppUserPrincipal;
import io.github.likhitysplit.solo.finance.dto.CreateExpenseRequest;
import io.github.likhitysplit.solo.finance.dto.ExpenseResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseResponse create(@AuthenticationPrincipal AppUserPrincipal me, @Valid @RequestBody CreateExpenseRequest req) {
        return ExpenseResponse.from(expenseService.createExpense(me.id(), req.amountCents(),
                req.merchant(), req.category(), req.spentOn(), req.note()));
    }

    @GetMapping
    public List<ExpenseResponse> recent(@AuthenticationPrincipal AppUserPrincipal me) {
        return expenseService.getRecent(me.id()).stream().map(ExpenseResponse::from).toList();
    }
}
