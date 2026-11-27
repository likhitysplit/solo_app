CREATE TABLE stat_progress (
                               id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                               user_id   BIGINT      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                               stat      VARCHAR(20) NOT NULL CHECK (stat IN ('INTELLIGENCE', 'DILIGENCE', 'EXPRESSION', 'SOCIABILITY', 'FITNESS')),
                               total_xp  INTEGER     NOT NULL DEFAULT 0 CHECK (total_xp >= 0),
                               UNIQUE (user_id, stat)
);