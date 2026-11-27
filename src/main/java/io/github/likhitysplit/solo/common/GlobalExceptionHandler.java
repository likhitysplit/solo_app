package io.github.likhitysplit.solo.common;

import io.github.likhitysplit.solo.auth.AccountConflictException;
import io.github.likhitysplit.solo.focus.FocusConflictException;
import io.github.likhitysplit.solo.focus.FocusSessionNotFoundException;
import io.github.likhitysplit.solo.task.AnchorLimitExceededException;
import io.github.likhitysplit.solo.task.TaskAlreadyCompletedException;
import io.github.likhitysplit.solo.task.TaskNotFoundException;
import io.github.likhitysplit.solo.user.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AnchorLimitExceededException.class)
    public ProblemDetail handleAnchorLimit(AnchorLimitExceededException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("anchor limit reached");
        return problem;
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFound(UserNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("user not found");
        return problem;
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ProblemDetail handleTaskNotFound(TaskNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("task not found");
        return problem;
    }

    @ExceptionHandler(TaskAlreadyCompletedException.class)
    public ProblemDetail handleTaskAlreadyCompleted(TaskAlreadyCompletedException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("task already completed");
        return problem;
    }

    @ExceptionHandler(InvalidInputException.class)
    public ProblemDetail handleInvalidInput(InvalidInputException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("invalid input");
        return problem;
    }

    @ExceptionHandler(FocusConflictException.class)
    public ProblemDetail handleFocusConflict(FocusConflictException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Focus conflict");
        return problem;
    }

    @ExceptionHandler(FocusSessionNotFoundException.class)
    public ProblemDetail handleFocusNotFound(FocusSessionNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Focus session not found");
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(fe.getField(), fe.getDefaultMessage());
        }

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, String.join(" · ", errors.values()));
        problem.setTitle("Invalid input");
        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthentication(AuthenticationException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problem.setTitle("Invalid email or password");
        return problem;
    }

    @ExceptionHandler(AccountConflictException.class)
    public ProblemDetail handleAccountConflict(AccountConflictException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Account conflict");
        return problem;
    }
}
