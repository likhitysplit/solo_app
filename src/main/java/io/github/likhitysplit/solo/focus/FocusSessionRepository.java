package io.github.likhitysplit.solo.focus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FocusSessionRepository extends JpaRepository<FocusSession, Long> {
    boolean existsByUserIdAndEndedAtIsNull(Long userId);
    Optional<FocusSession> findByUserIdAndEndedAtIsNull(Long userId);
}