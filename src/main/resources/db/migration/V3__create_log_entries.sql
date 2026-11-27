CREATE TABLE log_entries (
                             id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                             user_id     BIGINT        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                             content     VARCHAR(1000) NOT NULL CHECK (length(trim(content)) > 0),
                             tag         VARCHAR(20)   CHECK (tag IN ('INTELLIGENCE', 'DILIGENCE', 'EXPRESSION', 'SOCIABILITY', 'FITNESS')),
                             created_at  TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE INDEX idx_log_entries_user_created ON log_entries (user_id, created_at DESC);