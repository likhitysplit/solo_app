package io.github.likhitysplit.solo.auth;

public class AccountConflictException extends RuntimeException{
    public AccountConflictException(String message) {
        super(message);
    }
}
