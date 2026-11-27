package io.github.likhitysplit.solo.log;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {
    List<LogEntry> findTop20ByUserIdOrderByCreatedAtDesc(Long userId);
}
