package io.github.likhitysplit.solo.auth;

import io.github.likhitysplit.solo.common.InvalidInputException;
import io.github.likhitysplit.solo.user.User;
import io.github.likhitysplit.solo.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User signup(String username, String email, String rawPassword, String timezone) {
        String cleanUsername = username.trim();
        String cleanEmail = email.trim().toLowerCase(Locale.ROOT);

        try {
            ZoneId.of(timezone);
        } catch (DateTimeException e) {
            throw new InvalidInputException("Unknown timezone: " + timezone);
        }

        if (users.existsByEmail(cleanEmail)) {
            throw new AccountConflictException("An account with that email already exists");
        }
        if (users.existsByUsername(cleanUsername)) {
            throw new AccountConflictException("That username is taken");
        }

        String hash = passwordEncoder.encode(rawPassword);
        return users.save(new User(cleanUsername, cleanEmail, hash, timezone));
    }
}