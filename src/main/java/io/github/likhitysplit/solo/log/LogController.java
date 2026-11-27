package io.github.likhitysplit.solo.log;

import io.github.likhitysplit.solo.auth.AppUserPrincipal;
import io.github.likhitysplit.solo.log.dto.CreateLogRequest;
import io.github.likhitysplit.solo.log.dto.LogEntryResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LogEntryResponse create(@AuthenticationPrincipal AppUserPrincipal me, @Valid @RequestBody CreateLogRequest req) {
        return LogEntryResponse.from(logService.createEntry(me.id(), req.content(), req.tag()));
    }

    @GetMapping
    public List<LogEntryResponse> recent(@AuthenticationPrincipal AppUserPrincipal me) {
        return logService.getRecent(me.id()).stream().map(LogEntryResponse::from).toList();
    }
}
