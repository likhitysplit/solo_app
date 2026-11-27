package io.github.likhitysplit.solo.auth.dto;

import jakarta.validation.constraints.*;

public record SignupRequest(
        @NotBlank(message = "Username is required")
        @Pattern(regexp = "^[A-Za-z0-9_]{3,30}$",
                message = "Username must be 3–30 letters, numbers, or underscores")
        String username,

        @NotBlank(message = "Email is required")
        @Email(message = "Email doesn't look valid")
        @Size(max = 254, message = "Email is too long")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 72, message = "Password must be 8–72 characters")
        String password,

        @NotBlank(message = "Timezone is required")
        String timezone
) { }