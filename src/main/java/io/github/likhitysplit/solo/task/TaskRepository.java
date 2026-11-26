package io.github.likhitysplit.solo.task;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUserIdAndScheduledDate(Long userId, LocalDate scheduledDate);

    long countByUserIdAndScheduledDateAndType(Long userId, LocalDate scheduledDate, TaskType type);
}