package io.github.likhitysplit.solo.task;

public class AnchorLimitExceededException extends RuntimeException {
    public AnchorLimitExceededException(Long userId, java.time.LocalDate date) {
        super("user " + userId + " already has 3 anchors on " + date);
    }
}