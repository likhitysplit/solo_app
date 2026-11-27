package io.github.likhitysplit.solo.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUserIdAndScheduledDate(Long userId, LocalDate scheduledDate);

    long countByUserIdAndScheduledDateAndType(Long userId, LocalDate scheduledDate, TaskType type);

    @Query("""
            SELECT t FROM Task t
            WHERE t.user.id = :userId
              AND t.status = :status
              AND t.scheduledDate < :date
            ORDER BY t.originalDate, t.id
            """)
    List<Task> findUnfinishedBefore(@Param("userId") Long userId,
                                    @Param("status") TaskStatus status,
                                    @Param("date") LocalDate date);
}