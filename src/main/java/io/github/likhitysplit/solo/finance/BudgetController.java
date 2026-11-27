package io.github.likhitysplit.solo.finance;

import io.github.likhitysplit.solo.auth.AppUserPrincipal;
import io.github.likhitysplit.solo.finance.dto.BudgetResponse;
import io.github.likhitysplit.solo.finance.dto.BudgetResultResponse;
import io.github.likhitysplit.solo.finance.dto.SetBudgetRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;
    private final BudgetCloseService budgetCloseService;

    public BudgetController(BudgetService budgetService, BudgetCloseService budgetCloseService) {
        this.budgetService = budgetService;
        this.budgetCloseService = budgetCloseService;
    }

    @PutMapping("/{category}")
    public BudgetResponse set(@AuthenticationPrincipal AppUserPrincipal me,
                              @PathVariable ExpenseCategory category,
                              @Valid @RequestBody SetBudgetRequest req) {
        return BudgetResponse.from(budgetService.setBudget(
                me.id(), category, req.period(), req.limitCents(), me.today()));
    }

    @GetMapping("/summary")
    public List<BudgetStatus> summary(@AuthenticationPrincipal AppUserPrincipal me) {
        return budgetService.getSummary(me.id(), me.today());
    }

    @PostMapping("/close")
    public List<BudgetResultResponse> close(@AuthenticationPrincipal AppUserPrincipal me) {
        return budgetCloseService.closeFinishedPeriods(me.id(), me.today())
                .stream()
                .map(BudgetResultResponse::from)
                .toList();
    }
}