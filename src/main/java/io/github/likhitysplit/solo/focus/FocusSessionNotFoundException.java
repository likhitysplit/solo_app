package io.github.likhitysplit.solo.focus;

public class FocusSessionNotFoundException extends RuntimeException {
    public FocusSessionNotFoundException(Long sessionId) {
        super("Focus session " + sessionId + " not found");
    }
}