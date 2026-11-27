package io.github.likhitysplit.solo.stats;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StatProgressRepository extends JpaRepository<StatProgress, Long> {
    Optional<StatProgress> findByUserIdAndStat(Long userId, Stat stat);
    List<StatProgress> findByUserId(Long userId);
}
