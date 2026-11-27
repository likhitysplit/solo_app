package io.github.likhitysplit.solo.task;

import io.github.likhitysplit.solo.task.dto.CompleteTaskResponse;
import io.github.likhitysplit.solo.task.dto.CreateTaskRequest;
import io.github.likhitysplit.solo.task.dto.TaskResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@PathVariable Long userId,
                               @RequestBody CreateTaskRequest req) {
        Task task = taskService.createTask(
                userId, req.title(), req.type(), req.stat(), req.baseXp(), req.date());
        return TaskResponse.from(task);
    }

    @GetMapping
    public List<TaskResponse> getForDay(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Task> tasks = taskService.getTasksForDay(userId, date);
        List<TaskResponse> result = new ArrayList<>();
        for (Task t : tasks) {
            result.add(TaskResponse.from(t));
        }
        return result;
    }

    @PostMapping("/{taskId}/complete")
    public CompleteTaskResponse complete(@PathVariable Long userId,
                                         @PathVariable Long taskId) {
        return CompleteTaskResponse.from(taskService.completeTask(userId, taskId));
    }

    @PostMapping("/rollover")
    public RolloverResult rollOver(@PathVariable Long userId,
                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return taskService.rollOver(userId, date);
    }
}