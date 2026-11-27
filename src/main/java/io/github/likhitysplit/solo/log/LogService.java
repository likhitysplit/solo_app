package io.github.likhitysplit.solo.log;

import io.github.likhitysplit.solo.common.InvalidInputException;
import io.github.likhitysplit.solo.stats.Stat;
import io.github.likhitysplit.solo.user.User;
import io.github.likhitysplit.solo.user.UserNotFoundException;
import io.github.likhitysplit.solo.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LogService {

    private final LogEntryRepository logs;
    private final UserRepository users;

    public LogService(LogEntryRepository logs, UserRepository users) {
        this.logs = logs;
        this.users = users;
    }

    @Transactional
    public LogEntry createEntry(Long userId, String content, Stat tag) {
        User user = users.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        String trimmed = content == null ? "" : content.trim();
        if (trimmed.isEmpty()) {
            throw new InvalidInputException("Log entry can't be empty");
        }
        if (trimmed.length() > LogEntry.MAX_LENGTH) {
            throw new InvalidInputException("Log entry is too long (max " + LogEntry.MAX_LENGTH + " characters)");
        }

        return logs.save(new LogEntry(user, trimmed, tag));
    }

    @Transactional(readOnly = true)
    public List<LogEntry> getRecent(Long userId) {
        return logs.findTop20ByUserIdOrderByCreatedAtDesc(userId);
    }
}