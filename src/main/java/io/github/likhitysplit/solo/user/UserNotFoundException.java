package io.github.likhitysplit.solo.user;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long userId) {
        super("User " + userId + " not found");
    }
}