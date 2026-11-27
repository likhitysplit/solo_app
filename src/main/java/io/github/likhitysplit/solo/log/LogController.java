package io.github.likhitysplit.solo.log;

import io.github.likhitysplit.solo.log.dto.CreateLogRequest;
import io.github.likhitysplit.solo.log.dto.LogEntryResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/logs")
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LogEntryResponse create(@PathVariable Long userId, @RequestBody CreateLogRequest req) {
        return LogEntryResponse.from(logService.createEntry(userId, req.content(), req.tag()));
    }

    @GetMapping
    public List<LogEntryResponse> recent(@PathVariable Long userId) {
        return logService.getRecent(userId).stream().map(LogEntryResponse::from).toList();
    }
}
