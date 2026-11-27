package io.github.likhitysplit.solo.focus;

import io.github.likhitysplit.solo.focus.dto.FinishFocusResponse;
import io.github.likhitysplit.solo.focus.dto.FocusSessionResponse;
import io.github.likhitysplit.solo.focus.dto.StartFocusRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/{userId}/focus")
public class FocusController {

    private final FocusService focusService;

    public FocusController(FocusService focusService) {
        this.focusService = focusService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FocusSessionResponse start(@PathVariable Long userId, @Valid @RequestBody StartFocusRequest req) {
        return FocusSessionResponse.from(focusService.start(userId, req.taskId(), req.plannedMinutes()));
    }

    @PostMapping("/{sessionId}/finish")
    public FinishFocusResponse finish(@PathVariable Long userId, @PathVariable Long sessionId) {
        return FinishFocusResponse.from(focusService.finish(userId, sessionId));
    }

    @GetMapping("/active")
    public ResponseEntity<FocusSessionResponse> active(@PathVariable Long userId) {
        return focusService.getActive(userId)
                .map(s -> ResponseEntity.ok(FocusSessionResponse.from(s)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}