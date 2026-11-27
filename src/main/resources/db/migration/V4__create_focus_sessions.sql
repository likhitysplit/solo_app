CREATE TABLE focus_sessions (
                                id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                user_id          BIGINT      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                task_id          BIGINT      REFERENCES tasks(id) ON DELETE SET NULL,
                                planned_minutes  INTEGER     NOT NULL CHECK (planned_minutes BETWEEN 5 AND 120),
                                started_at       TIMESTAMPTZ NOT NULL,
                                ended_at         TIMESTAMPTZ,
                                xp_awarded       INTEGER     NOT NULL DEFAULT 0 CHECK (xp_awarded >= 0),
                                CHECK (ended_at IS NULL OR ended_at >= started_at)
);

CREATE UNIQUE INDEX uq_focus_one_active_per_user
    ON focus_sessions (user_id)
    WHERE ended_at IS NULL;