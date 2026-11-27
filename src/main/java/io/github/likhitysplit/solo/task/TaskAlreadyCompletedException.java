package io.github.likhitysplit.solo.task;

public class TaskAlreadyCompletedException extends IllegalStateException{
    public TaskAlreadyCompletedException(Long taskId) {
        super("task " + taskId + " is already completed");
    }
}
