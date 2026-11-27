package io.github.likhitysplit.solo.auth;

import io.github.likhitysplit.solo.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;

public record AppUserPrincipal(
        Long id,
        String username,
        String email,
        String passwordHash,
        String timezone
) implements UserDetails {

    public static AppUserPrincipal from(User user) {
        return new AppUserPrincipal(user.getId(), user.getUsername(), user.getEmail(),
                user.getPasswordHash(), user.getTimezone());
    }

    public LocalDate today() {
        return LocalDate.now(ZoneId.of(timezone));
    }

    @Override public String getUsername() { return email; }
    @Override public String getPassword() { return passwordHash; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return List.of(); }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}