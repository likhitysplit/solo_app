package io.github.likhitysplit.solo.auth.dto;

import io.github.likhitysplit.solo.auth.AppUserPrincipal;
import io.github.likhitysplit.solo.user.User;

public record MeResponse(Long id, String username, String email, String timezone) {

    public static MeResponse from(User u) {
        return new MeResponse(u.getId(), u.getUsername(), u.getEmail(), u.getTimezone());
    }

    public static MeResponse from(AppUserPrincipal p) {
        return new MeResponse(p.id(), p.username(), p.email(), p.timezone());
    }
}