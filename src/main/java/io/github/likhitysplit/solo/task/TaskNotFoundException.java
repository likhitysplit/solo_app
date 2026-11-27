package io.github.likhitysplit.solo.task;

public class TaskNotFoundException extends RuntimeException{
    public TaskNotFoundException(Long taskId) {
        super("task " + taskId + " not found");
    }
}
