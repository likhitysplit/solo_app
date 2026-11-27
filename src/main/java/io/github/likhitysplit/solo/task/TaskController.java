package io.github.likhitysplit.solo.task;

import io.github.likhitysplit.solo.auth.AppUserPrincipal;
import io.github.likhitysplit.solo.task.dto.CompleteTaskResponse;
import io.github.likhitysplit.solo.task.dto.CreateTaskRequest;
import io.github.likhitysplit.solo.task.dto.TaskResponse;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@AuthenticationPrincipal AppUserPrincipal me,
                               @Valid @RequestBody CreateTaskRequest req) {
        Task task = taskService.createTask(
                me.id(), req.title(), req.type(), req.stat(), req.baseXp(), req.date());
        return TaskResponse.from(task);
    }

    @GetMapping
    public List<TaskResponse> getForDay(@AuthenticationPrincipal AppUserPrincipal me,
                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return taskService.getTasksForDay(me.id(), date)
                .stream()
                .map(TaskResponse::from)
                .toList();
    }

    @PostMapping("/{taskId}/complete")
    public CompleteTaskResponse complete(@AuthenticationPrincipal AppUserPrincipal me,
                                         @PathVariable Long taskId) {
        return CompleteTaskResponse.from(taskService.completeTask(me.id(), taskId));
    }

    @PostMapping("/rollover")
    public RolloverResult rollOver(@AuthenticationPrincipal AppUserPrincipal me) {
        return taskService.rollOver(me.id(), me.today());   // server decides "today"
    }
}