package io.github.likhitysplit.solo.focus;

import io.github.likhitysplit.solo.auth.AppUserPrincipal;
import io.github.likhitysplit.solo.focus.dto.FinishFocusResponse;
import io.github.likhitysplit.solo.focus.dto.FocusSessionResponse;
import io.github.likhitysplit.solo.focus.dto.StartFocusRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/focus")
public class FocusController {

    private final FocusService focusService;

    public FocusController(FocusService focusService) {
        this.focusService = focusService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FocusSessionResponse start(@AuthenticationPrincipal AppUserPrincipal me, @Valid @RequestBody StartFocusRequest req) {
        return FocusSessionResponse.from(focusService.start(me.id(), req.taskId(), req.plannedMinutes()));
    }

    @PostMapping("/{sessionId}/finish")
    public FinishFocusResponse finish(@AuthenticationPrincipal AppUserPrincipal me, @PathVariable Long sessionId) {
        return FinishFocusResponse.from(focusService.finish(me.id(), sessionId));
    }

    @GetMapping("/active")
    public ResponseEntity<FocusSessionResponse> active(@AuthenticationPrincipal AppUserPrincipal me) {
        return focusService.getActive(me.id())
                .map(s -> ResponseEntity.ok(FocusSessionResponse.from(s)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}