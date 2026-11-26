CREATE TABLE users (
                       id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                       username       VARCHAR(30)  NOT NULL UNIQUE,
                       email          VARCHAR(254) NOT NULL UNIQUE,
                       password_hash  VARCHAR(255) NOT NULL,
                       timezone       VARCHAR(64)  NOT NULL,
                       created_at     TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE tasks (
                       id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                       user_id        BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                       title          VARCHAR(200) NOT NULL,
                       type           VARCHAR(10)  NOT NULL CHECK (type IN ('ANCHOR', 'EXTRA')),
                       stat           VARCHAR(20)  NOT NULL CHECK (stat IN ('INTELLIGENCE', 'DILIGENCE', 'EXPRESSION', 'SOCIABILITY', 'FITNESS')),
                       base_xp        INTEGER      NOT NULL CHECK (base_xp BETWEEN 1 AND 20),
                       status         VARCHAR(10)  NOT NULL DEFAULT 'OPEN' CHECK (status IN ('OPEN', 'COMPLETED')),
                       scheduled_date DATE         NOT NULL,
                       original_date  DATE         NOT NULL,
                       completed_at   TIMESTAMPTZ,
                       CHECK ((status = 'COMPLETED') = (completed_at IS NOT NULL))
);

CREATE INDEX idx_tasks_user_date ON tasks (user_id, scheduled_date);