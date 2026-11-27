CREATE TABLE budget_results (
                                id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                user_id           BIGINT      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                period            VARCHAR(10) NOT NULL CHECK (period IN ('WEEKLY', 'MONTHLY')),
                                period_start      DATE        NOT NULL,
                                qualified         BOOLEAN     NOT NULL,
                                categories_met    INTEGER     NOT NULL CHECK (categories_met >= 0),
                                categories_total  INTEGER     NOT NULL CHECK (categories_total >= 0),
                                xp_awarded        INTEGER     NOT NULL DEFAULT 0 CHECK (xp_awarded >= 0),
                                evaluated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
                                CHECK (categories_met <= categories_total),
                                UNIQUE (user_id, period, period_start)
);